package mg.vinaAkoho.vina_akoho.dto.depense;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepenseRequestDTO {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotBlank(message = "La désignation est obligatoire")
    @Size(max = 255, message = "La désignation ne doit pas dépasser 255 caractères")
    private String designation;

    @NotNull(message = "La catégorie de dépense est obligatoire")
    private Integer idCategorieDepense;

    @NotNull(message = "La phase est obligatoire")
    private Integer idPhase;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le montant doit être positif ou nul")
    private BigDecimal montant;

    @NotNull(message = "Le statut de dépense est obligatoire")
    private Integer idStatutDepense;
}
