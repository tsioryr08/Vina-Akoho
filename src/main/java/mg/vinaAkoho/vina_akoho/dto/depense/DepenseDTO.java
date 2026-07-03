package mg.vinaAkoho.vina_akoho.dto.depense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepenseDTO {

    private Integer id;
    private LocalDate date;
    private String designation;
    private Integer idCategorieDepense;
    private String libelleCategorieDepense;
    private Integer idPhase;
    private String libellePhase;
    private BigDecimal montant;
    private Integer idStatutDepense;
    private String libelleStatutDepense;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
