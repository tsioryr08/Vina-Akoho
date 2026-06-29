package mg.vinaAkoho.vina_akoho.dto.clients;

import mg.vinaAkoho.vina_akoho.entity.clients.Client;

public class ClientResumeDTO {

    private Integer id;
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private String adresse;
    private String idLocalite;
    private String idZoneLivraison;
    private String notes;
    private Integer tailleCheptel;

    public ClientResumeDTO(Client client) {
        this.id = client.getId();
        this.nom = client.getNom();
        this.prenom = client.getPrenom();
        this.numeroTelephone = client.getNumeroTelephone();
        this.adresse = client.getAdresse();
        this.idLocalite = client.getIdLocalite();
        this.idZoneLivraison = client.getIdZoneLivraison();
        this.notes = client.getNotes();
        this.tailleCheptel = client.getTailleCheptel();
    }

    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getIdLocalite() {
        return idLocalite;
    }

    public String getIdZoneLivraison() {
        return idZoneLivraison;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getTailleCheptel() {
        return tailleCheptel;
    }
}
