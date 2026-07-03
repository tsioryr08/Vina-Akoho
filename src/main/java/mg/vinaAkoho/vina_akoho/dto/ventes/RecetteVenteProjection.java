package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RecetteVenteProjection {

    LocalDate getDateVente();

    Long getProduitId();

    String getProduitNom();

    BigDecimal getPrixVente();

    BigDecimal getQuantiteVendue();

    BigDecimal getMontantRecette();
}
