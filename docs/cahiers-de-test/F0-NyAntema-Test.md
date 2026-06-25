# Cahier de test — F0 Login

**Module :** F0 — Authentification (Login)
**Développeur :** Ny Antema
**Branche :** feature/ny-antema-login

---

## Test 1 — Connexion avec identifiants corrects

Date : 23 juin 2026, 11:23
Testeur : Ny Antema
Page : POST /api/login
Identifiants : admin@vinaakoho.mg / admin123

Résultat attendu : Code 200, `success: true`, un token JWT présent dans `data.token`, et les infos de l'employé (nom, prénom, email, rôle) renvoyées.

Résultat obtenu : Code 200, `success: true`, token JWT généré correctement, infos utilisateur retournées conformément au contrat API.

Statut :
☑ Succès
☐ Échec

Commentaire : Authentification fonctionnelle et conforme.

Front-end : La page de login `src/main/resources/templates/index.html` a été intégrée au projet. Le script d'authentification a été renommé `login.js` et réalise maintenant la redirection vers une page dépendant du rôle (ex: `/achats`, `/production`, `/comptabilite`, `/stock`, `/commercial`).


inserer les donnees (docs/sql/insert_login.sql)
```bash
mvn spring-boot:run
```
demarrer le serveur en (http://localhost:8080)

```
curl -i -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@vinaakoho.mg","mdp":"admin123"}'

HTTP/1.1 200
Content-Type: application/json

{"success":true,"message":"Connexion réussie","data":{"token":"eyJ...","idEmploye":1,"nom":"Admin","prenom":"Sys","email":"admin@vinaakoho.mg","role":"ADMIN"}}
```

---

## Test 2 — Connexion avec mot de passe incorrect

Date : 23 juin 2026
Testeur : Ny Antema
Page : POST /api/login
Identifiants : admin@vinaakoho.mg / faux123

Résultat attendu : Code 401, `success: false`, message "Email ou mot de passe incorrect", `data: null`.

Résultat obtenu : Code 401, `success: false`, message "Email ou mot de passe incorrect", `data: null`.

Statut :
☑ Succès
☐ Échec

```
curl -i -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@vinaakoho.mg","mdp":"faux123"}'

HTTP/1.1 401
{"success":false,"message":"Email ou mot de passe incorrect","data":null}
```

---

## Test 3 — Connexion avec email inexistant

Date : 23 juin 2026
Testeur : Ny Antema
Page : POST /api/login
Identifiants : personne@vinaakoho.mg / x

Résultat attendu : Code 401, message **identique** à celui du Test 2 (ne pas révéler si un email existe).

Résultat obtenu : Code 401, `success: false`, message strictement identique au Test 2.

Statut :
☑ Succès
☐ Échec

```
curl -i -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"personne@vinaakoho.mg","mdp":"x"}'

HTTP/1.1 401
{"success":false,"message":"Email ou mot de passe incorrect","data":null}
```

---

## Test 4 — Connexion avec champs vides

Date : 23 juin 2026
Testeur : Ny Antema
Page : POST /api/login
Identifiants : email vide / mdp vide

Résultat attendu : Code 400, message de validation Jakarta ("L'email est obligatoire" ou "Le mot de passe est obligatoire").

Résultat obtenu : Code 400, message exact reçu : "L'email est obligatoire", `success: false`, `data: null`.

Statut :
☑ Succès
☐ Échec

Commentaire : Sur deux champs vides simultanément, c'est la première erreur de validation rencontrée (l'email) qui est renvoyée. Comportement cohérent avec `GlobalExceptionHandler`.

```
curl -i -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"","mdp":""}'

HTTP/1.1 400
{"success":false,"message":"L'email est obligatoire","data":null}
```

---

## Test 5 — Accès à une route protégée sans token

Date : 23 juin 2026
Testeur : Ny Antema
Page : GET /api/ping (sans en-tête Authorization)

Résultat attendu : Code 401, message "Authentification requise. En-tête Authorization manquant."

Résultat obtenu : Code 401, accès refusé correctement.

Statut :
☑ Succès
☐ Échec

Commentaire : Le module F0 ne possède pas encore de route métier protégée (seule `/api/login`, publique, existe). Un controller temporaire `PingController` (`GET /api/ping`) a été créé uniquement pour valider `JwtFilter` sur une route protégée réelle. **Ce fichier sera supprimé avant la Pull Request** — il ne fait pas partie des livrables du module.

```
curl -i http://localhost:8080/api/ping

HTTP/1.1 401
{"success": false, "message": "Authentification requise. En-tête Authorization manquant.", "data": null}
```

---

## Test 6 — Accès à une route protégée avec un token invalide

Date : 23 juin 2026
Testeur : Ny Antema
Page : GET /api/ping
Authorization : Bearer ceci.nest.pasvalide

Résultat attendu : Code 401, message "Token invalide ou expiré"

Résultat obtenu : Code 401, token rejeté correctement.

Statut :
☑ Succès
☐ Échec

```
curl -i http://localhost:8080/api/ping \
  -H "Authorization: Bearer ceci.nest.pasvalide"

HTTP/1.1 401
{"success": false, "message": "Token invalide ou expiré", "data": null}
```

---

## Test 7 — Accès à une route protégée avec un token valide

Date : 23 juin 2026
Testeur : Ny Antema
Page : GET /api/ping
Authorization : Bearer <token valide obtenu via Test 1>

Résultat attendu : Code 200, accès autorisé.

Résultat obtenu : Code 200, accès validé avec succès.

Statut :
☑ Succès
☐ Échec

Commentaire : Un nouveau token a été généré juste avant ce test (le précédent datait d'un cycle de redémarrage du serveur antérieur — voir Bug n°2 ci-dessous).

```
curl -i -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@vinaakoho.mg","mdp":"admin123"}'

HTTP/1.1 200
{"success":true,"message":"Connexion réussie","data":{"token":"eyJ...","idEmploye":1,"nom":"Admin","prenom":"Sys","email":"admin@vinaakoho.mg","role":"ADMIN"}}

curl -i http://localhost:8080/api/ping \
  -H "Authorization: Bearer eyJ..."

HTTP/1.1 200
{"success":true,"message":"pong","data":"Route protegee atteinte avec succes"}
```

---

## Test 8 — Accès à la route de login elle-même (sans token)

Date : 23 juin 2026
Testeur : Ny Antema
Page : POST /api/login (sans en-tête Authorization)

Résultat attendu : Route publique accessible sans authentification.

Résultat obtenu : Confirmé par le Test 1 — la requête de login a été envoyée sans en-tête Authorization et a abouti normalement (code 200, token reçu). Cela prouve que `/api/login` est bien exemptée du filtre JWT (voir `JwtFilter.ROUTES_PUBLIQUES`).

Statut :
☑ Succès
☐ Échec

Commentaire : Ce test ne nécessite pas d'appel curl distinct — la preuve est identique à celle du Test 1.

---

## Bugs identifiés en cours de test

| # | Description | Gravité | Statut |
|---|---|---|---|
| 1 | Incohérence entre `Regles_Backend_VINA-AKOHO.md` (base/utilisateur `vinakoho`) et `application.properties` (base `vina_akoho`, utilisateur `postgres`) | Moyen — bloque l'harmonisation en équipe, pas le fonctionnement local | À trancher avec l'équipe avant de pousser `application.properties` |
| 2 | 500 "Une erreur interne est survenue" obtenu lors du premier essai du Test 7 sur `/api/ping` | Faux positif | Résolu — causé par un appel testé avant le redémarrage du serveur suite à l'ajout de `PingController.java`. Aucune ligne d'erreur correspondante dans les logs serveur, ce qui confirme que le contrôleur n'était pas encore chargé au moment de l'appel. Confirmé résolu après redémarrage propre de `mvn spring-boot:run`. |
| 3 | Deux classes `LoginServiceTest` déclarées dans le même package (`LoginServiceTest.java` et `LoginServletTest.java`, doublon résiduel d'une correction antérieure) trouvées dans une archive de travail | Bloquant si présent | Non présent dans la copie de travail actuelle (confirmé par `mvn test` réussi avec exactement 4 fichiers de test compilés) — à surveiller si le fichier reapparaît après un merge |

---

## Exécution automatique des tests unitaires

- Commande exécutée : `mvn test`
- Résultat : **BUILD SUCCESS**
- Tests exécutés : 14
- Échecs : 0
- Erreurs : 0
- Skipped : 0
- Classes testées : `VinaAkohoApplicationTests`, `JwtUtilTest`, `PasswordHasherTest`, `LoginServiceTest`



---

## Commande de reproduction

```bash
mvn test
mvn spring-boot:run
```
## Remarque 
j'ai supprimer pingcontroller apres les tests car c'etait juste util pour le test 