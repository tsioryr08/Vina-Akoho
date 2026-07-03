package mg.vinaAkoho.vina_akoho.controller.livraison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import mg.vinaAkoho.vina_akoho.dto.livraison.FichierLivraisonDTO;

@Controller
@RequestMapping("/api/livraisons/fichiers")
public class LivraisonViewController {

    private static final Path DOSSIER_LIVRAISON = Paths.get("uploads/livraison").toAbsolutePath().normalize();

    public LivraisonViewController() {
        try {
            Files.createDirectories(DOSSIER_LIVRAISON);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier des fichiers de livraison : " + DOSSIER_LIVRAISON, e);
        }
    }

    @ModelAttribute("fichiersLivraison")
    public List<FichierLivraisonDTO> listerFichiersLivraison() {
        return listerTous();
    }

    @GetMapping
    public String listerFichiers(Model model) {
        model.addAttribute("fichiers", listerTous());
        return "livraison/documents";
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<FileSystemResource> servirFichier(@PathVariable String filename, HttpServletRequest request) {
        try {
            Path filePath = DOSSIER_LIVRAISON.resolve(filename).normalize();
            if (!filePath.startsWith(DOSSIER_LIVRAISON) || !Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            FileSystemResource resource = new FileSystemResource(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public List<FichierLivraisonDTO> listerTous() {
        if (!Files.isDirectory(DOSSIER_LIVRAISON)) {
            return new ArrayList<>();
        }
        try (Stream<Path> stream = Files.list(DOSSIER_LIVRAISON)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        String nom = path.getFileName().toString();
                        return FichierLivraisonDTO.builder()
                                .nom(nom)
                                .url("/api/livraisons/fichiers/" + nom)
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
