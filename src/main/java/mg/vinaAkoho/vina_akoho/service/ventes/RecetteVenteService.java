package mg.vinaAkoho.vina_akoho.service.ventes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.RecetteVenteDTO;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecetteVenteService {

    private final LigneVenteRepository ligneVenteRepository;

    public List<RecetteVenteDTO> listerParPeriode(LocalDate debut, LocalDate fin) {
        LocalDate dateDebut = debut != null ? debut : LocalDate.now().withDayOfMonth(1);
        LocalDate dateFin = fin != null ? fin : LocalDate.now();

        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }

        return ligneVenteRepository.calculerRecettesParPeriode(
                        dateDebut.atStartOfDay(),
                        dateFin.plusDays(1).atStartOfDay())
                .stream()
                .map(ligne -> RecetteVenteDTO.builder()
                        .dateVente(ligne.getDateVente())
                        .produitId(ligne.getProduitId())
                        .produitNom(ligne.getProduitNom())
                        .prixVente(ligne.getPrixVente())
                        .quantiteVendue(ligne.getQuantiteVendue())
                        .montantRecette(ligne.getMontantRecette())
                        .build())
                .toList();
    }

    public BigDecimal calculerTotal(List<RecetteVenteDTO> recettes) {
        return recettes.stream()
                .map(RecetteVenteDTO::getMontantRecette)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculerQuantiteTotale(List<RecetteVenteDTO> recettes) {
        return recettes.stream()
                .map(RecetteVenteDTO::getQuantiteVendue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
