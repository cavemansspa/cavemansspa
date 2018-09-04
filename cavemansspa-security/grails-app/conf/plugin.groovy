grails.plugin.springsecurity.rest.token.storage.jwt.secret = 'd6e2ec20e11141438605ee29c3dc1189'
grails.plugin.springsecurity.rest.token.validation.enableAnonymousAccess = true
grails.plugin.springsecurity.useSecurityEventListener = true

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'io.cavemansspa.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'io.cavemansspa.security.UserRole'
grails.plugin.springsecurity.authority.className = 'io.cavemansspa.security.Role'

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/', access: ['permitAll']],
        [pattern: '/error', access: ['permitAll']],
        [pattern: '/index', access: ['permitAll']],

        // Enable Spring Security stateful login endpoints.
        // Optional, remove if you don't need stateful auth.
        [pattern: '/login/**', access: ['permitAll']],
        [pattern: '/logout', access: ['permitAll']],
        [pattern: '/logout/**', access: ['permitAll']],

]

grails.plugin.springsecurity.filterChain.chainMap = [

        // Enable access to static assets from asset-pipeline.
        [pattern: '/assets/**', filters: 'none'],

        //Stateless chain -- we will use /api for SPA api based stateless requests.
        [
                pattern: '/api/**',
                // Let anonymous access through.
                filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
        ],

        //Traditional, stateful chain
        [
                pattern: '/**',
                filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
        ]
]

