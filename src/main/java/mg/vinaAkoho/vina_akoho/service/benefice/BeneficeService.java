package mg.vinaAkoho.vina_akoho.service.benefice;


import org.springframework.stereotype.Service;

import mg.vinaAkoho.vina_akoho.dto.benefice.RapportBeneficeDTO;
import mg.vinaAkoho.vina_akoho.repository.depense.CategorieDepenseRepository;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BeneficeService {

    private final VenteRepository venteRepository;
    private final DepenseRepository depenseRepository;
    private final CategorieDepenseRepository categorieRepository;

    public BeneficeService(VenteRepository venteRepository, DepenseRepository depenseRepository, CategorieDepenseRepository categorieRepository) {
        this.venteRepository = venteRepository;
        this.depenseRepository = depenseRepository;
        this.categorieRepository = categorieRepository;
    }

    public RapportBeneficeDTO calculerBeneficeParPeriodeEtCategorie(LocalDate dateDebut, LocalDate dateFin, Integer categorieId) {
    LocalDateTime startDateTime = dateDebut.atStartOfDay(); 
    

    LocalDateTime endDateTime = dateFin.atTime(java.time.LocalTime.MAX); 

    BigDecimal totalRecettes = venteRepository.sumRecettesEntreDeuxDates(startDateTime, endDateTime);
    BigDecimal totalDepenses = depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(dateDebut, dateFin, categorieId);

    RapportBeneficeDTO rapport = new RapportBeneficeDTO(dateDebut, dateFin, totalRecettes, totalDepenses);
    rapport.setCategorieId(categorieId);
    rapport.setCategories(categorieRepository.findAll()); 

    return rapport;
}

    
}
