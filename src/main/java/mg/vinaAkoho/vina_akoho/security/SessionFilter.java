package mg.vinaAkoho.vina_akoho.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SessionFilter extends OncePerRequestFilter {

    private static final List<String> ROUTES_PUBLIQUES = List.of(
            "/api/login",
            "/",
            "/login",
            "/index",
            "/index.html",
            "/index.php",
            "/assets/",
            "/css/",
            "/js/",
            "/images/",
            "/webjars/"
    );

    public static final String ATTRIBUT_ID_EMPLOYE = "idEmploye";
    public static final String ATTRIBUT_ROLE = "role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String chemin = request.getRequestURI();

        if (estRoutePublique(chemin)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(ATTRIBUT_ID_EMPLOYE) == null) {
            if (estPageHtml(chemin)) {
                response.sendRedirect("/");
            } else {
                envoyerErreur401(response, "Authentification requise. Session expirée ou inexistante.");
            }
            return;
        }

        request.setAttribute(ATTRIBUT_ID_EMPLOYE, session.getAttribute(ATTRIBUT_ID_EMPLOYE));
        request.setAttribute(ATTRIBUT_ROLE, session.getAttribute(ATTRIBUT_ROLE));

        filterChain.doFilter(request, response);
    }

    private boolean estRoutePublique(String chemin) {
        return ROUTES_PUBLIQUES.stream().anyMatch(route ->
                route.equals("/") ? route.equals(chemin) : chemin.startsWith(route)
        );
    }

    private boolean estPageHtml(String chemin) {
        return !chemin.startsWith("/api/");
    }

    private void envoyerErreur401(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"success\": false, \"message\": \"%s\", \"data\": null}",
                message
        ));
    }
}
