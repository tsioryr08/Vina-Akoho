package mg.vinaAkoho.vina_akoho.config;

import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class HeaderConfig {

    public static final Map<String, String> ROLE_HEADER_MAP = new LinkedHashMap<>();

    static {
        ROLE_HEADER_MAP.put("Administrateur", "layout/header");
        ROLE_HEADER_MAP.put("Responsable de production", "layout/header");
        ROLE_HEADER_MAP.put("Responsable commercial", "layout/header");
        ROLE_HEADER_MAP.put("Comptable", "layout/header");
        ROLE_HEADER_MAP.put("Responsable achat", "layout/header");
        ROLE_HEADER_MAP.put("Gestionnaire de stock", "layout/header");
        ROLE_HEADER_MAP.put("Livreur", "layout/header");
    }

    public static String resolve(String role) {
        return ROLE_HEADER_MAP.getOrDefault(role, "layout/header");
    }
}
