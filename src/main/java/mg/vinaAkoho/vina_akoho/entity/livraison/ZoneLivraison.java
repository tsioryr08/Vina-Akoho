package mg.vinaAkoho.vina_akoho.entity.livraison;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zone_livraison")
@Getter
@Setter
public class ZoneLivraison {
    @Id
    private String id;
    @Column(unique = true)
    private String libelle;
}
