package mg.vinaAkoho.vina_akoho.dto.ventes;

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
public class StatistiqueVenteReponseDTO {
    private List<TopProduitStatDTO> topProduits;
    private List<TopCategorieStatDTO> topCategories;
    private List<EvolutionVenteStatDTO> evolution;
}