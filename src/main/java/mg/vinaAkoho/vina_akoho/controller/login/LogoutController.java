package mg.vinaAkoho.vina_akoho.controller.login;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logout")
public class LogoutController {

    @PostMapping
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }
}
