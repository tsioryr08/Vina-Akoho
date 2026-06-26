package mg.vinaAkoho.vina_akoho.dto.clients;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ClientRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres")
    private String prenom;

    private LocalDate dateInscription;

    private Boolean isActif;

    @NotBlank(message = "Le numero telephone est obligatoire")
    @Size(max = 20, message = "Le numero telephone ne doit pas depasser 20 caracteres")
    private String numeroTelephone;

    private String adresse;

    @Size(max = 100, message = "La localite ne doit pas depasser 100 caracteres")
    private String idLocalite;

    @Size(max = 100, message = "La zone de livraison ne doit pas depasser 100 caracteres")
    private String idZoneLivraison;

    private String notes;

    @NotNull(message = "Le service est obligatoire")
    @Positive(message = "Le service doit etre positif")
    private Integer idService;

    @NotNull(message = "Le type de client est obligatoire")
    @Positive(message = "Le type de client doit etre positif")
    private Integer idTypeClient;

    @PositiveOrZero(message = "La taille du cheptel doit etre positive ou egale a zero")
    private Integer tailleCheptel;

    public String getNom() { return nom; }

    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }

    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateInscription() { return dateInscription; }

    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }

    public Boolean getIsActif() { return isActif; }

    public void setIsActif(Boolean actif) { isActif = actif; }

    public String getNumeroTelephone() { return numeroTelephone; }

    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }

    public String getAdresse() { return adresse; }

    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getIdLocalite() { return idLocalite; }

    public void setIdLocalite(String idLocalite) { this.idLocalite = idLocalite; }

    public String getIdZoneLivraison() { return idZoneLivraison; }

    public void setIdZoneLivraison(String idZoneLivraison) { this.idZoneLivraison = idZoneLivraison; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public Integer getIdService() { return idService; }

    public void setIdService(Integer idService) { this.idService = idService; }

    public Integer getIdTypeClient() { return idTypeClient; }

    public void setIdTypeClient(Integer idTypeClient) { this.idTypeClient = idTypeClient; }

    public Integer getTailleCheptel() { return tailleCheptel; }

    public void setTailleCheptel(Integer tailleCheptel) { this.tailleCheptel = tailleCheptel; }
}
