package mg.vinaAkoho.vina_akoho.service.depense;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO;
import mg.vinaAkoho.vina_akoho.entity.depense.Phase;
import mg.vinaAkoho.vina_akoho.exception.depense.PhaseNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.depense.PhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PhaseService {

    private final PhaseRepository phaseRepository;

    public List<PhaseDTO> listerToutes() {
        return phaseRepository.findAll()
                .stream()
                .map(this::versDTO)
                .toList();
    }

    public PhaseDTO trouverParId(Integer id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> PhaseNotFoundException.parId(id));
        return versDTO(phase);
    }

    private PhaseDTO versDTO(Phase phase) {
        return PhaseDTO.builder()
                .id(phase.getId())
                .libelle(phase.getLibelle())
                .description(phase.getDescription())
                .createdAt(phase.getCreatedAt())
                .updatedAt(phase.getUpdatedAt())
                .build();
    }
}
