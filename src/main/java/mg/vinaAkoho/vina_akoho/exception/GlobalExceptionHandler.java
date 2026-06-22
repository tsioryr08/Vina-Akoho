package mg.vinaAkoho.vina_akoho.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.exception.login.TokenInvalideException;

/**
 * Intercepte TOUTES les exceptions levees par les controllers de
 * l'application et les transforme dans le format ApiResponse standard
 * , au lieu de laisser Spring renvoyer une
 * page d'erreur HTML/JSON par defaut illisible pour le frontend.
 *
 *ce fichier est partage par tout le projet.
 * Chaque developpeur qui cree une nouvelle exception personnalisee
 * dans son module doit ajouter ICI une methode @ExceptionHandler
 * correspondante (voir l'exemple avec IdentifiantsInvalidesException
 * et TokenInvalideException ci-dessous), plutôt que de creer son
 * propre gestionnaire d'exceptions dans son module.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ApiResponse<Object>> handleIdentifiantsInvalides(IdentifiantsInvalidesException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TokenInvalideException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenInvalide(TokenInvalideException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Capturee automatiquement quand la validation Jakarta (@NotBlank, etc.)
     * echoue sur un DTO annote @Valid dans un controller.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Données invalides");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(ApiResponse.error(message));
    }

    /**
     * toute exception non prevue ailleurs tombe ici,
     * pour ne jamais renvoyer une stack trace brute au frontend.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenerique(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(ApiResponse.error("Une erreur interne est survenue"));
    }
}
