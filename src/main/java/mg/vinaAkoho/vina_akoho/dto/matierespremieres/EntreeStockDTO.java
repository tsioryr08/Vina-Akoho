package mg.vinaAkoho.vina_akoho.dto.matierespremieres;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EntreeStockDTO(

        @NotNull(message = "La matière première est obligatoire")
        Integer idMatierePremiere,

        @NotNull(message = "La quantité est obligatoire")
        @DecimalMin(value = "0", inclusive = false, message = "La quantité doit être supérieure à 0")
        BigDecimal quantite,

        @NotNull(message = "La date de réception est obligatoire")
        LocalDate dateReception,

        @NotNull(message = "L'employé responsable est obligatoire")
        Integer idEmploye,

        @NotNull(message = "Le coût unitaire est obligatoire")
        @DecimalMin(value = "0", inclusive = false, message = "Le coût unitaire doit être supérieur à 0")
        BigDecimal coutUnitaire

) {
}