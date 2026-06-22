# Cahier de test — F0 Login

**Module :** F0 — Authentification (Login)
**Développeur :** Ny Antema
**Branche :** feature/ny-antema-login

---

## Test 1 — Connexion avec identifiants corrects

Date :
Testeur : Ny Antema
Page : POST /api/auth/login
Registration : admin@vinaakoho.mg / admin123
Résultat attendu : Code 200, `success: true`, un token JWT présent dans `data.token`, et les infos de l'employé (nom, prénom, email, rôle) renvoyées.
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 2 — Connexion avec mot de passe incorrect

Date :
Testeur : Ny Antema
Page : POST /api/auth/login
Registration : admin@vinaakoho.mg / mauvaisMotDePasse
Résultat attendu : Code 401, `success: false`, message "Email ou mot de passe incorrect", `data: null`.
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 3 — Connexion avec email inexistant

Date :
Testeur : Ny Antema
Page : POST /api/auth/login
Registration : inconnu@vinaakoho.mg / nimporte
Résultat attendu : Code 401, même message que le Test 2 (ne doit pas révéler si l'email existe ou non).
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 4 — Connexion avec champs vides

Date :
Testeur : Ny Antema
Page : POST /api/auth/login
Registration : email vide / mdp vide
Résultat attendu : Code 400, message de validation Jakarta ("L'email est obligatoire" ou "Le mot de passe est obligatoire").
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 5 — Accès à une route protégée sans token

Date :
Testeur : Ny Antema
Page : GET /api/<une-route-protegee-existante> (sans en-tête Authorization)
Résultat attendu : Code 401, message "Authentification requise. En-tête Authorization manquant."
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 6 — Accès à une route protégée avec un token invalide/expiré

Date :
Testeur : Ny Antema
Page : GET /api/<une-route-protegee-existante>, en-tête `Authorization: Bearer token_invalide_au_hasard`
Résultat attendu : Code 401, message "Token invalide ou expiré"
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 7 — Accès à une route protégée avec un token valide

Date :
Testeur : Ny Antema
Page : GET /api/<une-route-protegee-existante>, en-tête `Authorization: Bearer <token-recu-au-test-1>`
Résultat attendu : Code 200, la requête passe normalement (le filtre laisse passer)
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Test 8 — Accès à la route de login elle-même (vérifie qu'elle reste publique)

Date :
Testeur : Ny Antema
Page : POST /api/auth/login (sans en-tête Authorization)
Résultat attendu : Le filtre ne bloque pas cette route précise, contrairement aux autres (c'est la route publique de référence)
Résultat obtenu :
Statut :
☐ Succès
☐ Échec
Commentaire :

---

## Bugs identifiés

| # | Description | Gravité | Statut |
|---|---|---|---|
|   |   |   |   |

## Notes complémentaires

(à remplir après exécution réelle des tests, en local, une fois la base de données initialisée — voir GUIDE.md)
