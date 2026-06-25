package mg.vinaAkoho.vina_akoho.repository.produit;

import jakarta.persistence.criteria.Predicate;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProduitSpecification {

    private ProduitSpecification() {
    }

    public static Specification<Produit> avecFiltres(String recherche,
                                                       Long idCategorie,
                                                       BigDecimal prixMin,
                                                       BigDecimal prixMax) {
        return (root, query, cb) -> {
            List<Predicate> predicats = new ArrayList<>();

            if (recherche != null && !recherche.isBlank()) {
                String motif = "%" + recherche.trim().toLowerCase() + "%";
                Predicate parRef = cb.like(cb.lower(root.get("ref")), motif);
                Predicate parNom = cb.like(cb.lower(root.get("nom")), motif);
                Predicate parDescription = cb.like(cb.lower(cb.coalesce(root.get("description"), "")), motif);
                predicats.add(cb.or(parRef, parNom, parDescription));
            }

            if (idCategorie != null) {
                predicats.add(cb.equal(root.get("categorie").get("id"), idCategorie));
            }

            if (prixMin != null) {
                predicats.add(cb.greaterThanOrEqualTo(root.get("prixVente"), prixMin));
            }

            if (prixMax != null) {
                predicats.add(cb.lessThanOrEqualTo(root.get("prixVente"), prixMax));
            }

            return cb.and(predicats.toArray(new Predicate[0]));
        };
    }
}
