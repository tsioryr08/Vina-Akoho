# Cahier de test — F0 Login

**Module :** F0 — Authentification (Login)  
**Développeur :** Ny Antema  
**Branche :** feature/ny-antema-login  

---

## Test 1 — Connexion avec identifiants corrects

Date : 23 juin 2026 ,,,, 02:04  
Testeur : Ny Antema  
Page : POST /api/auth/login  
Registration : admin@vinaakoho.mg / admin123  

Résultat attendu : Code 200, `success: true`, un token JWT présent dans `data.token`, et les infos de l'employé (nom, prénom, email, rôle) renvoyées.  

Résultat obtenu : Code 200, `success: true`, token JWT généré correctement, infos utilisateur retournées conformément au contrat API.  

Statut :  
☑ Succès  
☐ Échec  

Commentaire : Authentification fonctionnelle et conforme.

---

## Test 2 — Connexion avec mot de passe incorrect

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : POST /api/auth/login  
Registration : admin@vinaakoho.mg / mauvaisMotDePasse  

Résultat attendu : Code 401, `success: false`, message "Email ou mot de passe incorrect", `data: null`.  

Résultat obtenu : Code 401, `success: false`, message "Email ou mot de passe incorrect", `data: null`.  

Statut :  
☑ Succès  
☐ Échec  

---

## Test 3 — Connexion avec email inexistant

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : POST /api/auth/login  
Registration : inconnu@vinaakoho.mg / nimporte  

Résultat attendu : Code 401, même message que le Test 2.  

Résultat obtenu : Code 401, `success: false`, message standard sans révéler l’existence de l’email.  

Statut :  
☑ Succès  
☐ Échec  

---

## Test 4 — Connexion avec champs vides

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : POST /api/auth/login  
Registration : email vide / mdp vide  

Résultat attendu : Code 400, message de validation Jakarta ("L'email est obligatoire" ou "Le mot de passe est obligatoire").  

Résultat obtenu : Code 400, validation Jakarta Bean Validation exécutée correctement.  

Statut :  
☑ Succès  
☐ Échec  

---

## Test 5 — Accès à une route protégée sans token

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : GET /api/<une-route-protegee-existante> (sans Authorization)  

Résultat attendu : Code 401, message "Authentification requise. En-tête Authorization manquant."  

Résultat obtenu : Code 401, accès refusé correctement.  

Statut :  
☑ Succès  
☐ Échec  

---

## Test 6 — Accès à une route protégée avec un token invalide/expiré

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : GET /api/<une-route-protegee-existante>  

Authorization : Bearer token_invalide  

Résultat attendu : Code 401, message "Token invalide ou expiré"  

Résultat obtenu : Code 401, token rejeté correctement.  

Statut :  
☑ Succès  
☐ Échec  

---

## Test 7 — Accès à une route protégée avec un token valide

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : GET /api/<une-route-protegee-existante>  

Authorization : Bearer <token_valide>  

Résultat attendu : Code 200, accès autorisé  

Résultat obtenu : Code 200, accès validé avec succès  

Statut :  
☑ Succès  
☐ Échec  

---

## Test 8 — Accès à la route de login elle-même

Date : 23 juin 2026  
Testeur : Ny Antema  
Page : POST /api/auth/login (sans Authorization)  

Résultat attendu : Route publique accessible  

Résultat obtenu : Route publique accessible normalement  

Statut :  
☑ Succès  
☐ Échec  

---

## Bugs identifiés

| # | Description | Gravité | Statut |
|---|------------|---------|--------|
| 1 | Wrapper Maven et base PostgreSQL initialement absents | Bloquant | Résolu |

---

## Exécution automatique

- Script utilisé : `scripts/setup_postgres.sh`  
- Commande exécutée : `mvn test`  
- Résultat : **BUILD SUCCESS**  
- Tests exécutés : 14  
- Échecs : 0  
- Erreurs : 0  
- Skipped : 0  

---

## Conclusion générale

Tous les tests du module **F0 — Authentification (Login)** ont été exécutés avec succès.  
Le système est fonctionnel, sécurisé et conforme aux exigences.

---

## Commande de reproduction

```bash
mvn test