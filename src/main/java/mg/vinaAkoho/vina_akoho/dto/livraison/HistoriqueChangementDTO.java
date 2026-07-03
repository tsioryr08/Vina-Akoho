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
public class HistoriqueChangementDTO {

    private Integer id;
    private Integer idLivraison;
    private String ancienStatut;
    private String nouveauStatut;
    private Integer idUtilisateur;
    private String commentaire;
    private LocalDateTime dateChangement;
}
