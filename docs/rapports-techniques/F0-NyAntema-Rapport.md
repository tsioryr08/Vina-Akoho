# Rapport technique — F0 Login

**Module :** F0 — Authentification (Login + filtre utilisateur)
**Développeur :** Ny Antema
**Branche :** feature/ny-antema-login

---

## 1. Objectif du module

Permettre à un employé de se connecter à l'application avec son email et son mot de passe, recevoir un token d'authentification (JWT), et protéger l'accès aux autres routes de l'API en exigeant ce token (RG01 — chaque utilisateur utilise ses propres identifiants).

## 2. Structure des fichiers

```
src/main/java/mg/vinaAkoho/vina_akoho/
├── controller/login/
│   └── LoginController.java          → expose POST /api/login
├── service/login/
│   └── LoginService.java             → logique métier : vérification email/mdp, génération du token
├── repository/login/
│   ├── EmployeRepository.java        → accès BDD table employe
│   └── RoleRepository.java           → accès BDD table role
├── entity/login/
│   ├── Employe.java                  → mapping JPA table employe
│   └── Role.java                     → mapping JPA table role
├── dto/login/
│   ├── LoginRequestDTO.java          → données reçues (email, mdp)
│   └── LoginResponseDTO.java         → données renvoyées (token, infos employé)
├── dto/
│   └── ApiResponse.java              → format de réponse standard du projet (PARTAGÉ, racine dto/)
├── exception/login/
│   ├── IdentifiantsInvalidesException.java
│   └── TokenInvalideException.java
├── exception/
│   └── GlobalExceptionHandler.java   → gestion centralisée des erreurs (PARTAGÉ, racine exception/)
├── security/
│   ├── PasswordHasher.java           → hachage/vérification BCrypt
│   ├── JwtUtil.java                  → génération/vérification des tokens JWT
│   └── JwtFilter.java                → filtre qui protège les routes (le "filtre user" du Sprint1)
└── config/
    └── FilterConfig.java             → enregistrement explicite du filtre sur /api/*

Ajouts récents (intégration front-end) :
- `controller/login/LoginViewController.java` → sert la page `index.html` sur `/`, `/login` et chemins historiques (`/index`, `/index.html`, `/index.php`).
- Frontend : `src/main/resources/templates/index.html` + `static/assets/js/login.js` (script renommé depuis `auth.js`) — `login.js` gère la requête vers `/api/login`, stocke le token en `localStorage` et redirige selon le rôle.
```

> Note : `dto/ApiResponse.java` et `exception/GlobalExceptionHandler.java` sont placés à la racine de leur couche (pas dans `login/`) car ils sont conçus pour être réutilisés par tous les modules du projet, pas seulement le login.

## 3. Dépendances ajoutées au pom.xml

| Dépendance | Rôle |
|---|---|
| `org.mindrot:jbcrypt:0.4` | Hachage des mots de passe (BCrypt) |
| `com.auth0:java-jwt:4.4.0` | Génération et vérification des tokens JWT |

Aucune dépendance Spring Security n'a été ajoutée (choix délibéré pour garder le projet léger — auth gérée "à la main" via un Filter classique).

## 4. Fonctions principales (signatures Java)

### LoginService
```java
public LoginResponseDTO login(LoginRequestDTO requete)
```
Cherche l'employé par email, vérifie le mot de passe avec BCrypt, génère et renvoie un token JWT avec les infos de l'employé. Lève `IdentifiantsInvalidesException` si l'email n'existe pas ou si le mot de passe est incorrect (même message dans les deux cas, par sécurité — on ne révèle pas si un email existe en base).

### JwtUtil
```java
public String genererToken(Integer idEmploye, String role, String nom)
public DecodedJWT verifierEtDecoder(String token)
public Integer extraireIdEmploye(DecodedJWT decoded)
public String extraireRole(DecodedJWT decoded)
```
Encapsule toute la logique JWT (signature HMAC256, expiration, extraction des claims).

### PasswordHasher
```java
public static String hash(String motDePasseEnClair)
public static boolean verifier(String motDePasseEnClair, String hashStocke)
```
Classe utilitaire statique (pas un bean Spring) pour le hachage BCrypt.

### JwtFilter
```java
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
```
S'exécute sur toutes les routes `/api/*` (sauf `/api/login`, déclarée publique). Vérifie la présence et la validité de l'en-tête `Authorization: Bearer <token>`. Si valide, place `idEmploye` et `role` en attributs de la requête pour les controllers suivants. Sinon, renvoie directement une erreur 401 au format `ApiResponse` standard.

## 5. Logique métier expliquée

1. Le frontend envoie `POST /api/login` avec `{ "email": "...", "mdp": "..." }`
2. `LoginController` valide le format (Jakarta `@NotBlank`) et délègue à `LoginService`
3. `LoginService` cherche l'employé par email dans `EmployeRepository`. Si absent → exception.
4. Le mot de passe envoyé en clair est comparé au hash stocké via `PasswordHasher.verifier()` (BCrypt re-hache avec le même salt et compare)
5. Si correct, `JwtUtil.genererToken()` crée un token contenant : id employé (subject), rôle, prénom, date d'expiration — signé avec la clé secrète définie dans `application.properties` (`jwt.secret`)
6. Le token est renvoyé au frontend dans `LoginResponseDTO`
7. Pour toute requête suivante vers une route protégée, le frontend doit renvoyer ce token dans l'en-tête `Authorization: Bearer <token>`
8. `JwtFilter` intercepte la requête, vérifie le token avant même que le Controller ne s'exécute

## 6. Dépendances avec les autres modules

- **Table `role`** : doit être pré-remplie avec les 7 rôles RO01–RO07 avant de pouvoir créer un employé (clé étrangère `id_role` obligatoire). Voir `GUIDE.md` §2.6.

Configuration DB : Le fichier `application.properties` a été aligné sur la convention d'équipe (utilisateur et base `vinakoho`) — vérifiez que la base et l'utilisateur `vinakoho` existent en local ou en CI avant de lancer l'application.
- **Tous les autres modules (F1 Produits, F2 Matières premières, F5 Clients, etc.)** : leurs controllers passeront automatiquement par `JwtFilter` puisqu'il est enregistré sur `/api/*`. Ils peuvent récupérer l'employé connecté et son rôle via :
  ```java
  Integer idEmploye = (Integer) request.getAttribute(JwtFilter.ATTRIBUT_ID_EMPLOYE);
  String role = (String) request.getAttribute(JwtFilter.ATTRIBUT_ROLE);
  ```
  Utile par exemple pour les futures règles d'autorisation par rôle (ex. seul l'Administrateur peut créer un compte — RG01), à implémenter dans chaque module concerné.

## 7. Tests effectués

- **Tests unitaires automatisés** (`mvn test`) : 14 tests, 0 échec — couvrant `LoginService`, `JwtUtil`, `PasswordHasher`.
- **Tests manuels via curl** (voir `docs/cahiers-de-test/F0-NyAntema-Test.md`) : connexion valide, mot de passe incorrect, email inexistant, champs vides, et protection des routes par `JwtFilter` (sans token / token invalide / token valide), réalisés à l'aide d'un controller temporaire de test (`PingController`, supprimé avant la fin de la tâche).

## 8. Limites actuelles / pistes d'amélioration future

- Pas encore de gestion des rôles/permissions au niveau du filtre (le filtre vérifie seulement "es-tu connecté", pas "as-tu le droit de faire CETTE action précise") — à ajouter module par module selon RO01–RO07.
- Pas de endpoint de déconnexion explicite : avec un JWT stateless, la déconnexion se fait normalement côté frontend (suppression du token stocké), il n'y a rien à invalider côté serveur sauf si on ajoute une liste noire de tokens (non nécessaire pour ce sprint).
- Pas encore d'endpoint pour qu'un Administrateur crée un compte employé (RG01 : "Seul l'administrateur peut créer, modifier ou supprimer des comptes utilisateurs") — à discuter si ça fait partie du périmètre F0 ou d'un module séparé de gestion des utilisateurs.
- Incohérence à trancher en équipe entre `Regles_Backend_VINA-AKOHO.md` (base/utilisateur `vinakoho`) et `application.properties` actuel (base `vina_akoho`, utilisateur `postgres`) — voir `GUIDE.md` §2.4.