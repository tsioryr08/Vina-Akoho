package mg.vinaAkoho.vina_akoho.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.interfaces.DecodedJWT;

import mg.vinaAkoho.vina_akoho.exception.login.TokenInvalideException;

/**
 * Tests unitaires de JwtUtil.
 *
 * JwtUtil utilise normalement @Value("${jwt.secret}") pour récupérer
 * sa clé secrète depuis application.properties, ce qui suppose que
 * Spring ait démarré. Pour un test unitaire (rapide, isole, sans
 * demarrer toute l'application), on utilise ReflectionTestUtils.setField
 * pour injecter directement une valeur de test dans ces champs prives.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "cle-secrete-de-test");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 36000000L);
    }

    @Test
    void genererToken_doitProduireUneChaineNonVide() {
        String token = jwtUtil.genererToken(1, "Administrateur", "Ny Antema");

        assertNotNull(token);
        assertFalse(token.isBlank());
        // Un JWT a toujours 3 parties séparées par des points : header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void verifierEtDecoder_doitRetrouverLesInfosDuToken() {
        String token = jwtUtil.genererToken(42, "Comptable", "Tsiory");

        DecodedJWT decoded = jwtUtil.verifierEtDecoder(token);

        assertEquals(42, jwtUtil.extraireIdEmploye(decoded));
        assertEquals("Comptable", jwtUtil.extraireRole(decoded));
    }

    @Test
    void verifierEtDecoder_doitRejeterUnTokenInvalide() {
        String tokenInvalide = "ceci.nest.pasUnTokenValide";

        assertThrows(TokenInvalideException.class,
                () -> jwtUtil.verifierEtDecoder(tokenInvalide));
    }

    @Test
    void verifierEtDecoder_doitRejeterUnTokenSigneAvecUneAutreCle() {
        // On génère un token avec une clé secrète différente :
        // un peu comme si quelqu'un essayait de fabriquer un faux token.
        JwtUtil autreJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(autreJwtUtil, "secret", "une-autre-cle-secrete");
        ReflectionTestUtils.setField(autreJwtUtil, "expirationMs", 36000000L);

        String tokenAvecMauvaiseCle = autreJwtUtil.genererToken(1, "Administrateur", "Test");

        assertThrows(TokenInvalideException.class,
                () -> jwtUtil.verifierEtDecoder(tokenAvecMauvaiseCle));
    }

    @Test
    void verifierEtDecoder_doitRejeterUnTokenExpire() {
        // On crée un JwtUtil dont l'expiration est négative :
        // le token sera donc déjà expiré au moment même de sa création.
        JwtUtil jwtUtilExpiration = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtilExpiration, "secret", "cle-secrete-de-test");
        ReflectionTestUtils.setField(jwtUtilExpiration, "expirationMs", -1000L);

        String tokenExpire = jwtUtilExpiration.genererToken(1, "Administrateur", "Test");

        assertThrows(TokenInvalideException.class,
                () -> jwtUtil.verifierEtDecoder(tokenExpire));
    }
}
