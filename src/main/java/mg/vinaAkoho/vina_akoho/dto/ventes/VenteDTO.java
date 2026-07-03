package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
public class VenteDTO {

    private Long id;
    private String clientNom;
    private String clientPrenom;
    private String clientTelephone;
    private String clientAdresse;
    private String clientZoneLivraison;
    private LocalDateTime dateVente;
    private String modePaiement;
    private String statutVente;
    private BigDecimal montantTotal;
    private List<LigneVenteDTO> lignes;
    private FactureDTO facture;
}
