package mg.vinaAkoho.vina_akoho.exception.admin;

public class EmployeNotFoundException extends RuntimeException {
    public EmployeNotFoundException(Integer id) {
        super("Employé #" + id + " introuvable");
    }
}