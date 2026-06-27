package mg.vinaAkoho.vina_akoho.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ApiResponse<Object>> handleIdentifiantsInvalides(IdentifiantsInvalidesException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Données invalides");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenerique(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Une erreur interne est survenue"));
    }
}
