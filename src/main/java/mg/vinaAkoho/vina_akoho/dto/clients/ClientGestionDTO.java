package mg.vinaAkoho.vina_akoho.dto.clients;

import mg.vinaAkoho.vina_akoho.entity.clients.Client;

public class ClientGestionDTO {

    private Integer id;
    private String nom;
    private String prenom;
    private String dateInscription;
    private Boolean isActif;
    private String numeroTelephone;
    private String adresse;
    private String idLocalite;
    private String idZoneLivraison;
    private String notes;
    private Integer idService;
    private String service;
    private Integer idTypeClient;
    private String typeClient;
    private Integer tailleCheptel;
    private Boolean estSupprimer;
    private String createdAt;
    private String updatedAt;

    public ClientGestionDTO(Client client) {
        this.id = client.getId();
        this.nom = client.getNom();
        this.prenom = client.getPrenom();
        this.dateInscription = client.getDateInscription() == null ? null : client.getDateInscription().toString();
        this.isActif = client.getActif();
        this.numeroTelephone = client.getNumeroTelephone();
        this.adresse = client.getAdresse();
        this.idLocalite = client.getIdLocalite();
        this.idZoneLivraison = client.getIdZoneLivraison();
        this.notes = client.getNotes();
        this.idService = client.getIdService();
        this.service = client.getService() == null ? null : client.getService().getLibelle();
        this.idTypeClient = client.getIdTypeClient();
        this.typeClient = client.getTypeClient() == null ? null : client.getTypeClient().getLibelle();
        this.tailleCheptel = client.getTailleCheptel();
        this.estSupprimer = client.getEstSupprimer();
        this.createdAt = client.getCreatedAt() == null ? null : client.getCreatedAt().toString();
        this.updatedAt = client.getUpdatedAt() == null ? null : client.getUpdatedAt().toString();
    }

    public Integer getId() {
        return id;
    }

    public String getNom() { return nom; }

    public String getPrenom() { return prenom; }

    public String getDateInscription() { return dateInscription; }

    public Boolean getIsActif() { return isActif; }

    public String getNumeroTelephone() { return numeroTelephone; }

    public String getAdresse() { return adresse; }

    public String getIdLocalite() { return idLocalite; }

    public String getIdZoneLivraison() { return idZoneLivraison; }

    public String getNotes() { return notes; }

    public Integer getIdService() { return idService; }

    public String getService() { return service; }

    public Integer getIdTypeClient() { return idTypeClient; }

    public String getTypeClient() { return typeClient; }

    public Integer getTailleCheptel() { return tailleCheptel; }

    public Boolean getEstSupprimer() {
        return estSupprimer;
    }

    public String getCreatedAt() { return createdAt; }

    public String getUpdatedAt() { return updatedAt; }
}
