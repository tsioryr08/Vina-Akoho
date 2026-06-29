package mg.vinaAkoho.vina_akoho.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private static final int ROUNDS = 10;

    private PasswordHasher() {
    }

    public static String hash(String motDePasseEnClair) {
        return BCrypt.hashpw(motDePasseEnClair, BCrypt.gensalt(ROUNDS));
    }

    public static boolean verifier(String motDePasseEnClair, String hashStocke) {
        return BCrypt.checkpw(motDePasseEnClair, hashStocke);
    }
}
