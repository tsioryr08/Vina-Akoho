package mg.vinaAkoho.vina_akoho.dto.produit;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorieRequestDTO {

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    private String libelle;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    @DecimalMin(value = "0.0", message = "Le pourcentage de protéines doit être supérieur ou égal à 0")
    @DecimalMax(value = "100.0", message = "Le pourcentage de protéines ne doit pas dépasser 100")
    private BigDecimal pourcentageProteine;

    @DecimalMin(value = "0.0", message = "Le pourcentage de matières grasses doit être supérieur ou égal à 0")
    @DecimalMax(value = "100.0", message = "Le pourcentage de matières grasses ne doit pas dépasser 100")
    private BigDecimal pourcentageMatiereGrasses;

    @DecimalMin(value = "0.0", message = "Le pourcentage d'humidité max doit être supérieur ou égal à 0")
    @DecimalMax(value = "100.0", message = "Le pourcentage d'humidité max ne doit pas dépasser 100")
    private BigDecimal pourcentageHumiditeMax;

    @NotNull(message = "La marge est obligatoire")
    @DecimalMin(value = "0.0", message = "La marge doit être supérieure ou égale à 0")
    private BigDecimal margePourcentage;

    private Boolean actif;
}