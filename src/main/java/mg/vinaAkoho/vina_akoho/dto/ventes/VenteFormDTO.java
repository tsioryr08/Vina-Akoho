package mg.vinaAkoho.vina_akoho.dto.ventes;

import jakarta.validation.constraints.NotNull;
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
public class VenteFormDTO {

    @NotNull(message = "Le client est obligatoire")
    private Integer idClient;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private Long idModePaiement;

    /**
     * Point 1 du markdown : "Livraison requise ?". Si true, les champs
     * ci-dessous sont utilisés pour créer automatiquement une livraison
     * (module livraison existant) juste après la création de la vente.
     */
    private boolean livraisonRequise;

    /** Zone de livraison choisie quand une livraison est requise. */
    private String idZoneLivraison;

    private String adresseLivraison;

    private String contactLivraison;

    /** Date de livraison souhaitée par le client (optionnelle). Format ISO yyyy-MM-dd. */
    private String dateLivraisonSouhaitee;

    /** Commentaire ou instructions de livraison (optionnel). */
    private String commentaireLivraison;
}
