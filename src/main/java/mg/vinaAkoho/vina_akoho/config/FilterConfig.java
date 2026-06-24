package mg.vinaAkoho.vina_akoho.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mg.vinaAkoho.vina_akoho.security.JwtFilter;

/**
 * Enregistre explicitement JwtFilter comme filtre Servlet sur toutes
 * les routes de l'application ("/*").
 *
 * Remarque : comme JwtFilter est déjà annote @Component, Spring Boot
 * l'enregistrerait probablement déjà automatiquement. On le déclare
 * ici explicitement pour deux raisons :
 *  - Avoir un contrôle total et visible sur quelles URLs sont filtrées
 *  - Définir un "order" precis si on ajoute d'autres filtres plus tard
 */
@Configuration
public class FilterConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public FilterConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
