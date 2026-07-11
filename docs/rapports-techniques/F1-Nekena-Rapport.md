# RAPPORT TECHNIQUE - GESTION DES PRODUITS ET CATEGORIES

**Version:** 1.0  
**Remarque** : 
- ajout de dependance lombok pour faciliter la creation d'entite et DTO
(voir docu en ligne)
- ajout d'un script de modif pour ajouter les champs actif dans produits et categories

---

## 1. STRUCTURE DES FICHIERS

### 1.1 ENTITES (entity/produit/)

```
src/main/java/mg/vinaAkoho/vina_akoho/entity/produit/
├── Produit.java          # Entite Produit
├── Categorie.java        # Entite Catego

.

rie
```

### 1.2 DTOs (dto/produit/)

```
src/main/java/mg/vinaAkoho/vina_akoho/dto/produit/
├── ProduitDTO.java              # DTO lecture produit
├── ProduitRequestDTO.java       # DTO ecriture produit
├── CategorieDTO.java            # DTO lecture categorie
├── CategorieRequestDTO.java     # DTO ecriture categorie
```

### 1.3 REPOSITORIES (repository/produit/)

```
src/main/java/mg/vinaAkoho/vina_akoho/repository/produit/
├── ProduitRepository.java       # Repository produit
├── CategorieRepository.java     # Repository categorie
├── ProduitSpecification.java    # Specification pour filtres
```

### 1.4 SERVICES (service/produit/)

```
src/main/java/mg/vinaAkoho/vina_akoho/service/produit/
├── ProduitService.java          # Service produit
├── CategorieService.java        # Service categorie
```

### 1.5 CONTROLLERS (controller/produit/)

```
src/main/java/mg/vinaAkoho/vina_akoho/controller/produit/
├── ProduitController.java       # Controller produit
├── CategorieController.java     # Controller categorie
```

### 1.6 EXCEPTIONS (exception/produit/)

```
src/main/java/mg/vinaAkoho/vina_akoho/exception/produit/
├── ProduitNotFoundException.java
├── ProduitDejaExistantException.java
├── CategorieNotFoundException.java
├── CategorieDejaExistanteException.java
├── CategorieEnUtilisationException.java
```

### 1.7 TEMPLATES (templates/)

```
src/main/resources/templates/
├── produit/
│   ├── list.html
│   ├── detail.html
│   └── formulaire.html
└── categorie/
    ├── list.html
    ├── detail.html
    └── formulaire.html
```

---

## 2. FONCTIONS PRINCIPALES

### 2.1 SERVICE PRODUIT

```java
// Liste tous les produits actifs
public List<ProduitDTO> listerTous()

// Recherche avec filtres paginee
public Page<ProduitDTO> rechercher(String recherche, Long idCategorie, 
                                    BigDecimal prixMin, BigDecimal prixMax, 
                                    Pageable pageable)

// Recupere un produit actif par son ID
public ProduitDTO trouverParId(Long id)

// Recupere un produit meme inactif (pour modification)
public ProduitDTO trouverParIdPourModification(Long id)

// Cree un nouveau produit
public ProduitDTO creer(ProduitRequestDTO requete)

// Modifie un produit existant
public ProduitDTO modifier(Long id, ProduitRequestDTO requete)

// Desactive un produit (soft delete)
public void supprimer(Long id)

// Reactive un produit
public void reactiver(Long id)
```

### 2.2 SERVICE CATEGORIE

```java
// Liste toutes les categories actives
public List<CategorieDTO> listerToutes()

// Liste paginee des categories actives
public Page<CategorieDTO> listerToutes(Pageable pageable)

// Recherche des categories par libelle
public Page<CategorieDTO> rechercher(String search, Pageable pageable)

// Recupere une categorie active par son ID
public CategorieDTO trouverParId(Long id)

// Recupere une categorie meme inactive (pour modification)
public CategorieDTO trouverParIdPourModification(Long id)

// Cree une nouvelle categorie
public CategorieDTO creer(CategorieRequestDTO requete)

// Modifie une categorie existante
public CategorieDTO modifier(Long id, CategorieRequestDTO requete)

// Desactive une categorie (soft delete)
public void supprimer(Long id)

// Reactive une categorie
public void reactiver(Long id)
```

### 2.3 REPOSITORY PRODUIT

```java
// Requetes derivees
public List<Produit> findAllActifs()
public Page<Produit> findAllActifs(Pageable pageable)
public Optional<Produit> findByIdAndActifTrue(Long id)

// Verifications d'unicite
public boolean existsByRefIgnoreCaseAndActifTrue(String ref)
public boolean existsByNomIgnoreCaseAndActifTrue(String nom)
public boolean existsByRefIgnoreCaseAndIdNotAndActifTrue(String ref, Long id)
public boolean existsByNomIgnoreCaseAndIdNotAndActifTrue(String nom, Long id)

// Compteurs
public long countByCategorieIdAndActifTrue(Long categorieId)
```

### 2.4 REPOSITORY CATEGORIE

```java
// Requetes derivees
public List<Categorie> findAllByActifTrue()
public Page<Categorie> findAllByActifTrue(Pageable pageable)
public Optional<Categorie> findByIdAndActifTrue(Long id)
public Page<Categorie> findByLibelleContainingIgnoreCaseAndActifTrue(String search, Pageable pageable)

// Verifications d'unicite
public boolean existsByLibelleIgnoreCaseAndActifTrue(String libelle)
public boolean existsByLibelleIgnoreCaseAndIdNotAndActifTrue(String libelle, Long id)
```

---

## 3. LOGIQUE METIER EXPLIQUEE

### 3.1 SOFT DELETE

Le systeme utilise un soft delete via le champ `actif` au lieu de supprimer physiquement les donnees.

**Principe:**
- Le champ `actif` est un boolean (true = actif, false = inactif)
- Les listes affichent uniquement les elements actifs
- Les contraintes d'unicite s'appliquent uniquement sur les elements actifs
- Les elements inactifs peuvent etre reactives

**Avantages:**
- Conservation de l'historique des donnees
- Possibilite de reactiver des elements
- Pas de perte de donnees



### 3.2 FILTRAGE ET RECHERCHE

**Filtres pour les produits:**
- Recherche textuelle (ref, nom, description)
- Filtre par categorie
- Filtre par fourchette de prix (min/max)

**Filtres pour les categories:**
- Recherche par libelle
Les filtres utilisent `Specification` pour construire les requetes dynamiquement.

### 3.3 VALIDATION DES DONNEES

**Validation pour les categories:**
- Libelle obligatoire, unique, max 100 caracteres
- Marge obligatoire, >= 0
- Pourcentages entre 0 et 100
- Unicite du libelle (case-insensitive)

**Validation pour les produits:**
- Reference obligatoire, unique, max 100 caracteres
- Nom obligatoire, unique, max 150 caracteres
- Categorie obligatoire
- Prix >= 0
- Unicite de la reference et du nom (case-insensitive)

### 3.4 CONTRAINTES D'INTEGRITE

**Categorie:**
- Ne peut pas etre desactivee si elle a des produits actifs associes
- Unicite du libelle

**Produit:**
- Unicite de la reference
- Unicite du nom
---
### ENDPOINTS API

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/produits` | Liste des produits |
| GET | `/produits/recherche` | Recherche produits |
| GET | `/produits/{id}` | Detail produit |
| GET | `/produits/nouveau` | Formulaire creation |
| POST | `/produits` | Creer produit |
| GET | `/produits/{id}/modifier` | Formulaire modification |
| POST | `/produits/{id}` | Modifier produit |
| GET | `/produits/{id}/supprimer` | Desactiver produit |
| GET | `/produits/{id}/reactiver` | Reactiver produit |
| GET | `/categories` | Liste categories |
| GET | `/categories/{id}` | Detail categorie |
| GET | `/categories/nouveau` | Formulaire creation |
| POST | `/categories` | Creer categorie |
| GET | `/categories/{id}/modifier` | Formulaire modification |
| POST | `/categories/{id}` | Modifier categorie |
| GET | `/categories/{id}/supprimer` | Desactiver categorie |
| GET | `/categories/{id}/reactiver` | Reactiver categorie |
---

#