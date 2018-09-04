package io.cavemansspa.security

class UserOauthProviderXref {

    User user
    OauthProvider oauthProvider
    String externalId

    Date dateCreated
    Date lastUpdated

    static constraints = {
    }

    static enum OauthProvider {
        FACEBOOK,
        GOOGLE
    }

    static OauthProvider asOauthProvider(String provider) {
        OauthProvider.values().find { OauthProvider it ->
            return it.name() == provider.toUpperCase()
        }
    }
}
