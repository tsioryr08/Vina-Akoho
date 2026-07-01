package mg.vinaAkoho.vina_akoho.dto.entreeproduit;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EntreeProduitRequestDTO(
        @NotNull(message = "Le produit est obligatoire")
        Long idProduit,

        @NotNull(message = "La quantité à produire est obligatoire")
        @DecimalMin(value = "0", inclusive = false, message = "La quantité doit être supérieure à 0")
        BigDecimal quantiteAProduire,

        LocalDate datePeremption, // optionnelle

        @NotNull(message = "L'employé responsable est obligatoire")
        Integer idEmploye
) {
}