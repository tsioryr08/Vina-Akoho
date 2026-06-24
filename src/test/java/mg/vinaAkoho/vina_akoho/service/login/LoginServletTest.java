package mg.vinaAkoho.vina_akoho.service.login;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.login.LoginResponseDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.login.Role;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.security.JwtUtil;
import mg.vinaAkoho.vina_akoho.security.PasswordHasher;


@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginService loginService;

    private Employe employeDeTest;
    private Role roleAdministrateur;

    @BeforeEach
    void setUp() {
        roleAdministrateur = new Role("Administrateur");
        roleAdministrateur.setId(1);

        employeDeTest = new Employe();
        employeDeTest.setId(10);
        employeDeTest.setNom("Rakoto");
        employeDeTest.setPrenom("Ny Antema");
        employeDeTest.setEmail("ny.antema@vinaakoho.mg");
        employeDeTest.setMdp(PasswordHasher.hash("motDePasseCorrect"));
        employeDeTest.setRole(roleAdministrateur);
    }

    @Test
    void login_doitReussirAvecEmailEtMotDePasseCorrects() {
        LoginRequestDTO requete = new LoginRequestDTO("ny.antema@vinaakoho.mg", "motDePasseCorrect");

        when(employeRepository.findByEmail("ny.antema@vinaakoho.mg"))
                .thenReturn(Optional.of(employeDeTest));
        when(jwtUtil.genererToken(10, "Administrateur", "Ny Antema"))
                .thenReturn("faux-token-jwt-genere");

        LoginResponseDTO reponse = loginService.login(requete);

        assertNotNull(reponse);
        assertEquals("faux-token-jwt-genere", reponse.getToken());
        assertEquals(10, reponse.getIdEmploye());
        assertEquals("Rakoto", reponse.getNom());
        assertEquals("Ny Antema", reponse.getPrenom());
        assertEquals("Administrateur", reponse.getRole());
    }

    @Test
    void login_doitEchouerSiEmailInexistant() {
        LoginRequestDTO requete = new LoginRequestDTO("inconnu@vinaakoho.mg", "nimporteQuoi");

        when(employeRepository.findByEmail("inconnu@vinaakoho.mg"))
                .thenReturn(Optional.empty());

        IdentifiantsInvalidesException exception = assertThrows(
                IdentifiantsInvalidesException.class,
                () -> loginService.login(requete)
        );
        assertEquals("Email ou mot de passe incorrect", exception.getMessage());

        // On vérifie qu'on n'a JAMAIS essayé de générer un token
        // puisque l'employé n'existe même pas.
        verify(jwtUtil, never()).genererToken(any(), any(), any());
    }

    @Test
    void login_doitEchouerSiMotDePasseIncorrect() {
        LoginRequestDTO requete = new LoginRequestDTO("ny.antema@vinaakoho.mg", "mauvaisMotDePasse");

        when(employeRepository.findByEmail("ny.antema@vinaakoho.mg"))
                .thenReturn(Optional.of(employeDeTest));

        IdentifiantsInvalidesException exception = assertThrows(
                IdentifiantsInvalidesException.class,
                () -> loginService.login(requete)
        );
        assertEquals("Email ou mot de passe incorrect", exception.getMessage());

        verify(jwtUtil, never()).genererToken(any(), any(), any());
    }

    @Test
    void login_doitRenvoyerLeMemeMessageQueLEmailExisteOuNon() {
        // Vérification de sécurité explicite : un attaquant qui essaie
        // de deviner des emails valides ne doit JAMAIS voir de différence
        // entre "email inexistant" et "mot de passe incorrect".
        LoginRequestDTO requeteEmailInconnu = new LoginRequestDTO("fantome@vinaakoho.mg", "x");
        LoginRequestDTO requeteMauvaisMdp = new LoginRequestDTO("ny.antema@vinaakoho.mg", "x");

        when(employeRepository.findByEmail("fantome@vinaakoho.mg")).thenReturn(Optional.empty());
        when(employeRepository.findByEmail("ny.antema@vinaakoho.mg")).thenReturn(Optional.of(employeDeTest));

        String message1 = assertThrows(IdentifiantsInvalidesException.class,
                () -> loginService.login(requeteEmailInconnu)).getMessage();
        String message2 = assertThrows(IdentifiantsInvalidesException.class,
                () -> loginService.login(requeteMauvaisMdp)).getMessage();

        assertEquals(message1, message2);
    }
}
