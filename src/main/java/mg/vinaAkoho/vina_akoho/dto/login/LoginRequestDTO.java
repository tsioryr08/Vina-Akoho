package mg.vinaAkoho.vina_akoho.dto.login;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO reçu par le controller quand un utilisateur tente de se connecter.
 * On valide que les champs ne sont pas vides AVANT même d'aller chercher
 * en base de données.
 */
public class LoginRequestDTO {

    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String mdp;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String email, String mdp) {
        this.email = email;
        this.mdp = mdp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}
