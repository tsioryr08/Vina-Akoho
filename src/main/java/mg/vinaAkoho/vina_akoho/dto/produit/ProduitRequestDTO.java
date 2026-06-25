package mg.vinaAkoho.vina_akoho.dto.produit;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO utilisé pour la création et la mise à jour d'un produit
 * (requête entrante depuis le frontend).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitRequestDTO {

    @NotBlank(message = "La référence du produit est obligatoire")
    @Size(max = 100, message = "La référence ne doit pas dépasser 100 caractères")
    private String ref;

    @NotNull(message = "La catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    private String nom;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix de vente doit être positif ou nul")
    private BigDecimal prixVente;

    @Min(value = 0, message = "Le seuil d'alerte doit être positif ou nul")
    private Integer seuilAlerte;

    private String description;
    private Boolean actif;
}
