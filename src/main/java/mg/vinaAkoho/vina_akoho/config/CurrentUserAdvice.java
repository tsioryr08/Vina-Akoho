package mg.vinaAkoho.vina_akoho.config;

import jakarta.servlet.http.HttpSession;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.security.SessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    @Autowired
    private EmployeRepository employeRepository;

    @ModelAttribute("currentUser")
    public Employe getCurrentUser(HttpSession session) {
        Integer idEmploye = (Integer) session.getAttribute(SessionFilter.ATTRIBUT_ID_EMPLOYE);
        if (idEmploye == null) return null;
        return employeRepository.findById(idEmploye).orElse(null);
    }

    @ModelAttribute("sidebarTemplate")
    public String getSidebarTemplate(HttpSession session) {
        String role = (String) session.getAttribute(SessionFilter.ATTRIBUT_ROLE);
        if (role == null) return "layout/sidebar";
        return SidebarConfig.resolve(role);
    }

    @ModelAttribute("headerTemplate")
    public String getHeaderTemplate(HttpSession session) {
        String role = (String) session.getAttribute(SessionFilter.ATTRIBUT_ROLE);
        if (role == null) return "layout/header";
        return HeaderConfig.resolve(role);
    }
}
