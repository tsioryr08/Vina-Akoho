package mg.vinaAkoho.vina_akoho.service.depense;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.depense.DepenseDTO;
import mg.vinaAkoho.vina_akoho.dto.depense.DepenseRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.depense.CategorieDepense;
import mg.vinaAkoho.vina_akoho.entity.depense.Depense;
import mg.vinaAkoho.vina_akoho.entity.depense.Phase;
import mg.vinaAkoho.vina_akoho.entity.depense.StatutDepense;
import mg.vinaAkoho.vina_akoho.exception.depense.DepenseDtoNonSupporteException;
import mg.vinaAkoho.vina_akoho.exception.depense.DepenseNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class DepenseService {

    private final DepenseRepository depenseRepository;
    private final CategorieDepenseService categorieDepenseService;
    private final PhaseService phaseService;
    private final StatutDepenseService statutDepenseService;

    public List<DepenseDTO> listerToutes() {
        return depenseRepository.findAll()
                .stream()
                .map(this::versDTO)
                .toList();
    }

    public DepenseDTO trouverParId(Integer id) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> DepenseNotFoundException.parId(id));
        return versDTO(depense);
    }

    public BigDecimal calculerMontantTotal() {
        return depenseRepository.findAll()
                .stream()
                .map(Depense::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculerMontantTotalParCategorie(Integer idCategorieDepense) {
        return depenseRepository.findAll()
                .stream()
                .filter(depense -> Objects.equals(depense.getIdCategorieDepense(), idCategorieDepense))
                .map(Depense::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long compterToutes() {
        return depenseRepository.count();
    }

    public List<DepenseDTO> rechercher(String motCle, Integer idCategorie, LocalDate dateDu) {
        return depenseRepository.findAll()
                .stream()
                .filter(depense -> {
                    if (motCle == null || motCle.isBlank()) {
                        return true;
                    }
                    String designation = depense.getDesignation();
                    return designation != null && designation.toLowerCase().contains(motCle.toLowerCase());
                })
                .filter(depense -> idCategorie == null || Objects.equals(depense.getIdCategorieDepense(), idCategorie))
                .filter(depense -> dateDu == null || (depense.getDate() != null && !depense.getDate().isBefore(dateDu)))
                .map(this::versDTO)
                .toList();
    }

    public DepenseDTO creer(DepenseRequestDTO requete) {
        CategorieDepense categorieDepense = convertToEntity(categorieDepenseService.trouverParId(requete.getIdCategorieDepense()));
        Phase phase = convertToEntity(phaseService.trouverParId(requete.getIdPhase()));
        StatutDepense statutDepense = convertToEntity(statutDepenseService.trouverParId(requete.getIdStatutDepense()));

        Depense depense = new Depense();
        depense.setDesignation(requete.getDesignation());
        depense.setMontant(requete.getMontant());
        depense.setDate(requete.getDate());
        depense.setCategorieDepense(categorieDepense);
        depense.setPhase(phase);
        depense.setStatutDepense(statutDepense);

        Depense sauvegardee = depenseRepository.save(depense);
        return versDTO(sauvegardee);
    }

    public DepenseDTO modifier(Integer id, DepenseRequestDTO requete) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> DepenseNotFoundException.parId(id));

        CategorieDepense categorieDepense = convertToEntity(categorieDepenseService.trouverParId(requete.getIdCategorieDepense()));
        Phase phase = convertToEntity(phaseService.trouverParId(requete.getIdPhase()));
        StatutDepense statutDepense = convertToEntity(statutDepenseService.trouverParId(requete.getIdStatutDepense()));

        depense.setDesignation(requete.getDesignation());
        depense.setMontant(requete.getMontant());
        depense.setDate(requete.getDate());
        depense.setCategorieDepense(categorieDepense);
        depense.setPhase(phase);
        depense.setStatutDepense(statutDepense);

        Depense sauvegardee = depenseRepository.save(depense);
        return versDTO(sauvegardee);
    }

    public void supprimer(Integer id) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> DepenseNotFoundException.parId(id));
        depenseRepository.delete(depense);
    }

    private <T> T convertToEntity(Object dto) {
        if (dto instanceof mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO) {
            mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO cdto = (mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO) dto;
            CategorieDepense entity = new CategorieDepense();
            entity.setId(cdto.getId());
            entity.setLibelle(cdto.getLibelle());
            entity.setCreatedAt(cdto.getCreatedAt());
            return (T) entity;
        } else if (dto instanceof mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO) {
            mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO pdto = (mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO) dto;
            Phase entity = new Phase();
            entity.setId(pdto.getId());
            entity.setLibelle(pdto.getLibelle());
            entity.setDescription(pdto.getDescription());
            entity.setCreatedAt(pdto.getCreatedAt());
            entity.setUpdatedAt(pdto.getUpdatedAt());
            return (T) entity;
        } else if (dto instanceof mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO) {
            mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO sdto = (mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO) dto;
            StatutDepense entity = new StatutDepense();
            entity.setId(sdto.getId());
            entity.setLibelle(sdto.getLibelle());
            entity.setCreatedAt(sdto.getCreatedAt());
            return (T) entity;
        }
        throw DepenseDtoNonSupporteException.pourType(dto.getClass());
    }

    private DepenseDTO versDTO(Depense depense) {
        return DepenseDTO.builder()
                .id(depense.getId())
                .date(depense.getDate())
                .designation(depense.getDesignation())
                .idCategorieDepense(depense.getIdCategorieDepense())
                .libelleCategorieDepense(depense.getCategorieDepense() != null ? depense.getCategorieDepense().getLibelle() : null)
                .idPhase(depense.getIdPhase())
                .libellePhase(depense.getPhase() != null ? depense.getPhase().getLibelle() : null)
                .montant(depense.getMontant())
                .idStatutDepense(depense.getIdStatutDepense())
                .libelleStatutDepense(depense.getStatutDepense() != null ? depense.getStatutDepense().getLibelle() : null)
                .createdAt(depense.getCreatedAt())
                .updatedAt(depense.getUpdatedAt())
                .build();
    }
}
