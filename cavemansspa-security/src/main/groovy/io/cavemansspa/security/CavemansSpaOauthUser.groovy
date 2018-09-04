package io.cavemansspa.security

import grails.plugin.springsecurity.rest.oauth.OauthUser
import groovy.transform.CompileStatic
import org.pac4j.core.profile.CommonProfile
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.SpringSecurityCoreVersion

@CompileStatic
class CavemansSpaOauthUser extends OauthUser implements Serializable {


    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID

    CommonProfile userProfile

    final id
    final String email

    CavemansSpaOauthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities)
    }

    CavemansSpaOauthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, CommonProfile userProfile) {
        super(username, password, authorities)
        this.userProfile = userProfile

        this.email = userProfile.email
    }

    CavemansSpaOauthUser(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, CommonProfile userProfile) {
        super(username, password, authorities)
        this.userProfile = userProfile

        this.id = id
        this.email = userProfile.email
    }

}
