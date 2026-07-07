# 📋 Cahier de test — Sprint 3 Dashboard Administrateur

---

Date : 07/07/2026
Testeur : Tsiory
Module : Dashboard Admin — Gestion des comptes utilisateurs
Registration : admin@vinaakoho.mg / admin123

---

## Test 1 — Accès via sidebar

Résultat attendu : Clic sur "Gestion des comptes A2" → redirection vers `/admin/employes`
Résultat obtenu : Page liste chargée avec tableau des employés actifs et 3 KPIs
Statut : ☑ Succès

---

## Test 2 — Liste des actifs

Résultat attendu : Tableau affiche les 7 employés actifs de la base
Résultat obtenu : 7 employés affichés avec nom, prénom, rôle, statut
Statut : ☑ Succès

---

## Test 3 — Tab Désactivés

Action : Clic sur tab "Désactivés"
Résultat attendu : Tableau vide (aucun désactivé au départ)
Résultat obtenu : "Aucun employé trouvé"
Statut : ☑ Succès

---

## Test 4 — Recherche

Action : Taper "admin" dans la barre de recherche
Résultat attendu : Seul Admin Sys affiché
Résultat obtenu : Résultat filtré correctement
Statut : ☑ Succès

---

## Test 5 — Filtre par rôle

Action : Sélectionner "Administrateur" dans le select
Résultat attendu : Seuls les employés avec ce rôle affichés
Résultat obtenu : Filtrage correct
Statut : ☑ Succès

---

## Test 6 — Création de compte

Action : Clic "+ Nouveau compte" → remplir formulaire
```
Nom: RAZANAMAIKA / Prénom: Julia
Email: julia@gmail.com / Contact: 0340111112
Mdp: julia123 / Rôle: Administrateur
```
Résultat attendu : Compte créé, mdp hashé en BCrypt, redirection vers liste
Résultat obtenu : Compte créé, vérification base :
```sql
SELECT LEFT(mdp,7) FROM employe WHERE email='julia@gmail.com';
→ $2a$10$
```
Statut : ☑ Succès

---

## Test 7 — Modification de compte

Action : Clic "Modifier" sur Julia → formulaire pré-rempli → changer le rôle
Résultat attendu : Données pré-remplies, modification enregistrée
Résultat obtenu : Formulaire chargé avec données existantes, modification OK
Statut : 
☑ Succès

---

## Test 8 — Désactivation

Action : Clic "Désactiver" sur Julia
Résultat attendu : Badge passe à "Désactivé", bouton devient "Réactiver"
Résultat obtenu : Comportement correct, KPI "Comptes désactivés" passe à 1
Statut : ☑ Succès

---

## Test 9 — Réactivation

Action : Tab Désactivés → Clic "Réactiver" sur Julia
Résultat attendu : Julia disparaît du tab Désactivés, réapparaît dans Actifs
Résultat obtenu : Comportement correct
Statut : 
☑ Succès

---

## Test 10 — Réinitialisation MDP (même mot de passe)

Action : Réinit. MDP sur Julia → taper "julia123" (même mdp)
Résultat attendu : Erreur sous le champ "Le nouveau mot de passe doit être différent de l'ancien"
Résultat obtenu : Message d'erreur affiché directement sous le champ dans le modal
Statut : 
☑ Succès

---

## Test 11 — Réinitialisation MDP (nouveau mot de passe)

Action : Réinit. MDP sur Julia → taper "nouveaumdp123"
Résultat attendu : Modal se ferme, mdp mis à jour en BCrypt
Résultat obtenu : OK, vérification base → mdp commence par $2a$10$
Statut : 
☑ Succès

---

## Test 12 — Validation champs obligatoires

Action : Soumettre formulaire création avec champs vides
Résultat attendu : Message d'erreur, pas d'appel API
Résultat obtenu : "Veuillez remplir tous les champs obligatoires"
Statut : ☑ Succès

---

## Test 13 — Email déjà utilisé

Action : Créer un compte avec email déjà existant
Résultat attendu : Erreur "L'email est déjà utilisé"
Résultat obtenu : Message d'erreur affiché
Statut : ☑ Succès

---

## Test 14 — Dashboard admin avec données réelles

URL : `/admin`
Résultat attendu : Tableau avec vrais employés, colonne "Dernière connexion" relative
Résultat obtenu : Employés réels affichés, "Il y a X min/h/jours" ou "Jamais connecté"
Statut : ☑ Succès

---

## Bugs identifiés & résolus

| Bug | Cause | Résolution |
|-----|-------|------------|
| `BeanDefinitionOverrideException` sur `/admin` | `AdminViewController` déclarait aussi `GET /admin` déjà géré par `DashboardController` | Suppression de la méthode `dashboardAdmin()` dans `AdminViewController` |
| `operator does not exist: smallint = boolean` | Colonne `actif` en `SMALLINT`, Hibernate envoie `boolean` | Migration V25 : `ALTER COLUMN actif TYPE BOOLEAN USING CASE WHEN actif = 1 THEN TRUE ELSE FALSE END` (précédée de `DROP DEFAULT`) |
| MDP créé/réinitialisé en clair | `setMdp()` sans hachage | Ajout de `PasswordHasher.hash()` dans `creer()` et `reinitialiserMdp()` |
| Erreur MDP identique non visible | Message affiché en haut de page loin du modal | Déplacé dans `#erreurMdp` directement sous le champ dans le modal |
