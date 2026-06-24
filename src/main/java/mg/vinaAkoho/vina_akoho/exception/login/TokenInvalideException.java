package mg.vinaAkoho.vina_akoho.exception.login;

/**
 * Levee par le filtre JWT quand le token est absent, malforme,
 * expire, ou que sa signature ne correspond pas a notre cle secrete.
 */
public class TokenInvalideException extends RuntimeException {

    public TokenInvalideException(String message) {
        super(message);
    }
}
