package io.cavemansspa.security

import grails.events.annotation.gorm.Listener
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
@Slf4j
class UserPasswordEncoderListener {

    @Autowired
    SpringSecurityService springSecurityService

    @Listener(User)
    void onPreInsertEvent(PreInsertEvent event) {
        encodePasswordForEvent(event)
    }

    @Listener(User)
    void onPreUpdateEvent(PreUpdateEvent event) {
        User u = event.entityObject as User
        if (u.isDirty('password')) encodePasswordForEvent(event, u)
    }

    private void encodePasswordForEvent(AbstractPersistenceEvent event, User user = event.entityObject as User) {
        if (user.password && user.password != 'N/A') {
            log.info("Encoding password for user: ${user.email}")
            event.getEntityAccess().setProperty('password', encodePassword(user.password))
//            user.setProperty('password', encodePassword(user.password))
        }
    }

    private String encodePassword(String password) {
        springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
