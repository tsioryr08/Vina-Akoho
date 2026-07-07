package mg.vinaAkoho.vina_akoho.dto.admin;

import jakarta.validation.constraints.NotBlank;

public class ReinitialisationMdpDTO {

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    private String nouveauMdp;

    public String getNouveauMdp() { return nouveauMdp; }
    public void setNouveauMdp(String nouveauMdp) { this.nouveauMdp = nouveauMdp; }
}