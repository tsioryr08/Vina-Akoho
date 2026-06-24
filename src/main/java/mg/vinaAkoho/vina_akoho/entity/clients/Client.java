package mg.vinaAkoho.vina_akoho.entity.clients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "id_service", nullable = false)
    private Integer idService;

    @Column(name = "id_typeclient", nullable = false)
    private Integer idTypeClient;

    @Column(name = "taille_cheptel")
    private Integer tailleCheptel;

    @Column(name = "is_actif")
    private Boolean actif;

    public Client() {
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
        return idService;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public Integer getIdTypeClient() {
        return idTypeClient;
    }

    public void setIdTypeClient(Integer idTypeClient) {
        this.idTypeClient = idTypeClient;
    }

    public Integer getTailleCheptel() {
        return tailleCheptel;
    }

    public void setTailleCheptel(Integer tailleCheptel) {
        this.tailleCheptel = tailleCheptel;
    }
}
