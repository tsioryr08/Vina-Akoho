package mg.vinaAkoho.vina_akoho.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * C'est le "filtre user" 
 *
 * Un Filter en Java/Servlet s'execute avant que la requête n'atteigne le
 * Controller. Ici, son rôle est de :
 *  - Laisser passer librement les routes publiques (ex: /api/login)
 *  - Pour toutes les autres routes, lire l'en-tete "Authorization: Bearer <token>"
 *  - Verifier le token avec JwtUtil
 *  - Si le token est valide, mettre l'id de l'employe et son rôle dans
 *     des attributs de la requête (request.setAttribute), que les
 *     controllers peuvent ensuite recuperer
 *  - Si le token est absent/invalide, couper la requête immediatement
 *     avec une reponse 401, sans même laisser le Controller s'executer
 *
 * OncePerRequestFilter garantit que le filtre ne s'exécute qu'une seule
 * fois par requête (utile avec certains forwards internes de Spring).
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Routes accessibles sans être connecte.
    private static final List<String> ROUTES_PUBLIQUES = List.of(
            "/api/login"
    );

    public static final String ATTRIBUT_ID_EMPLOYE = "idEmploye";
    public static final String ATTRIBUT_ROLE = "role";

    @Autowired
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String chemin = request.getRequestURI();

        // 1. Routes publiques : on laisse passer sans verification
        if (estRoutePublique(chemin)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Lecture de l'en-tete Authorization
        String enTeteAuth = request.getHeader("Authorization");

        if (enTeteAuth == null || !enTeteAuth.startsWith("Bearer ")) {
            envoyerErreur401(response, "Authentification requise. En-tête Authorization manquant.");
            return;
        }

        String token = enTeteAuth.substring(7); // enleve "Bearer "

        // 3. Vérification du token
        try {
            DecodedJWT decoded = jwtUtil.verifierEtDecoder(token);

            // 4. On rend disponibles l'id et le rôle pour les controllers suivants
            request.setAttribute(ATTRIBUT_ID_EMPLOYE, jwtUtil.extraireIdEmploye(decoded));
            request.setAttribute(ATTRIBUT_ROLE, jwtUtil.extraireRole(decoded));

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            envoyerErreur401(response, "Token invalide ou expiré");
        }
    }

    private boolean estRoutePublique(String chemin) {
        return ROUTES_PUBLIQUES.stream().anyMatch(chemin::startsWith);
    }

    /**
     * On ecrit directement la reponse JSON ici (au format ApiResponse standard)
     * car le GlobalExceptionHandler ne peut PAS intercepter les erreurs levees
     * dans un Filter : les filtres s'executent avant que Spring MVC ne prenne
     * la main, donc avant le mecanisme @RestControllerAdvice.
     */
    private void envoyerErreur401(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = String.format(
                "{\"success\": false, \"message\": \"%s\", \"data\": null}",
                message
        );
        response.getWriter().write(json);
    }
}
