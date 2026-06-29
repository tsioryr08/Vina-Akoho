package mg.vinaAkoho.vina_akoho.dto.login;

public class LoginRequestDTO {

    private String email;
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
