package mg.vinaAkoho.vina_akoho.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping({"/", "/login", "/index", "/index.html", "/index.php"})
    public String afficherPageLogin() {
        return "index";
    }
}
