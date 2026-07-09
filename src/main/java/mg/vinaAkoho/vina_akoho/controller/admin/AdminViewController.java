package mg.vinaAkoho.vina_akoho.controller.admin;

import mg.vinaAkoho.vina_akoho.entity.login.Role;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.login.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminViewController {

    private final RoleRepository roleRepository;
    private final EmployeRepository employeRepository;

    @Autowired
    public AdminViewController(RoleRepository roleRepository, EmployeRepository employeRepository) {
        this.roleRepository = roleRepository;
        this.employeRepository = employeRepository;
    }

    @GetMapping("/admin/employes")
    public String listeEmployes(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("totalActifs", employeRepository.findByActif(true).size());
        model.addAttribute("totalDesactives", employeRepository.findByActif(false).size());
        model.addAttribute("totalRoles", roles.size());
        return "dashboard/admin/employes";
    }

    @GetMapping("/admin/employes/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("modeEdition", false);
        return "dashboard/admin/employe-form";
    }

    @GetMapping("/admin/employes/{id}/modifier")
    public String formulaireModification(@PathVariable Integer id, Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("modeEdition", true);
        model.addAttribute("idEmploye", id);
        return "dashboard/admin/employe-form";
    }
}