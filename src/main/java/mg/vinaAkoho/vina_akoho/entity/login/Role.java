package mg.vinaAkoho.vina_akoho.entity.login;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entité correspondant à la table "role" du schéma SQL.
 * Représente les 7 rôles définis dans les règles d'organisation (RO01 à RO07) :
 * Administrateur, Responsable Production, Gestionnaire de Stock,
 * Responsable Commercial, Comptable, Responsable Achats, Livreur.
 */
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "poste", nullable = false, unique = true, length = 100)
    private String poste;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Role() {
    }

    public Role(String poste) {
        this.poste = poste;
    }

    // --- Getters / Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
