package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
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
public class RechercheVenteDTO {

    private String client;

    private String produit;

    private String numeroFacture;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String modePaiement;

    private String statut;

    private BigDecimal montantMin;

    private BigDecimal montantMax;

    private String triPar;

    private String ordreTri;

    @Min(value = 1, message = "La page doit être au moins 1")
    private Integer page;

    @Min(value = 1, message = "La taille doit être au moins 1")
    private Integer taille;

    public static RechercheVenteDTO parDefaut() {
        return RechercheVenteDTO.builder()
                .page(0)
                .taille(10)
                .triPar("dateVente")
                .ordreTri("desc")
                .build();
    }
}
