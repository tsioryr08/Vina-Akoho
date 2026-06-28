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
public class CategorieDTO {

    private Long id;
    private String libelle;
    private String description;
    private BigDecimal pourcentageProteine;
    private BigDecimal pourcentageMatiereGrasses;
    private BigDecimal pourcentageHumiditeMax;
    private BigDecimal margePourcentage;
    private Boolean actif;
    private Long nombreProduits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}