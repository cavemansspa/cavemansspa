package io.cavemansspa.security

import com.google.common.cache.CacheBuilder
import com.google.common.cache.LoadingCache
import grails.core.GrailsApplication
import grails.plugin.springsecurity.rest.authentication.RestAuthenticationEventPublisher
import grails.plugin.springsecurity.rest.oauth.OauthUserDetailsService
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.util.Holders
import grails.web.mapping.LinkGenerator
import groovy.util.logging.Slf4j
import io.cavemansspa.security.UserOauthProviderXref
import org.codehaus.groovy.runtime.InvokerHelper
import org.pac4j.core.client.IndirectClient
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.profile.CommonProfile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

@Slf4j
class CavemansSpaRestOauthService {

    static transactional = false

    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService
    GrailsApplication grailsApplication
    LinkGenerator grailsLinkGenerator
    CavemansSpaOauthUserDetailsService cavemansSpaOauthUserDetailsService
//            OauthUserDetailsService oauthUserDetailsService
    RestAuthenticationEventPublisher authenticationEventPublisher

    private transient LoadingCache<String, IndirectClient> clientCache = CacheBuilder.newBuilder().<String, IndirectClient> build { String provider ->
        log.debug "Creating OAuth client for provider: ${provider}"
        Map<String, ?> providerConfig = grailsApplication.config.grails.plugin.springsecurity.rest.oauth."${provider}"
        def clientClass = providerConfig.client
        if (clientClass instanceof CharSequence) clientClass = Class.forName(clientClass as String, true, Holders.grailsApplication.classLoader)
        IndirectClient client = (clientClass as Class<? extends IndirectClient>).newInstance()

        Map<String, ?> clientConfig = [:]
        clientConfig.putAll providerConfig
        clientConfig.remove 'client'

        String callbackUrl = grailsLinkGenerator.link controller: 'restOauth', action: 'callback', params: [provider: provider], mapping: 'oauth', absolute: true
        log.debug "Callback URL is: ${callbackUrl}"
        clientConfig.callbackUrl = callbackUrl

        InvokerHelper.setProperties client, clientConfig

        client
    }

    IndirectClient getClient(String provider) {
        clientCache.get provider
    }

    CommonProfile getProfile(String provider, WebContext context) {
        IndirectClient client = getClient(provider)
        Credentials credentials = client.getCredentials context

        log.debug "Querying provider to fetch User ID"
        client.getUserProfile credentials, context
    }

    CavemansSpaOauthUser getOauthUser(String provider, CommonProfile profile) {
        def providerConfig = grailsApplication.config.grails.plugin.springsecurity.rest.oauth."${provider}"
        List defaultRoles = providerConfig.defaultRoles.collect { new SimpleGrantedAuthority(it) }
        //oauthUserDetailsService.loadUserByUserProfile(profile, defaultRoles) as CavemansSpaOauthUser
        cavemansSpaOauthUserDetailsService.loadUserByUserProfile(profile, defaultRoles)
    }

    String storeAuthentication(String provider, WebContext context) {
        CommonProfile profile = getProfile(provider, context)
        log.debug "User's ID: ${profile.id}"

        CavemansSpaOauthUser userDetails = getOauthUser(provider, profile)

        User newUser = null
        if (!userDetails.id) {
            newUser = new User(userDetails.properties)
            log.info("Storing new oauth user [provider: ${provider}, email: ${newUser.email}")
            UserOauthProviderXref.OauthProvider oauthProvider = UserOauthProviderXref.asOauthProvider(provider)
            println "provider: ${oauthProvider}"
            newUser.addToUserOauthProviders(new UserOauthProviderXref(oauthProvider: oauthProvider, externalId: profile.id))
            newUser.save(flush: true, failOnError: true)
            Role userRole = Role.findByAuthority('ROLE_USER')
            UserRole.create(newUser, userRole, true)
        } else {
            User user = User.get(userDetails.id as Long)
            if (!UserOauthProviderXref.countByUserAndExternalId(user, profile.id)) {
                UserOauthProviderXref.OauthProvider oauthProvider = UserOauthProviderXref.asOauthProvider(provider)
                user.addToUserOauthProviders(new UserOauthProviderXref(oauthProvider: oauthProvider, externalId: profile.id))
            }
        }

        AccessToken accessToken = tokenGenerator.generateAccessToken(userDetails)
        log.debug "Generated REST authentication token: ${accessToken}"

        log.debug "Storing token on the token storage"
        tokenStorageService.storeToken(accessToken.accessToken, userDetails)

        authenticationEventPublisher.publishTokenCreation(accessToken)

        //User authenticatedUser = userDetails.id ? User.get(userDetails.id as Long) : newUser
        if (newUser) {
            newUser.lastAuthenticated = new Date()
            newUser.save(flush: true, failOnError: true)
        }

        SecurityContextHolder.context.setAuthentication(accessToken)

        return accessToken.accessToken
    }
}
