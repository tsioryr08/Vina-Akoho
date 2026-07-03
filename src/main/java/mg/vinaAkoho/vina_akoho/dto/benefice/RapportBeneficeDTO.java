package mg.vinaAkoho.vina_akoho.dto.benefice;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import mg.vinaAkoho.vina_akoho.entity.depense.CategorieDepense;

public class RapportBeneficeDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal benefice;
    private Integer categorieId; 
    private List<CategorieDepense> categories;

    public RapportBeneficeDTO(LocalDate dateDebut, LocalDate dateFin, BigDecimal totalRecettes, BigDecimal totalDepenses) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.totalRecettes = totalRecettes != null ? totalRecettes : BigDecimal.ZERO;
        this.totalDepenses = totalDepenses != null ? totalDepenses : BigDecimal.ZERO;
        this.benefice = this.totalRecettes.subtract(this.totalDepenses);
    }

    // Getters et Setters
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public BigDecimal getTotalRecettes() { return totalRecettes; }
    public BigDecimal getTotalDepenses() { return totalDepenses; }
    public BigDecimal getBenefice() { return benefice; }
    
    public Integer getCategorieId() { return categorieId; }
    public void setCategorieId(Integer categorieId) { this.categorieId = categorieId; }
    
    public List<CategorieDepense> getCategories() { return categories; }
    public void setCategories(List<CategorieDepense> categories) { this.categories = categories; }
}