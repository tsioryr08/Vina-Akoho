package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
public class VenteListeExportDTO {

    private Long id;
    private String client;
    private LocalDateTime date;
    private String produits;
    private String modePaiement;
    private BigDecimal total;
    private String statut;
}
