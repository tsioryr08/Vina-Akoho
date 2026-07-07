package mg.vinaAkoho.vina_akoho.exception.admin;

public class EmailDejaUtiliseException extends RuntimeException {
    public EmailDejaUtiliseException(String email) {
        super("L'email '" + email + "' est déjà utilisé par un autre compte");
    }
}