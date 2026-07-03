package mg.vinaAkoho.vina_akoho.dto.produit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitDTO {

    private Long id;
    private String ref;
    private Long idCategorie;
    private String libelleCategorie;
    private String nom;
    private BigDecimal prixVente;
    private Integer seuilAlerte;
    private String description;
    private Boolean actif;
    private BigDecimal quantiteStock;
    private String statut;
    private BigDecimal margePourcentage;
    private BigDecimal pourcentageProteine;
    private BigDecimal pourcentageMatiereGrasses;
    private BigDecimal pourcentageHumiditeMax;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}