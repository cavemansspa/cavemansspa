package io.cavemansspa.security


import grails.plugin.springsecurity.rest.oauth.OauthUserDetailsService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.UserProfile
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsChecker
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

//tag::class[]
/**
 * Builds an {@link grails.plugin.springsecurity.rest.oauth.OauthUser}. Delegates to the default {@link UserDetailsService#loadUserByUsername(java.lang.String)}
 * where the username passed is {@link UserProfile#getId()}.
 *
 * If the user is not found, it will create a new one with the the default roles.
 */
@Slf4j
@CompileStatic
class CavemansSpaOauthUserDetailsService implements OauthUserDetailsService {

    @Delegate
    CavemansSpaUserDetailsService cavemansSpaUserDetailsService

    UserDetailsChecker preAuthenticationChecks

    CavemansSpaOauthUser loadUserByUserProfile(CommonProfile userProfile, Collection<GrantedAuthority> defaultRoles)
            throws UsernameNotFoundException {
        UserDetails userDetails
        CavemansSpaUser cavemansSpaUser
        CavemansSpaOauthUser oauthUser

        try {
            log.debug "Trying to fetch user details for user profile: ${userProfile}"
            cavemansSpaUser = cavemansSpaUserDetailsService.loadUserByUsername(userProfile.email) as CavemansSpaUser

            log.debug "Checking user details with ${preAuthenticationChecks.class.name}"
            preAuthenticationChecks?.check(cavemansSpaUser as UserDetails)

            //Collection<GrantedAuthority> allRoles = userDetails.authorities + defaultRoles
            def allRoles = cavemansSpaUser.authorities + defaultRoles
            oauthUser = new CavemansSpaOauthUser(cavemansSpaUser.id as Long, cavemansSpaUser.username, cavemansSpaUser.password, allRoles, userProfile)
        } catch (UsernameNotFoundException ignored) {
            log.debug "User not found. Creating a new one with default roles: ${defaultRoles}"
            oauthUser = new CavemansSpaOauthUser(userProfile.email, 'N/A', defaultRoles, userProfile)
        }

        return oauthUser
    }

}
//end::class[]