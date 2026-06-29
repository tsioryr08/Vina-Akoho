package mg.vinaAkoho.vina_akoho.controller.login;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;

@Controller
public class LoginViewController {

    private final EmployeRepository employeRepository;

    @Autowired
    public LoginViewController(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    @ModelAttribute("employes")
    public List<Employe> listerEmployes() {
        return employeRepository.findAll();
    }

    @GetMapping({"/", "/login", "/index", "/index.html", "/index.php"})
    public String afficherPageLogin() {
        return "index";
    }
}
