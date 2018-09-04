package cavemansspa.security

import grails.plugins.*
import io.cavemansspa.security.CavemansSpaOauthUserDetailsService
import io.cavemansspa.security.CavemansSpaRestOauthService
import io.cavemansspa.security.CavemansSpaSecurityEventListener
import io.cavemansspa.security.CavemansSpaUserDetailsService
import io.cavemansspa.security.UserPasswordEncoderListener

class CavemansspaSecurityGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.8 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Cavemansspa Security" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/cavemansspa-security"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() {
        { ->
            userPasswordEncoderListener(UserPasswordEncoderListener)
            userDetailsService(CavemansSpaUserDetailsService)
            cavemansSpaUserDetailsService(CavemansSpaUserDetailsService)
            oauthUserDetailsService(CavemansSpaOauthUserDetailsService) { bean ->
                bean.autowire = "byName"
            }
            
            cavemansSpaOauthUserDetailsService(CavemansSpaOauthUserDetailsService) { bean ->
                bean.autowire = "byName"
            }

            cavemansSpaSecurityEventListener(CavemansSpaSecurityEventListener) { bean ->
                bean.autowire = "byName"
            }

            cavemansSpaRestOauthService(CavemansSpaRestOauthService) { bean ->
                bean.autowire = "byName"
            }

        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
