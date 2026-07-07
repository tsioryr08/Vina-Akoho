# 📄 Rapport technique — Sprint 3 Dashboard Administrateur

---

Développeur : Tsiory (Architecte Backend)
Sprint : 3
Date : 07/07/2026

---

## Structure des fichiers

```
controller/admin/AdminViewController.java
controller/admin/EmployeAdminController.java
controller/dashboard/DashboardController.java  ← modifié

service/admin/EmployeAdminService.java

dto/admin/EmployeAdminDTO.java
dto/admin/CreerEmployeDTO.java
dto/admin/ModifierEmployeDTO.java
dto/admin/ReinitialisationMdpDTO.java

exception/admin/EmployeNotFoundException.java
exception/admin/EmailDejaUtiliseException.java
exception/admin/MdpIdentifiqueException.java

entity/login/Employe.java          ← ajout champs actif + derniereConnexion
repository/login/EmployeRepository.java  ← ajout méthodes search/filtre

templates/dashboard/admin/index.html    ← données réelles + dernière connexion
templates/dashboard/admin/employes.html ← nouvelle page liste
templates/dashboard/admin/employe-form.html ← nouvelle page formulaire
templates/layout/admin.html             ← lien A2 corrigé

db/migration/V24__add_actif_emp.sql
db/migration/V25__modif_col_actif_emp.sql
db/migration/V26__add_derniere_connexion_employe.sql
```

---

## Endpoints

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/admin/employes` | Page liste employés |
| GET | `/admin/employes/nouveau` | Formulaire création |
| GET | `/admin/employes/{id}/modifier` | Formulaire modification |
| GET | `/api/admin/employes/actifs` | Liste actifs (JSON) |
| GET | `/api/admin/employes/desactives` | Liste désactivés (JSON) |
| GET | `/api/admin/employes/recherche?terme=X` | Recherche (JSON) |
| GET | `/api/admin/employes/par-role?idRole=X` | Filtre par rôle (JSON) |
| POST | `/api/admin/employes` | Créer un compte |
| PUT | `/api/admin/employes/{id}` | Modifier un compte |
| PATCH | `/api/admin/employes/{id}/desactiver` | Désactiver |
| PATCH | `/api/admin/employes/{id}/reactiver` | Réactiver |
| PATCH | `/api/admin/employes/{id}/reinitialiser-mdp` | Réinitialiser MDP |

---

## Fonctions principales

### `EmployeAdminService.creer(CreerEmployeDTO)`
Crée un nouveau compte employé. Vérifie que l'email n'est pas déjà utilisé
(`EmailDejaUtiliseException`), hashe le mot de passe via `PasswordHasher.hash()`,
initialise `actif = true` et `createdAt`.

### `EmployeAdminService.modifier(Integer id, ModifierEmployeDTO)`
Modifie nom, prénom, email, contact et rôle. Vérifie l'unicité de l'email
uniquement si celui-ci a changé.

### `EmployeAdminService.desactiver(Integer id)` / `reactiver(Integer id)`
Soft delete — passe `actif` à `false`/`true` sans supprimer l'enregistrement.
Préserve l'intégrité référentielle (mouvements de stock, ventes, etc.).

### `EmployeAdminService.reinitialiserMdp(Integer id, ReinitialisationMdpDTO)`
Vérifie que le nouveau mot de passe est différent de l'ancien via
`PasswordHasher.verifier()` (`MdpIdentifiqueException` sinon).
Hashe le nouveau mot de passe via `PasswordHasher.hash()` avant sauvegarde.

### `EmployeRepository.rechercher(String terme)`
Requête JPQL insensible à la casse sur nom, prénom et email avec `LIKE`.

### `LoginService.login()` — modifié
Ajout de `employe.setDerniereConnexion(LocalDateTime.now())` après
authentification réussie pour tracer la dernière connexion.

---

## Logique métier

**Soft delete :** la suppression physique est volontairement absente.
Un employé désactivé (`actif = false`) ne peut plus se connecter mais
ses données historiques (mouvements, ventes) restent intactes en base.

**Hachage BCrypt :** tous les mots de passe passent par `PasswordHasher.hash()`
(10 rounds BCrypt) à la création et à la réinitialisation.

**Dernière connexion :** champ `derniere_connexion TIMESTAMP` mis à jour
à chaque login réussi. Affiché en temps relatif ("Il y a X min/h/jours")
via JavaScript dans le dashboard `/admin`.

---

## Migrations Flyway

| Version | Description |
|---------|-------------|
| V24 | `ALTER TABLE employe ADD COLUMN actif SMALLINT DEFAULT 1` |
| V25 | Conversion `actif SMALLINT → BOOLEAN` (DROP DEFAULT + ALTER TYPE + SET DEFAULT TRUE) |
| V26 | `ALTER TABLE employe ADD COLUMN derniere_connexion TIMESTAMP` |

---

## Dépendances avec les autres modules

| Module | Dépendance |
|--------|-----------|
| F0 Login (Ny Antema) | `LoginService` modifié pour `derniereConnexion`, filtre session requis |
| `PasswordHasher` (Ny Antema) | Utilisé pour hash et vérification BCrypt |
| `GlobalExceptionHandler` | Ajout handlers `EmployeNotFoundException`, `EmailDejaUtiliseException`, `MdpIdentifiqueException` |
