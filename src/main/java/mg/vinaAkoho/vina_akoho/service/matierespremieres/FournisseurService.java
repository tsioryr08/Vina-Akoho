package mg.vinaAkoho.vina_akoho.service.matierespremieres;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Fournisseur;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.FournisseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public FournisseurService(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }

    public List<Fournisseur> lister() {
        return fournisseurRepository.findAll();
    }

    @Transactional
    public Fournisseur creer(String nom, String email, String telephone) {
        Fournisseur f = new Fournisseur();
        f.setNom(nom);
        f.setEmail(email);
        f.setTelephone(telephone);
        return fournisseurRepository.save(f);
    }
}