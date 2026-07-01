package mg.vinaAkoho.vina_akoho.entity.ventes;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;

@Entity
@Table(name = "ligne_vente_lot")
@Getter
@Setter
@NoArgsConstructor
public class LigneVenteLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ligne_vente", nullable = false)
    private LigneVente ligneVente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lot_produit", nullable = false)
    private LotProduit lotProduit;

    @Column(name = "quantite", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantite;
}
