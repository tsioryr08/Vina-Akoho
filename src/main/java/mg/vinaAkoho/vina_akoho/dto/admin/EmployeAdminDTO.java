package mg.vinaAkoho.vina_akoho.dto.admin;

import java.time.LocalDateTime;

public class EmployeAdminDTO {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String contact;
    private String role;
    private Boolean actif;
    private LocalDateTime derniereConnexion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public LocalDateTime getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(LocalDateTime derniereConnexion) { this.derniereConnexion = derniereConnexion; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}