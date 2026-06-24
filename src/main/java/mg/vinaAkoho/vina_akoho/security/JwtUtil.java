package mg.vinaAkoho.vina_akoho.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import mg.vinaAkoho.vina_akoho.exception.login.TokenInvalideException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Classe responsable de :
 *  1) générer un token JWT après une connexion réussie,
 *  2) vérifier/décoder un token reçu sur les requêtes suivantes.
 *
 * Un JWT est composé de 3 parties séparées par des points :
 *  header.payload.signature
 * Le "payload" contient nos informations (id employé, rôle, date d'expiration).
 * La "signature" est calculée avec une clé secrète : si quelqu'un modifie
 * le contenu du token sans connaître la clé, la signature ne correspondra
 * plus et le token sera rejeté. C'est ce qui rend le JWT infalsifiable
 * sans avoir besoin de stocker quoi que ce soit côté serveur (stateless).
 */
@Component
public class JwtUtil {

    // Clé secrète utilisée pour signer les tokens.
    // Définie dans application.properties (jwt.secret) — ne JAMAIS la commit en clair
    // dans un vrai projet en production, mais c'est acceptable pour ce projet étudiant.
    @Value("${jwt.secret}")
    private String secret;

    // Durée de validité du token, en millisecondes (ici 10h par défaut).
    @Value("${jwt.expiration-ms:36000000}")
    private long expirationMs;

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NOM = "nom";

    /**
     * Génère un token JWT contenant l'id de l'employé (subject), son rôle
     * et son nom, avec une date d'expiration.
     */
    public String genererToken(Integer idEmploye, String role, String nom) {
        Algorithm algorithme = Algorithm.HMAC256(secret);
        Date maintenant = new Date();
        Date expiration = new Date(maintenant.getTime() + expirationMs);

        return JWT.create()
                .withSubject(String.valueOf(idEmploye))
                .withClaim(CLAIM_ROLE, role)
                .withClaim(CLAIM_NOM, nom)
                .withIssuedAt(maintenant)
                .withExpiresAt(expiration)
                .sign(algorithme);
    }

    /**
     * Vérifie la signature et l'expiration du token, puis le décode.
     * Lève une TokenInvalideException si quoi que ce soit ne va pas
     * (signature invalide, token expiré, format incorrect...).
     */
    public DecodedJWT verifierEtDecoder(String token) {
        try {
            Algorithm algorithme = Algorithm.HMAC256(secret);
            return JWT.require(algorithme)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalideException("Token invalide ou expiré");
        }
    }

    public Integer extraireIdEmploye(DecodedJWT decoded) {
        return Integer.valueOf(decoded.getSubject());
    }

    public String extraireRole(DecodedJWT decoded) {
        return decoded.getClaim(CLAIM_ROLE).asString();
    }
}
