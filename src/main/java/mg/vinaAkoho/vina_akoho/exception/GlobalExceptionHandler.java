package mg.vinaAkoho.vina_akoho.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.RecetteInexistanteException;
import mg.vinaAkoho.vina_akoho.exception.recetteproduit.RecetteProduitException;
import mg.vinaAkoho.vina_akoho.exception.recetteproduit.RecetteProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.stockmp.StockInsuffisantException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<ApiResponse<Object>> handleStockInsuffisant(StockInsuffisantException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage()));
}

    @ExceptionHandler(RecetteProduitNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRecetteNotFound(RecetteProduitNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(RecetteProduitException.class)
    public ResponseEntity<ApiResponse<Object>> handleRecetteException(RecetteProduitException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("Données invalides");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenerique(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Une erreur interne est survenue"));
    }

    @ExceptionHandler(ProduitNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleProduitNotFound(ProduitNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(RecetteInexistanteException.class)
    public ResponseEntity<ApiResponse<Object>> handleRecetteInexistante(RecetteInexistanteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }
}
