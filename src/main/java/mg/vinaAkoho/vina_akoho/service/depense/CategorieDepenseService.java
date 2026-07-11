package mg.vinaAkoho.vina_akoho.service.depense;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO;
import mg.vinaAkoho.vina_akoho.entity.depense.CategorieDepense;
import mg.vinaAkoho.vina_akoho.exception.depense.CategorieDepenseNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.depense.CategorieDepenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieDepenseService {

    private final CategorieDepenseRepository categorieDepenseRepository;

    public List<CategorieDepenseDTO> listerToutes() {
        return categorieDepenseRepository.findAll()
                .stream()
                .map(this::versDTO)
                .toList();
    }

    public CategorieDepenseDTO trouverParId(Integer id) {
        CategorieDepense categorieDepense = categorieDepenseRepository.findById(id)
                .orElseThrow(() -> CategorieDepenseNotFoundException.parId(id));
        return versDTO(categorieDepense);
    }

    public CategorieDepenseDTO trouverParIdSansException(Integer id) {
        return categorieDepenseRepository.findById(id)
                .map(this::versDTO)
                .orElseGet(() -> CategorieDepenseDTO.builder().libelle("").build());
    }

    private CategorieDepenseDTO versDTO(CategorieDepense categorieDepense) {
        return CategorieDepenseDTO.builder()
                .id(categorieDepense.getId())
                .libelle(categorieDepense.getLibelle())
                .createdAt(categorieDepense.getCreatedAt())
                .build();
    }
}
