package mg.vinaAkoho.vina_akoho.service.depense;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO;
import mg.vinaAkoho.vina_akoho.entity.depense.StatutDepense;
import mg.vinaAkoho.vina_akoho.exception.depense.StatutDepenseNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.depense.StatutDepenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatutDepenseService {

    private final StatutDepenseRepository statutDepenseRepository;

    public List<StatutDepenseDTO> listerTous() {
        return statutDepenseRepository.findAll()
                .stream()
                .map(this::versDTO)
                .toList();
    }

    public StatutDepenseDTO trouverParId(Integer id) {
        StatutDepense statutDepense = statutDepenseRepository.findById(id)
                .orElseThrow(() -> StatutDepenseNotFoundException.parId(id));
        return versDTO(statutDepense);
    }

    private StatutDepenseDTO versDTO(StatutDepense statutDepense) {
        return StatutDepenseDTO.builder()
                .id(statutDepense.getId())
                .libelle(statutDepense.getLibelle())
                .createdAt(statutDepense.getCreatedAt())
                .build();
    }
}
