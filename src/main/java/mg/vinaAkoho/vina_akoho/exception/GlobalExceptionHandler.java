package mg.vinaAkoho.vina_akoho.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.RecetteInexistanteException;
import mg.vinaAkoho.vina_akoho.exception.recetteproduit.RecetteProduitException;
import mg.vinaAkoho.vina_akoho.exception.recetteproduit.RecetteProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.stockmp.StockInsuffisantException;
import mg.vinaAkoho.vina_akoho.exception.admin.EmployeNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.admin.EmailDejaUtiliseException;
import mg.vinaAkoho.vina_akoho.exception.admin.MdpIdentifiqueException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.MatierePremiereNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.FournisseurNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.UniteNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.TypeMouvementNotFoundException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error(ex.getReason() == null ? "Requête invalide" : ex.getReason()));
    }

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = "Opération refusée par la base de données.";
        Throwable cause = ex.getMostSpecificCause();
        if (cause != null && cause.getMessage() != null) {
            String causeMessage = cause.getMessage();
            if (causeMessage.contains("chk_lot_produit_peremption")) {
                message = "La date de péremption doit être supérieure ou égale à la date de fabrication.";
            } else if (causeMessage.contains("fabrication_quantite_produite_check")) {
                message = "La quantité produite doit être strictement positive.";
            } else if (causeMessage.contains("uq_fabrication_lot_mp")) {
                message = "Un même lot de matière première ne peut pas être enregistré deux fois dans une fabrication.";
            }
        }

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

    @ExceptionHandler(EmployeNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmployeNotFound(EmployeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(EmailDejaUtiliseException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailDejaUtilise(EmailDejaUtiliseException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MdpIdentifiqueException.class)
    public ResponseEntity<ApiResponse<Object>> handleMdpIdentique(MdpIdentifiqueException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MatierePremiereNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleMatierePremiereNotFound(MatierePremiereNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(FournisseurNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleFournisseurNotFound(FournisseurNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(UniteNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUniteNotFound(UniteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TypeMouvementNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMouvementNotFound(TypeMouvementNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }
}
