package mg.vinaAkoho.vina_akoho.service.login;

import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.login.LoginResponseDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.security.JwtUtil;
import mg.vinaAkoho.vina_akoho.security.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Contient toute la LOGIQUE MÉTIER du login. 
 */
@Service
public class LoginService {

    private final EmployeRepository employeRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginService(EmployeRepository employeRepository, JwtUtil jwtUtil) {
        this.employeRepository = employeRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Tente de connecter un utilisateur :
     *  1. On cherche l'employé par email
     *  2. On vérifie le mot de passe avec BCrypt
     *  3. Si tout est correct, on génère un token JWT
     *
     * RG01 : "Chaque utilisateur doit utiliser ses propres identifiants
     * de connexion" — d'où la vérification stricte email + mot de passe.
     */
    public LoginResponseDTO login(LoginRequestDTO requete) {
        Employe employe = employeRepository.findByEmail(requete.getEmail())
                .orElseThrow(() -> new IdentifiantsInvalidesException(
                        "Email ou mot de passe incorrect"));

        boolean motDePasseCorrect = PasswordHasher.verifier(requete.getMdp(), employe.getMdp());

        if (!motDePasseCorrect) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.genererToken(
                employe.getId(),
                employe.getRole().getPoste(),
                employe.getPrenom()
        );

        return new LoginResponseDTO(
                token,
                employe.getId(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getEmail(),
                employe.getRole().getPoste()
        );
    }
}
