package io.cavemansspa.security

import grails.plugin.springsecurity.rest.RestTokenCreationEvent
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationListener

@Slf4j
class CavemansSpaSecurityEventListener
        implements ApplicationListener<RestTokenCreationEvent> {

    void onApplicationEvent(RestTokenCreationEvent event) {
        log.info("onApplicationEvent() -- ${event}")
        // The access token is a delegate of the event, so you have access to an instance of `grails.plugin.springsecurity.rest.token.AccessToken`
        if(event.principal.id) {
            User.withNewSession {
                log.info("onApplicationEvent() -- updating lastAuthenticated for ${event.principal}")
                User user = User.get(event.principal.id as Long)
                user.lastAuthenticated = new Date()
                user.save(flush: true)
            }
        }
    }
}