package mg.vinaAkoho.vina_akoho.dto.produit;

public record PaginationDTO(
    int currentPage,
    int totalPages,
    long totalElements,
    int pageSize
) {}
