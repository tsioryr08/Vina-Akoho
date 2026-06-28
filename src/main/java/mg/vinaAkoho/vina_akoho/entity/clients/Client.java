package mg.vinaAkoho.vina_akoho.entity.clients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "id_localite", length = 100)
    private String idLocalite;

    @Column(name = "id_zone_livraison", length = 100)
    private String idZoneLivraison;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "id_service", nullable = false)
    private ServiceClient service;

    @ManyToOne
    @JoinColumn(name = "id_typeclient", nullable = false)
    private TypeClient typeClient;

    @Column(name = "taille_cheptel")
    private Integer tailleCheptel;

    @Column(name = "is_actif")
    private Boolean actif;

    @Column(name = "est_supprimer", nullable = false)
    private Boolean estSupprimer = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Client() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (dateInscription == null) {
            dateInscription = LocalDate.now();
        }
        if (actif == null) {
            actif = true;
        }
        if (estSupprimer == null) {
            estSupprimer = false;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Boolean getEstSupprimer() {
        return estSupprimer;
    }

    public void setEstSupprimer(Boolean estSupprimer) {
        this.estSupprimer = estSupprimer;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getIdLocalite() {
        return idLocalite;
    }

    public void setIdLocalite(String idLocalite) {
        this.idLocalite = idLocalite;
    }

    public String getIdZoneLivraison() {
        return idZoneLivraison;
    }

    public void setIdZoneLivraison(String idZoneLivraison) {
        this.idZoneLivraison = idZoneLivraison;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getIdService() {
        return service == null ? null : service.getId();
    }

    public void setIdService(Integer idService) {
        if (idService == null) {
            this.service = null;
            return;
        }
        ServiceClient serviceClient = new ServiceClient();
        serviceClient.setId(idService);
        this.service = serviceClient;
    }

    public ServiceClient getService() {
        return service;
    }

    public void setService(ServiceClient service) {
        this.service = service;
    }

    public Integer getIdTypeClient() {
        return typeClient == null ? null : typeClient.getId();
    }

    public void setIdTypeClient(Integer idTypeClient) {
        if (idTypeClient == null) {
            this.typeClient = null;
            return;
        }
        TypeClient typeClient = new TypeClient();
        typeClient.setId(idTypeClient);
        this.typeClient = typeClient;
    }

    public TypeClient getTypeClient() {
        return typeClient;
    }

    public void setTypeClient(TypeClient typeClient) {
        this.typeClient = typeClient;
    }

    public Integer getTailleCheptel() {
        return tailleCheptel;
    }

    public void setTailleCheptel(Integer tailleCheptel) {
        this.tailleCheptel = tailleCheptel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
