package mg.vinaAkoho.vina_akoho.dto.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionMatierePremiereDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionProductionDTO;

public record ProductionDashboardDataDTO(
    BigDecimal quantiteProduitsFinis,
    BigDecimal quantiteMatièresPremières,
    long lotsProduits,
    long lotsExpirantBientot,
    long produitsSousSeuil,
    long mpSousSeuilMin,
    String uniteProduits,
    String uniteMps,
    List<PrevisionProductionDTO> previsionsProduction,
    List<PrevisionMatierePremiereDTO> previsionsMp,
    int joursAnalyse,
    int joursCouverture,
    LocalDate dateDebutPrevision,
    LocalDate dateFinPrevision
) {}
