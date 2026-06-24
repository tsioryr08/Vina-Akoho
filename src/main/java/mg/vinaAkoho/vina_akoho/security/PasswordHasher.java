package mg.vinaAkoho.vina_akoho.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Petite classe utilitaire (pas un bean Spring, juste des méthodes statiques)
 * pour hacher et vérifier les mots de passe avec l'algorithme BCrypt.
 *
 * Pourquoi BCrypt et pas autre chose ?
 * - On ne stocke JAMAIS un mot de passe en clair en base (RG01).
 * - BCrypt intègre un "salt" (valeur aléatoire) différent à chaque hachage,
 *   donc même si deux utilisateurs ont le même mot de passe, leur hash
 *   en base sera différent.
 * - Il est volontairement lent (configurable via "rounds"), ce qui rend
 *   les attaques par force brute beaucoup plus coûteuses.
 */
public class PasswordHasher {

    // Nombre de "rounds" de hachage. 10 est la valeur par défaut recommandée
    // (bon compromis sécurité / temps de calcul pour un projet comme celui-ci).
    private static final int ROUNDS = 10;

    private PasswordHasher() {
        // Classe utilitaire : on empêche l'instanciation
    }

    /**
     * Transforme un mot de passe en clair en un hash à stocker en base.
     * Exemple : "admin123" -> "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     */
    public static String hash(String motDePasseEnClair) {
        return BCrypt.hashpw(motDePasseEnClair, BCrypt.gensalt(ROUNDS));
    }

    /**
     * Vérifie qu'un mot de passe en clair correspond bien au hash stocké en base.
     * On ne peut jamais "déhacher" un hash : on rehache le mot de passe saisi
     * avec le même salt (extrait automatiquement du hash) et on compare.
     */
    public static boolean verifier(String motDePasseEnClair, String hashStocke) {
        return BCrypt.checkpw(motDePasseEnClair, hashStocke);
    }
}
