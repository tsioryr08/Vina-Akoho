package mg.vinaAkoho.vina_akoho.dto.clients;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ClientConnexionDTO {

    @Positive(message = "L'identifiant client doit etre positif")
    private Integer idClient;

    @NotBlank(message = "Le numero telephone est obligatoire")
    private String numeroTelephone;

    public ClientConnexionDTO() {
    }

    public ClientConnexionDTO(Integer idClient, String numeroTelephone) {
        this.idClient = idClient;
        this.numeroTelephone = numeroTelephone;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }
}
