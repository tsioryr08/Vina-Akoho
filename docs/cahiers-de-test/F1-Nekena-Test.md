# CAHIER DE TESTS - GESTION DES PRODUITS ET CATEGORIES

## 1. TESTS DES CATEGORIES

### 1.1 TEST DE CREATION D'UNE CATEGORIE

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Creation d'une categorie avec tous les champs valides | Acceder a `/categories/nouveau`, remplir tous les champs, soumettre | Redirection vers `/categories`, message de succes | Conforme | OK |
| Creation avec libelle existant | Creer une categorie puis une autre avec le meme libelle | Message d'erreur "existe deja" | Conforme | OK |
| Creation avec libelle vide | Laisser le champ libelle vide et soumettre | Message d'erreur de validation | Conforme | OK |

### 1.2 TEST DE LECTURE DES CATEGORIES

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Affichage de la liste des categories | Acceder a `/categories` | Liste affichee avec pagination | Conforme | OK |
| Affichage du detail d'une categorie | Acceder a `/categories/1` | Toutes les informations affichees | Conforme | OK |
| Acces a une categorie inexistante | Acceder a `/categories/9999` | Message "Non trouvee" | Conforme | OK |
| Pagination de la liste | Creer >20 categories et cliquer sur page 2 | Pagination fonctionnelle | Conforme | OK |

### 1.3 TEST DE MODIFICATION DES CATEGORIES

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Modification d'une categorie existante | Acceder a `/categories/1/modifier`, modifier, soumettre | Redirection, message de succes | Conforme | OK |
| Modification avec libelle deja utilise | Mettre un libelle existant | Message d'erreur | Conforme | OK |
| Modification d'une categorie inactive | Desactiver puis modifier | Modification possible | Conforme | OK |
| Annulation de la modification | Cliquer sur "Annuler" | Redirection sans modification | Conforme | OK |

### 1.4 TEST DE SUPPRESSION (SOFT DELETE) DES CATEGORIES

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Desactivation d'une categorie sans produits | Creer une categorie sans produits, cliquer sur "Desactiver" | Message de succes, categorie inactive | Conforme | OK |
| Desactivation avec produits actifs | Associer un produit, cliquer sur "Desactiver" | Message d'erreur | Conforme | OK |
| Reactivation d'une categorie | Desactiver puis cliquer sur "Reactivation" | Message de succes, categorie active | Conforme | OK |
| Reactivation avec libelle en conflit | 2 categories inactives meme libelle, reactiver les 2 | Erreur pour la seconde | Conforme | OK |

### 1.5 TEST DE RECHERCHE DES CATEGORIES

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Recherche par libelle exact | Saisir un libelle exact | Resultats correspondants | Conforme | OK |
| Recherche par libelle partiel | Saisir une partie du libelle | Resultats contenant le terme | Conforme | OK |
| Recherche sans resultat | Saisir "xxxxxxxxx" | Message "Aucun resultat" | Conforme | OK |
| Recherche avec reinitialisation | Saisir un terme puis cliquer sur "Reinitialiser" | Retour a la liste complete | Conforme | OK |

---

## 2. TESTS DES PRODUITS

### 2.1 TEST DE CREATION D'UN PRODUIT

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Creation avec tous les champs valides | Acceder a `/produits/nouveau`, remplir, soumettre | Redirection, message de succes | Conforme | OK |
| Creation avec reference existante | Creer "REF-001" puis un autre avec "REF-001" | Message d'erreur "existe deja" | Conforme | OK |
| Creation avec nom existant | Creer "Produit Test" puis un autre avec meme nom | Message d'erreur "existe deja" | Conforme | OK |
| Creation sans categorie | Laisser idCategorie vide | Message d'erreur de validation | Conforme | OK |
| Creation avec prix negatif | Prix = -1000 | Message d'erreur de validation | Conforme | OK |
| Creation avec marge heritee | Produit avec categorie marge=25% | Marge affichee = 25% | Conforme | OK |

### 2.2 TEST DE LECTURE DES PRODUITS

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Affichage de la liste des produits | Acceder a `/produits` | Liste affichee avec pagination | Conforme | OK |
| Affichage du detail d'un produit | Acceder a `/produits/1` | Informations et nutritionnelles affichees | Conforme | OK |
| Acces a un produit inexistant | Acceder a `/produits/9999` | Message "Non trouve" | Conforme | OK |
| Verification du statut actif | Creer un produit actif | Badge "Actif" affiche | Conforme | OK |
| Verification du statut inactif | Desactiver un produit | Badge "Inactif" affiche | Conforme | OK |

### 2.3 TEST DE MODIFICATION DES PRODUITS

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Modification d'un produit existant | Acceder a `/produits/1/modifier`, modifier, soumettre | Redirection, message de succes | Conforme | OK |
| Modification avec reference deja utilisee | Mettre une reference existante | Message d'erreur | Conforme | OK |
| Modification avec nom deja utilise | Mettre un nom existant | Message d'erreur | Conforme | OK |
| Modification du statut actif/inactif | Cocher/decocher "Actif" | Statut modifie | Conforme | OK |
| Modification d'un produit inactif | Desactiver puis modifier | Modification possible | Conforme | OK |

### 2.4 TEST DE SUPPRESSION (SOFT DELETE) DES PRODUITS

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Desactivation d'un produit | Creer un produit, cliquer sur "Desactiver" | Message de succes, produit inactif | Conforme | OK |
| Reactivation d'un produit | Desactiver puis cliquer sur "Reactivation" | Message de succes, produit actif | Conforme | OK |
| Reactivation avec reference en conflit | 2 produits inactifs meme reference | Erreur pour le second | Conforme | OK |
| Verification apres desactivation | Desactiver puis rechercher | Produit non trouve | Conforme | OK |

### 2.5 TEST DE RECHERCHE ET FILTRAGE DES PRODUITS

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Recherche par reference | Saisir "REF-001" | Resultat contenant "REF-001" | Conforme | OK |
| Recherche par nom | Saisir "Demarrage" | Resultats contenant "Demarrage" | Conforme | OK |
| Recherche par description | Saisir un mot de description | Resultats contenant le mot | Conforme | OK |
| Filtrage par categorie | Selectionner une categorie | Produits de la categorie uniquement | Conforme | OK |
| Filtrage par prix minimum | Prix min = 40000 | Produits avec prix >= 40000 | Conforme | OK |
| Filtrage par prix maximum | Prix max = 50000 | Produits avec prix <= 50000 | Conforme | OK |
| Filtrage par fourchette de prix | Prix min=40000, max=50000 | Produits entre 40000 et 50000 | Conforme | OK |
| Recherche sans resultat | Saisir "xxxxxxxxx" | Message "Aucun produit trouve" | Conforme | OK |
| Combinaison de filtres | Recherche + Categorie + Prix | Resultats correspondant a tous les criteres | Conforme | OK |
| Reinitialisation des filtres | Appliquer des filtres puis "Reinitialiser" | Retour a la liste complete | Conforme | OK |

---

## 3. TESTS D'INTEGRATION

### 3.1 CATEGORIE <-> PRODUIT

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Creation produit avec categorie valide | Creer categorie puis produit avec cette categorie | Produit cree avec categorie associee | Conforme | OK |
| Desactivation categorie avec produits actifs | Creer categorie avec produit, desactiver | Message d'erreur bloque | Conforme | OK |
| Desactivation categorie sans produits | Creer categorie sans produit, desactiver | Desactivation reussie | Conforme | OK |
| Mise a jour categorie d'un produit | Modifier produit, changer de categorie | Produit mis a jour | Conforme | OK |
| Suppression categorie utilisee | Tentative de suppression | Message d'erreur d'integrite | Conforme | OK |
| Affichage marge heritee | Categorie marge=25%, produit cree | Produit affiche marge=25% | Conforme | OK |

### 3.2 SOFT DELETE

| Description | Etapes | Resultat Attendu | Resultat Obtenu | Statut |
|-------------|--------|------------------|-----------------|--------|
| Suppression d'une categorie avec verification | Desactiver categorie, verifier en base | actif = false | Conforme | OK |
| Suppression d'un produit avec verification | Desactiver produit, verifier en base | actif = false | Conforme | OK |
| Reactivation et verification | Reactiver element, verifier en base | actif = true | Conforme | OK |
| Liste exclut elements inactifs | Desactiver element, afficher liste | Element non visible | Conforme | OK |

---

## 4. TESTS DE VALIDATION

### 4.1 VALIDATION DES FORMULAIRES CATEGORIE

| Champ | Valeur de Test | Resultat Attendu | Statut |
|-------|----------------|------------------|--------|
| Libelle | Vide | Erreur "obligatoire" | OK |
| Libelle | "A" * 101 | Erreur "trop long" | OK |
| Marge | -5 | Erreur ">= 0" | OK |
| Marge | Vide | Erreur "obligatoire" | OK |
| Proteines | 150 | Erreur "<= 100" | OK |
| Proteines | -10 | Erreur ">= 0" | OK |

### 4.2 VALIDATION DES FORMULAIRES PRODUIT

| Champ | Valeur de Test | Resultat Attendu | Statut |
|-------|----------------|------------------|--------|
| Reference | Vide | Erreur "obligatoire" | OK |
| Reference | "A" * 101 | Erreur "trop long" | OK |
| Nom | Vide | Erreur "obligatoire" | OK |
| Categorie | Vide | Erreur "obligatoire" | OK |
| Prix | -100 | Erreur ">= 0" | OK |
| Seuil alerte | -5 | Erreur ">= 0" | OK |

### 5.3 COMMENTAIRES ET OBSERVATIONS

amelioration : pour la recherche , utiliser js pour faciliter 
amin'zao mantsy mila exacte le mot vo mety .

