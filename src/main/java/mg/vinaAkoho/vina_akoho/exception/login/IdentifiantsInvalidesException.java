package mg.vinaAkoho.vina_akoho.exception.login;

/**
 * Levee quand l'email n'existe pas ou que le mot de passe ne correspond pas.
 * On utilise volontairement le meme message dans les deux cas
 * (email inconnu / mauvais mot de passe) pour ne pas indiquer à un attaquant
 * si un email existe en base ou non
 */
public class IdentifiantsInvalidesException extends RuntimeException {

    public IdentifiantsInvalidesException(String message) {
        super(message);
    }
}
