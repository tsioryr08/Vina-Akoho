package mg.vinaAkoho.vina_akoho.dto.matierespremieres;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MatierePremiereRequestDTO(

        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotNull(message = "Le fournisseur est obligatoire")
        Integer idFournisseur,

        @NotNull(message = "Le coût unitaire est obligatoire")
        @DecimalMin(value = "0", message = "Le coût unitaire ne peut pas être négatif")
        BigDecimal coutUnitaire,

        @NotNull(message = "L'unité est obligatoire")
        Integer idUnite,

        @DecimalMin(value = "0", message = "Le seuil minimum ne peut pas être négatif")
        BigDecimal seuilMinimum

) {
}
