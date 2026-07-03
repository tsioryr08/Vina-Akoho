package mg.vinaAkoho.vina_akoho.dto.livraison;

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
public class FichierLivraisonDTO {

    private String nom;
    private String url;
}
