package mg.vinaAkoho.vina_akoho.dto;

/**
 * Format de réponse API UNIQUE imposé à tout le projet !!!!!:
 * {
 *   "success": true/false,
 *   "message": "...",
 *   "data": { ... } ou null
 * }
 *
 * Cette classe est générique (<T>) car "data" peut être n'importe quel DTO
 * selon le module (LoginResponseDTO, ProduitDTO, ClientDTO, etc.).
 *
 *  ce fichier appartient au dossier "dto/" racine
 * (pas dans un sous-dossier de module) car il est partagé par tous les
 * controllers du projet, quel que soit le module. Si quelqu'un d'autre
 * 
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
