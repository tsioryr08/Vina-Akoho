#  Rapport technique — F3 Sortie Matières Premières

---

Développeur : Tsiory (Architecte Backend)
Sprint : 2
Date : 30/06/2026

---

## Structure des fichiers

```
controller/stockmp/SortieMpController.java
service/stockmp/SortieMpService.java
dto/stockmp/SortieMpRequestDTO.java
dto/stockmp/MouvementStockMpDTO.java
exception/stockmp/StockInsuffisantException.java
exception/stockmp/TypeMouvementNotFoundException.java

-- Classes partagées avec F2 (Rary) :
entity/matierespremieres/LotMp.java
entity/matierespremieres/MouvementStockMp.java
entity/matierespremieres/TypeMouvement.java
repository/matierespremieres/LotMpRepository.java
repository/matierespremieres/MouvementStockMpRepository.java
repository/matierespremieres/TypeMouvementRepository.java
```

---

## Endpoint

| Méthode | URL | Auth |
|---------|-----|------|
| POST | `/api/stock-mp/sortie` | Session requise (filtre F0) |

Corps de la requête (`SortieMpRequestDTO`) :
```json
{
  "idCategorie": 1,
  "idEmploye": 1,
  "referenceDocument": "REF-001"
}
```

---

## Fonctions principales

### `SortieMpService.effectuerSortie(SortieMpRequestDTO requete)`
Fonction centrale du module. Exécute la sortie automatique des MP selon la recette active.

Étapes internes :
1. Récupère l'employé (`employeRepository.findById`)
2. Récupère le type mouvement "Sortie" (`typeMouvementRepository.findByLibelle`)
3. Lit la recette active de la catégorie (`recetteProduitService.getRecetteActive`)
4. Pour chaque ligne de recette :
   - Récupère les lots disponibles triés par `date_achat ASC` (FIFO)
   - Vérifie que le stock total est suffisant → `StockInsuffisantException` sinon
   - Déduit lot par lot jusqu'à atteindre la quantité nécessaire
   - Sauvegarde le lot mis à jour (`lotMpRepository.save`)
   - Crée un `MouvementStockMp` de type Sortie (`mouvementStockMpRepository.save`)
5. Retourne la liste des mouvements créés en `MouvementStockMpDTO`

### `LotMpRepository.findByMatierePremiereIdAndQuantiteRestanteGreaterThanOrderByDateAchatAsc`
Requête Spring Data JPA qui filtre les lots d'une MP ayant encore du stock (`quantiteRestante > 0`), triés du plus ancien au plus récent — implémente le tri FIFO.

### `SortieMpController.sortie(@Valid @RequestBody SortieMpRequestDTO)`
Point d'entrée HTTP. Délègue entièrement au service. Retourne une `ApiResponse<List<MouvementStockMpDTO>>`.

---

## Logique métier

**Algorithme FIFO :**
- On lit les lots d'une MP triés par `date_achat ASC`
- On consomme le lot le plus ancien en premier
- Si sa `quantiteRestante` ne suffit pas, on passe au suivant
- On continue jusqu'à avoir déduit la totalité de la quantité requise par la recette
- Un `MouvementStockMp` de type "Sortie" est créé pour chaque lot touché

**Validation :**
- Vérification du stock total avant toute modification (pour éviter une déduction partielle en cas d'insuffisance)
- `@NotNull` sur `idCategorie` et `idEmploye` dans le DTO

---

## Dépendances avec les autres modules

| Module | Dépendance |
|--------|-----------|
| F0 Login (Ny Antema) | Filtre d'authentification — session requise sur tous les endpoints |
| F2 Matières premières (Rary) | `LotMp`, `MouvementStockMp`, `TypeMouvement` et leurs repositories partagés |
| RecetteProduit (Tsiory) | `RecetteProduitService.getRecetteActive(idCategorie)` pour lire les quantités MP requises |

---

## Résolution de conflits de beans (Sprint 2)

Au démarrage du Sprint 2, un `BeanDefinitionOverrideException` empêchait le lancement de l'application car `LotMp`, `LotMpRepository` et `MouvementStockMp` existaient en double dans les packages `stockmp` et `matierespremieres`.

**Résolution :**
- Suppression de `entity/stockmp/LotMp.java`
- Suppression de `entity/stockmp/MouvementStockMp.java`
- Suppression de `repository/stockmp/LotMpRepository.java`
- Conservation et complétion des classes de Rary (`matierespremieres`)
- Mise à jour des imports dans `SortieMpService` vers `repository.matierespremieres`
