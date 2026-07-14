package mg.vinaAkoho.vina_akoho.dto.livraison;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonFormDTO {

    @NotNull(message = "La vente est obligatoire")
    private Long idVente;

    @NotBlank(message = "La zone de livraison est obligatoire")
    private String idZoneLivraison;

    @NotNull(message = "Le livreur est obligatoire")
    private Integer idLivreur;

    private String lieuExact;

    private Integer idStatutLivraison;

    private String contact;

    private String dateLivraison;

    private String commentaire;
}
