package mg.vinaAkoho.vina_akoho.dto.ventes;

import jakarta.validation.constraints.NotNull;
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
public class VenteFormDTO {

    @NotNull(message = "Le client est obligatoire")
    private Integer idClient;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private Long idModePaiement;
}
