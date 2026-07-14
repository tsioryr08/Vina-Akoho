package mg.vinaAkoho.vina_akoho.entity.depense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;

import java.math.BigDecimal;

@Entity
@Table(name = "depense_lot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepenseLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_depense", nullable = false)
    private Depense depense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot_produit")
    private LotProduit lotProduit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot_mp")
    private LotMp lotMp;

    public BigDecimal getQuantiteProduit() {
        if (lotProduit != null && lotProduit.getProduit() != null) {
            return lotProduit.getQuantiteRestante();
        }
        return null;
    }
}
