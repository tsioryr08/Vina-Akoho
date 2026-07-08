package mg.vinaAkoho.vina_akoho.config;

import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class SidebarConfig {

    public static final Map<String, String> ROLE_SIDEBAR_MAP = new LinkedHashMap<>();

    static {
        ROLE_SIDEBAR_MAP.put("Administrateur", "layout/admin");
        ROLE_SIDEBAR_MAP.put("Responsable de production", "layout/responsableProduction");
        ROLE_SIDEBAR_MAP.put("Responsable commercial", "layout/responsableCommercial");
        ROLE_SIDEBAR_MAP.put("Comptable", "layout/comptable");
        ROLE_SIDEBAR_MAP.put("Responsable achat", "layout/responsableAchat");
        ROLE_SIDEBAR_MAP.put("Gestionnaire de stock", "layout/gestionnaireStock");
        ROLE_SIDEBAR_MAP.put("Livreur", "layout/livreur");
    }

    public static String resolve(String role) {
        return ROLE_SIDEBAR_MAP.getOrDefault(role, "layout/sidebar");
    }
}
