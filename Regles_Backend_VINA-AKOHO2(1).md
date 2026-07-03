# 📐 Règles & Conventions Backend — VINA-AKOHO

> Document de référence pour toute l'équipe — Sprint 1
> Validé par les Architectes Backend

---

## 1. Structure des dossiers (par couche)

Toute l'équipe code dans cette structure commune (déjà pushée sur `main`) :

```
src/main/java/mg/vinaAkoho/vina_akoho/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── exception/
```

### Sous-dossiers par module — OBLIGATOIRE

À l'intérieur de chaque couche, on crée un **sous-dossier par module/fonctionnalité** pour éviter de se perdre dans une structure trop chargée (9 développeurs dans les mêmes dossiers sinon ça devient illisible).

**Format :** `<couche>/<nom-module>/NomClasse.java`

**Exemple concret pour le module Matières premières :**

```
controller/matieres-premieres/MatierePremiereController.java
service/matieres-premieres/MatierePremiereService.java
repository/matieres-premieres/MatierePremiereRepository.java
entity/matieres-premieres/MatierePremiere.java
dto/matieres-premieres/MatierePremiereDTO.java
exception/matieres-premieres/StockInsuffisantException.java
```

**Autre exemple pour le module Produits :**

```
controller/produits/ProduitController.java
service/produits/ProduitService.java
repository/produits/ProduitRepository.java
entity/produits/Produit.java
dto/produits/ProduitDTO.java
```

Chacun crée son sous-dossier selon le nom de son module (en kebab-case, cohérent avec les URLs API — voir section 4).

---

## 2. Exceptions personnalisées

> **Aucune `RuntimeException` brute n'est autorisée.** Chaque développeur crée ses propres exceptions selon les erreurs rencontrées dans son module, dans le dossier `exception/`.

### Règles à respecter

- Toute exception métier doit hériter de `RuntimeException`
- Le nom doit être clair et explicite (ex. `StockInsuffisantException`, `LotPerimeException`, `MatierePremiereNotFoundException`)
- Chaque exception doit avoir un constructeur avec un message clair destiné à l'utilisateur final
- Les exceptions sont à créer **au fur et à mesure** de l'avancement — pas besoin de tout anticiper à l'avance

### Exemple de structure

```java
public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(String message) {
        super(message);
    }
}
```

### Gestion centralisée

Un `GlobalExceptionHandler.java` (avec `@RestControllerAdvice`) doit intercepter toutes les exceptions et les transformer dans le format `ApiResponse` standard (voir section 3) avant de les renvoyer au frontend.

---

## 3. Format de réponse API standard

Toute réponse API, succès ou erreur, doit suivre **ce format unique** :

```json
{
  "success": true,
  "message": "Opération réussie",
  "data": { ... }
}
```

En cas d'erreur :

```json
{
  "success": false,
  "message": "Stock insuffisant pour cette commande",
  "data": null
}
```

---

## 4. Conventions de nommage

| Élément      | Convention                         | Exemple                     |
| ------------ | ---------------------------------- | --------------------------- |
| Entités      | PascalCase                         | `MatierePremiere`           |
| DTOs         | PascalCase + DTO                   | `MatierePremiereDTO`        |
| Controllers  | PascalCase + Controller            | `MatierePremiereController` |
| Services     | PascalCase + Service               | `MatierePremiereService`    |
| Repositories | PascalCase + Repository            | `MatierePremiereRepository` |
| URLs API     | kebab-case                         | `/api/matieres-premieres`   |
| Fonction     | camelCase en français              | `creerUser`                 |
| Variable     | camelCase en français              | `nomUser`                   |
| Constantes   | MAJUSCULES_AVEC_UNDERSCORE         | `MAX_RETRY_COUNT`           |
| Table        | snake_case, singulier, en français | `matiere_premiere`          |
| Colonne      | snake_case, singulier, en français | `date_creation`             |

---

## 5. Règles obligatoires pour tout le monde

- ✅ **DTOs obligatoires** — aucune entité ne doit être directement exposée à l'API
- ✅ **Jakarta Validation obligatoire** sur tous les DTOs (`@NotNull`, `@NotBlank`, `@Min`, etc.)
- ✅ **Exceptions personnalisées obligatoires** — voir section 2
- ✅ **Tests unitaires obligatoires** avant chaque merge

---

## 6. Workflow Git

### Stratégie de branches

```
main   ← version stable, validée par Pull Request uniquement
dev    ← intégration de toutes les features, validée par Pull Request
feature/xxx  ← branche personnelle de chaque développeur
```

### Création de sa branche personnelle

**Format obligatoire :** `feature/prenom-module`

```bash
git checkout dev
git pull origin dev
git checkout -b feature/prenom-module
```

**Exemples concrets (Sprint 1) :**

| Module                | Nom de branche                    |
| --------------------- | --------------------------------- |
| F0 Login              | `feature/ny-antema-login`         |
| F1 Produits           | `feature/nekena-produits`         |
| F2 Matières premières | `feature/rary-matieres-premieres` |
| F5 Clients            | `feature/armando-clients`         |
| recette_produit       | `feature/tsiory-recette-produit`  |

### Règle d'or — Toujours se mettre à jour avant de commencer

**Avant de commencer à coder une fonctionnalité, chaque développeur doit récupérer la dernière version de `dev` :**

```bash
git checkout feature/mon-module
git pull origin dev
```

Ça évite de travailler sur une version obsolète, surtout pour les modules qui dépendent d'un autre module (ex. Recettes dépend de Matières premières). Si un conflit apparaît, c'est au développeur de le résoudre lui-même à ce moment-là, tranquillement.

### Processus — Envoyer son travail dans dev

> ⚠️ **Seuls les Architectes Backend peuvent merger dans 'main' via Pull Request**

1. Le développeur termine sa fonctionnalité sur `feature/xxx` et teste que ça marche
2. Il fait `git pull origin dev` et résout les conflits s'il y en a
3. Il merge sa branche dans dev en local :
   git checkout dev
   git merge feature/xxx
4. Il pousse dev sur GitHub :
   git push origin dev
5. Il prévient l'équipe dans le groupe que son module est dans `dev`
6. Une fois `dev` stable, les Architectes Backend ensemble ouvrent une Pull Request de `dev` vers `main` et la valident

### Format des commits — OBLIGATOIRE

Pour ne pas se perdre dans l'historique, chaque commit doit respecter ce format :

| Type      | Usage                   |
| --------- | ----------------------- |
| `feat:`   | Nouvelle fonctionnalité |
| `fix:`    | Correction de bug       |
| `config:` | Configuration           |
| `test:`   | Ajout de tests          |
| `docs:`   | Documentation           |
| `style:`  | Mise en forme           |

**Exemple :**

```
feat: ajoute endpoint CRUD matières premières
fix: corrige validation seuil négatif
```

---

## 7. Base de données — PostgreSQL

### Conventions de nommage

- **Tables** : minuscules, singulier, snake_case (ex. `user`, `order_item`)
- **Colonnes** : snake_case (ex. `first_name`, `created_at`, `updated_at`)

### Configuration

```properties
spring.jpa.hibernate.ddl-auto=update
```

### Utilisateur et base de données dédiés

| Paramètre       | Valeur     |
| --------------- | ---------- |
| Utilisateur     | `vinakoho` |
| Mot de passe    | `vinakoho` |
| Base de données | `vinakoho` |

**Script SQL (à exécuter avec un compte superutilisateur, ex. `postgres`) :**

```sql
-- 1. Création de l'utilisateur applicatif
CREATE USER vinakoho WITH PASSWORD 'vinakoho';

-- 2. Création de la base de données, appartenant à cet utilisateur
CREATE DATABASE vinakoho OWNER vinakoho;

-- 3. Attribution de tous les droits sur la base à l'utilisateur
GRANT ALL PRIVILEGES ON DATABASE vinakoho TO vinakoho;

-- 4. Connexion à la base vinakoho, puis droits sur le schéma public
\c vinakoho
GRANT ALL ON SCHEMA public TO vinakoho;

-- Grant all privileges on all current tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO vinakoho;

-- Grant privileges on sequences (crucial for auto-incrementing IDs)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO vinakoho;
```

L'application se connecte ensuite à PostgreSQL avec l'utilisateur `vinakoho`.

### Tables concernées — Sprint 1

```
matiere_premiere
categorie_produit
produit
recette_produit
client
mouvement_stock_mp
depenses
employe
role
```

---

## 8. Cahiers de test & Rapports techniques

> À fournir **à chaque fin de tâche ou de module**, avec tests inclus.

### 📋 Cahier de test

**Emplacement :** `docs/cahiers-de-test/FX-Nom-Test.md`
_(FX = numéro de la fonctionnalité concernée, ex. F1, F2, F5... — Nom = prénom du développeur)_

**Exemple concret :** `F1-Rary-Test.md`

**Format obligatoire pour chaque test effectué :**

```markdown
Date :
Testeur : (nom)
Page : (exemple : login.html)
Registration : nom/mdp (exemple admin/admin123)
Résultat attendu :
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :
```

### 📄 Rapport technique

**Emplacement :** `docs/rapports-techniques/FX-Nom-Rapport.md`

**Exemple concret :** `F0-Armando-Rapport.md`

**Contenu obligatoire :**

- Documentation technique du code
- Structure des fichiers du module

---

## 9. Intelligence Artificielle

> Il est **fortement recommandé de ne pas utiliser d'agents IA** pour générer du code, afin d'éviter que le code de certains développeurs ne soit modifié de façon incohérente avec le reste du projet.

---

## 10. Suivi & Communication

- La répartition du support entre architectes sera communiquée **ultérieurement** dans le groupe, une fois les besoins identifiés
- Un **point de débrief** sera organisé pour faire le point sur l'avancement de chacun — la date et l'heure seront communiquées dans le groupe en temps voulu
- Chaque développeur doit signaler l'avancement de ses tâches dans le groupe de discussion

---

## 11. Migration des donnees:
.Quand il y a une modification de la base comme ALTER etc , creer un fichier dans " ressources/db/migration"  et mettre les commade sql dedans
### Format du fichier:
```
Vx__schema_initial.sql
```
Avec lequelle x est le chiffre suivant du dernier .sql 
 ### NB: Ne jamais corriger ou modifier les anciens .sql  directement , toujours creer une nouvelle s'il y a une modification

_Document de référence — Architectes Backend VINA-AKOHO — Sprint 1_
