package mg.vinaAkoho.vina_akoho.service.login;

import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.login.LoginResponseDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.security.PasswordHasher;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final EmployeRepository employeRepository;

    @Autowired
    public LoginService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO requete, HttpSession session) {
        Employe employe = employeRepository.findByEmail(requete.getEmail())
                .orElseThrow(() -> new IdentifiantsInvalidesException(
                        "Email ou mot de passe incorrect"));

        boolean motDePasseCorrect = PasswordHasher.verifier(requete.getMdp(), employe.getMdp());

        if (!motDePasseCorrect) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        session.setAttribute("idEmploye", employe.getId());
        session.setAttribute("role", employe.getRole().getPoste());
        session.setAttribute("nom", employe.getNom());
        session.setAttribute("prenom", employe.getPrenom());
        session.setAttribute("email", employe.getEmail());

        return new LoginResponseDTO(
                null,
                employe.getId(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getEmail(),
                employe.getRole().getPoste()
        );
    }
}
