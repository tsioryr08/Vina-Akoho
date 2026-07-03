package mg.vinaAkoho.vina_akoho.dto.depense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseDTO {

    private Integer id;
    private String libelle;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
