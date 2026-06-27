package mg.vinaAkoho.vina_akoho.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mg.vinaAkoho.vina_akoho.security.SessionFilter;

@Configuration
public class FilterConfig {

    private final SessionFilter sessionFilter;

    @Autowired
    public FilterConfig(SessionFilter sessionFilter) {
        this.sessionFilter = sessionFilter;
    }

    @Bean
    public FilterRegistrationBean<SessionFilter> sessionFilterRegistration() {
        FilterRegistrationBean<SessionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(sessionFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
