package mg.vinaAkoho.vina_akoho.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de PasswordHasher.
 * Pas besoin de Mockito ici : ce sont des méthodes statiques pures,
 * sans dépendance externe (pas de base de données, pas de Spring).
 */
class PasswordHasherTest {

    @Test
    void hash_doitProduireUnHashDifferentDuMotDePasseEnClair() {
        String motDePasse = "admin123";

        String hash = PasswordHasher.hash(motDePasse);

        assertNotNull(hash);
        assertNotEquals(motDePasse, hash);
    }

    @Test
    void hash_doitProduireDesHashsDifferentsPourLeMemeMotDePasse() {
        // BCrypt utilise un "salt" aléatoire à chaque appel : deux hash
        // du même mot de passe ne doivent JAMAIS être identiques.
        String motDePasse = "admin123";

        String hash1 = PasswordHasher.hash(motDePasse);
        String hash2 = PasswordHasher.hash(motDePasse);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void verifier_doitRenvoyerTrueSiLeMotDePasseCorrespondAuHash() {
        String motDePasse = "admin123";
        String hash = PasswordHasher.hash(motDePasse);

        boolean resultat = PasswordHasher.verifier(motDePasse, hash);

        assertTrue(resultat);
    }

    @Test
    void verifier_doitRenvoyerFalseSiLeMotDePasseNeCorrespondPas() {
        String hash = PasswordHasher.hash("admin123");

        boolean resultat = PasswordHasher.verifier("mauvaisMotDePasse", hash);

        assertFalse(resultat);
    }
}
