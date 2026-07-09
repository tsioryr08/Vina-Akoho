package mg.vinaAkoho.vina_akoho.service.admin;

import jakarta.transaction.Transactional;
import mg.vinaAkoho.vina_akoho.dto.admin.CreerEmployeDTO;
import mg.vinaAkoho.vina_akoho.dto.admin.EmployeAdminDTO;
import mg.vinaAkoho.vina_akoho.dto.admin.ModifierEmployeDTO;
import mg.vinaAkoho.vina_akoho.dto.admin.ReinitialisationMdpDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.login.Role;
import mg.vinaAkoho.vina_akoho.exception.admin.EmailDejaUtiliseException;
import mg.vinaAkoho.vina_akoho.exception.admin.EmployeNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.login.RoleRepository;
import mg.vinaAkoho.vina_akoho.security.PasswordHasher;
import mg.vinaAkoho.vina_akoho.exception.admin.MdpIdentifiqueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeAdminService {

    private final EmployeRepository employeRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public EmployeAdminService(EmployeRepository employeRepository, RoleRepository roleRepository) {
        this.employeRepository = employeRepository;
        this.roleRepository = roleRepository;
    }

    // liste

    public List<EmployeAdminDTO> listerActifs() {
        return employeRepository.findByActif(true).stream().map(this::toDTO).toList();
    }

    public List<EmployeAdminDTO> listerDesactives() {
        return employeRepository.findByActif(false).stream().map(this::toDTO).toList();
    }

    public List<EmployeAdminDTO> listerParRole(Integer idRole) {
        return employeRepository.findByRoleId(idRole).stream().map(this::toDTO).toList();
    }

    public List<EmployeAdminDTO> listerParRoleEtStatut(Integer idRole, Boolean actif) {
        return employeRepository.findByActifAndRoleId(actif, idRole).stream().map(this::toDTO).toList();
    }

    public List<EmployeAdminDTO> rechercher(String terme) {
        return employeRepository.rechercher(terme).stream().map(this::toDTO).toList();
    }

    public List<EmployeAdminDTO> rechercherParStatut(String terme, Boolean actif) {
        return employeRepository.rechercherParStatut(terme, actif).stream().map(this::toDTO).toList();
    }

    // --- CRUD ---

    @Transactional
    public EmployeAdminDTO creer(CreerEmployeDTO dto) {
        if (employeRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDejaUtiliseException(dto.getEmail());
        }
        Role role = roleRepository.findById(dto.getIdRole())
                .orElseThrow(() -> new EmployeNotFoundException(dto.getIdRole()));

        Employe employe = new Employe();
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setEmail(dto.getEmail());
        employe.setContact(dto.getContact());
        employe.setMdp(PasswordHasher.hash(dto.getMdp()));
        employe.setRole(role);
        employe.setActif(true);
        employe.setCreatedAt(LocalDateTime.now());
        employe.setUpdatedAt(LocalDateTime.now());

        return toDTO(employeRepository.save(employe));
    }

    @Transactional
    public EmployeAdminDTO modifier(Integer id, ModifierEmployeDTO dto) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException(id));

        // Vérifier email uniquement si changé
        if (!employe.getEmail().equals(dto.getEmail()) && employeRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDejaUtiliseException(dto.getEmail());
        }

        Role role = roleRepository.findById(dto.getIdRole())
                .orElseThrow(() -> new EmployeNotFoundException(dto.getIdRole()));

        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setEmail(dto.getEmail());
        employe.setContact(dto.getContact());
        employe.setRole(role);
        employe.setUpdatedAt(LocalDateTime.now());

        return toDTO(employeRepository.save(employe));
    }

    @Transactional
    public EmployeAdminDTO desactiver(Integer id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException(id));
        employe.setActif(false);
        employe.setUpdatedAt(LocalDateTime.now());
        return toDTO(employeRepository.save(employe));
    }

    @Transactional
    public EmployeAdminDTO reactiver(Integer id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException(id));
        employe.setActif(true);
        employe.setUpdatedAt(LocalDateTime.now());
        return toDTO(employeRepository.save(employe));
    }

    @Transactional
    public void reinitialiserMdp(Integer id, ReinitialisationMdpDTO dto) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException(id));
            //verif que le nouveau mdp est diffferent de l'ancien
            if (PasswordHasher.verifier(dto.getNouveauMdp(), employe.getMdp())) {
                throw new MdpIdentifiqueException(
                "Le nouveau mot de passe doit être différent de l'ancien");
            }

        employe.setMdp(PasswordHasher.hash(dto.getNouveauMdp()));
        employe.setUpdatedAt(LocalDateTime.now());
        employeRepository.save(employe);
    }

    // --- Mapping ---

    private EmployeAdminDTO toDTO(Employe e) {
        EmployeAdminDTO dto = new EmployeAdminDTO();
        dto.setId(e.getId());
        dto.setNom(e.getNom());
        dto.setPrenom(e.getPrenom());
        dto.setEmail(e.getEmail());
        dto.setContact(e.getContact());
        dto.setRole(e.getRole().getPoste());
        dto.setActif(e.getActif());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}