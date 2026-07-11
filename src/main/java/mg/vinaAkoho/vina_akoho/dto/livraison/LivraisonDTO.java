package mg.vinaAkoho.vina_akoho.dto.livraison;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class LivraisonDTO {

    private Long id;
    private Long idVente;
    private String referenceVente;
    private String clientNom;
    private String clientPrenom;
    private String livreurNom;
    private String livreurPrenom;
    private String lieuExact;
    private String contact;
    private LocalDate dateLivraison;
    private String commentaire;
    private String zoneLivraison;
    private String statutLivraison;
    private LocalDateTime createdAt;
}
