package mg.vinaAkoho.vina_akoho.dto.clients;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientHistoriqueAchatsDTO {

    private List<VenteDTO> ventes;
    private BigDecimal totalAchats;
    private BigDecimal totalRegle;
    private BigDecimal soldeRestant;
}