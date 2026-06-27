package mg.vinaAkoho.vina_akoho.dto.login;

public class LoginResponseDTO {

    private String token;
    private Integer idEmploye;
    private String nom;
    private String prenom;
    private String email;
    private String role;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, Integer idEmploye, String nom, String prenom, String email, String role) {
        this.token = token;
        this.idEmploye = idEmploye;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
