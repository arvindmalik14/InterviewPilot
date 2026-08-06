package com.malik.InterviewPilot.config;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Starts H2's own standalone web console server in-process, on its own port.
 *
 * Spring Boot dropped the built-in H2ConsoleAutoConfiguration as of Boot 4
 * (org.springframework.boot.autoconfigure.h2 no longer exists) — and mounting H2's console as
 * a servlet inside Tomcat isn't an option either: H2 2.4.240's bundled WebServlet still
 * implements the old javax.servlet.http.HttpServlet, not jakarta.servlet, so it can't be
 * registered in a Jakarta EE 9+/Servlet 5+ container (Tomcat 10+) at all. H2's own
 * Server.createWebServer(...) sidesteps that entirely — it's H2's own minimal HTTP listener,
 * not built on the servlet API — and since it runs in this same JVM, it can still see the
 * in-memory jdbc:h2:mem:interviewpilot database. No -webAllowOthers, so (matching the old
 * auto-configuration's effective default) it only accepts connections from localhost.
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.h2.console", name = "enabled", havingValue = "true")
public class H2ConsoleConfig {

    @Value("${app.h2-console.port:8082}")
    private int port;

    @Bean(destroyMethod = "stop")
    public Server h2WebServer() throws java.sql.SQLException {
        return Server.createWebServer("-webPort", String.valueOf(port)).start();
    }
}
