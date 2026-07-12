# 🎨 DESIGN SYSTEM - VINA AKOHO

## ✅ Principes Clés
- **Aucun style inline** dans les pages HTML - tout dans `style.css`
- **Classes réutilisables** pour chaque pattern
- **Variables CSS** centralisées pour cohérence globale
- **Pages faciles à lire** - structure HTML claire

---

## 📐 STRUCTURE DE PAGE TYPIQUE

```html
<!doctype html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Ma Page - VINA AKOHO</title>
    <link rel="stylesheet" th:href="@{/css/style.css}" />
</head>
<body>
    <!-- SIDEBAR (injecté via template fragment) -->
    <div th:replace="${sidebarTemplate} :: sidebar"></div>
    <div class="sidebar-backdrop" id="vina-sidebar-backdrop"></div>

    <!-- MAIN LAYOUT -->
    <div class="main">
        <!-- HEADER (injecté via template fragment) -->
        <div th:replace="${headerTemplate} :: header"></div>

        <!-- CONTENU PRINCIPAL -->
        <div class="main-content">
            <!-- Contenu ici -->
        </div>
    </div>
</body>
</html>
```

---

## 🎯 COMPOSANTS ESSENTIELS

### 1️⃣ PAGE HEADER
Utilisez pour le titre principal de chaque page.

```html
<section class="page-header">
    <div>
        <p class="eyebrow">CODE REF - Description</p>
        <h1>Titre Principal</h1>
        <p>Description ou sous-titre de la page</p>
    </div>
    <div class="page-header-actions">
        <button class="btn btn-primary">+ Action</button>
        <button class="btn btn-outline">Filtre</button>
    </div>
</section>
```

---

### 2️⃣ CARTES (Cards)
Conteneur de contenu groupé avec bordure et ombre.

```html
<div class="card">
    <div class="card-header">
        <div>
            <h2 class="card-title">Titre de la Carte</h2>
            <p class="card-subtitle">Description courte</p>
        </div>
    </div>
    <div class="card-body">
        <!-- Contenu ici -->
    </div>
</div>
```

---

### 3️⃣ STATISTIQUES (KPIs)
Grille de statistiques avec valeurs en gros.

```html
<section class="stats-grid">
    <article class="stat-card">
        <div class="stat-label">Comptes Actifs</div>
        <div class="stat-value">248</div>
        <div class="stat-trend trend-success">+12% ce mois</div>
    </article>
    <article class="stat-card">
        <div class="stat-label">Revenus</div>
        <div class="stat-value">$12,450</div>
        <div class="stat-trend trend-warning">-5% vs mois dernier</div>
    </article>
    <!-- Ajouter plus de stat-card selon besoin -->
</section>
```

---

### 4️⃣ FORMULAIRES
Groupes de champs avec labels et validations.

```html
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Formulaire</h2>
    </div>
    <div class="card-body">
        <form class="form-grid">
            <div class="form-group">
                <label class="form-label">Nom *</label>
                <input type="text" class="form-control" placeholder="Entrez un nom" />
                <div class="form-help">Champ obligatoire</div>
            </div>

            <div class="form-group">
                <label class="form-label">Email *</label>
                <input type="email" class="form-control" placeholder="email@example.com" />
            </div>

            <div class="form-group">
                <label class="form-label">Message</label>
                <textarea class="form-control" rows="4" placeholder="Votre message..."></textarea>
            </div>

            <!-- ERREUR -->
            <div class="form-group">
                <label class="form-label">Mot de passe</label>
                <input type="password" class="form-control" />
                <div class="form-error">Le mot de passe doit faire au moins 8 caractères</div>
            </div>

            <!-- Actions -->
            <div class="form-actions">
                <button class="btn btn-outline">Annuler</button>
                <button class="btn btn-primary">Enregistrer</button>
            </div>
        </form>
    </div>
</div>
```

---

### 5️⃣ TABLEAUX
Tableau de données avec entêtes stylisés.

```html
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Liste des Employés</h2>
    </div>
    <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Nom & Prénom</th>
                    <th>Email</th>
                    <th>Rôle</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="emp : ${employes}">
                    <td th:text="${emp.nom} + ' ' + ${emp.prenom}"></td>
                    <td th:text="${emp.email}"></td>
                    <td><span class="badge badge-primary" th:text="${emp.role.poste}"></span></td>
                    <td>
                        <span th:if="${emp.actif}" class="badge badge-success">Actif</span>
                        <span th:unless="${emp.actif}" class="badge badge-danger">Inactif</span>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-ghost">Éditer</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
```

---

### 6️⃣ BOUTONS
Différentes variantes de boutons.

```html
<!-- Primaire (principal) -->
<button class="btn btn-primary">Enregistrer</button>

<!-- Secondaire (complément) -->
<button class="btn btn-secondary">Confirmer</button>

<!-- Outline (alternatives) -->
<button class="btn btn-outline">Annuler</button>

<!-- Ghost (actions secondaires) -->
<button class="btn btn-ghost">Voir plus</button>

<!-- Danger (actions critiques) -->
<button class="btn btn-danger">Supprimer</button>

<!-- Tailles -->
<button class="btn btn-primary btn-sm">Petit</button>
<button class="btn btn-primary">Normal</button>
<button class="btn btn-primary btn-lg">Grand</button>

<!-- Pleine largeur -->
<button class="btn btn-primary btn-block">Action complète</button>
```

---

### 7️⃣ RECHERCHE & FILTRES
Barre de recherche avec filtres.

```html
<div class="search-bar">
    <input type="text" class="form-control search-input" placeholder="Rechercher..." />
    <select class="form-control filter-select">
        <option value="">Tous les rôles</option>
        <option value="admin">Administrateur</option>
        <option value="user">Utilisateur</option>
    </select>
    <button class="btn btn-primary">Rechercher</button>
</div>
```

---

### 8️⃣ ONGLETS
Navigation par onglets.

```html
<div class="tabs">
    <button class="tab-button active" onclick="afficherOnglet('actifs')">Actifs</button>
    <button class="tab-button" onclick="afficherOnglet('inactifs')">Inactifs</button>
    <button class="tab-button" onclick="afficherOnglet('archive')">Archivés</button>
</div>

<div id="actifs" class="tab-content active">
    <!-- Contenu actifs -->
</div>

<div id="inactifs" class="tab-content">
    <!-- Contenu inactifs -->
</div>

<div id="archive" class="tab-content">
    <!-- Contenu archivés -->
</div>

<script>
function afficherOnglet(nom) {
    // Cacher tous les contenus
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.tab-button').forEach(el => el.classList.remove('active'));
    
    // Montrer l'onglet sélectionné
    document.getElementById(nom).classList.add('active');
    event.target.classList.add('active');
}
</script>
```

---

### 9️⃣ ALERTES
Messages informatifs, avertissements, erreurs.

```html
<!-- Information -->
<div class="alert alert-info">
    <div class="alert-title">Information</div>
    <div class="alert-message">Ceci est un message informatif.</div>
</div>

<!-- Succès -->
<div class="alert alert-success">
    <div class="alert-title">Succès</div>
    <div class="alert-message">L'action a été complétée avec succès.</div>
</div>

<!-- Avertissement -->
<div class="alert alert-warning">
    <div class="alert-title">Avertissement</div>
    <div class="alert-message">Attention, vérifiez vos données.</div>
</div>

<!-- Erreur -->
<div class="alert alert-danger">
    <div class="alert-title">Erreur</div>
    <div class="alert-message">Une erreur s'est produite.</div>
</div>

<!-- Avec icône (optionnel) -->
<div class="alert alert-success">
    <div class="alert-icon">✓</div>
    <div class="alert-content">
        <div class="alert-title">Succès</div>
        <div class="alert-message">Opération réussie!</div>
    </div>
</div>
```

---

### 🔟 MODALES (Popups)
Fenêtre modale pour formulaires ou confirmations.

```html
<!-- MODAL OVERLAY -->
<div id="myModal" class="modal-overlay">
    <div class="modal">
        <!-- Header -->
        <div class="modal-header">
            <h2 class="modal-title">Titre de la Modal</h2>
            <button class="modal-close" onclick="closeModal()">×</button>
        </div>

        <!-- Body -->
        <div class="modal-body">
            <div class="form-group">
                <label class="form-label">Champ 1</label>
                <input type="text" class="form-control" />
            </div>
            <div class="form-group">
                <label class="form-label">Champ 2</label>
                <input type="text" class="form-control" />
            </div>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
            <button class="btn btn-ghost" onclick="closeModal()">Annuler</button>
            <button class="btn btn-primary">Confirmer</button>
        </div>
    </div>
</div>

<!-- SCRIPT -->
<script>
function openModal() {
    document.getElementById('myModal').classList.add('active');
}

function closeModal() {
    document.getElementById('myModal').classList.remove('active');
}
</script>
```

---

### 1️⃣1️⃣ BADGE & TAGS
Petits labels pour statuts ou catégories.

```html
<span class="badge badge-primary">Actif</span>
<span class="badge badge-secondary">En attente</span>
<span class="badge badge-danger">Critique</span>
<span class="badge badge-success">Approuvé</span>
```

---

## 🎬 UTILITAIRES FLEXBOX

Remplacez tous les `style="display:flex..."` par ces classes :

```html
<!-- FLEXBOX -->
<div class="flex"><!-- Flex horizontal --></div>
<div class="flex-col"><!-- Flex vertical --></div>
<div class="flex-center"><!-- Centré --></div>
<div class="flex-between"><!-- Space-between --></div>

<!-- GRID -->
<div class="grid-2"><!-- 2 colonnes --></div>
<div class="grid-3"><!-- 3 colonnes --></div>
<div class="grid-4"><!-- 4 colonnes --></div>
<div class="grid-auto"><!-- Auto-fit responsive --></div>

<!-- GAP (espacement) -->
<div class="flex gap-1"><!-- petit espacement --></div>
<div class="flex gap-2"><!-- moyen --></div>
<div class="flex gap-3"><!-- large --></div>

<!-- ESPACEMENT -->
<div class="mt-1"><!-- Margin-top petit --></div>
<div class="mb-3"><!-- Margin-bottom large --></div>
<div class="p-2"><!-- Padding moyen --></div>
```

---

## 🎨 VARIABLES CSS DISPONIBLES

Toutes les variables sont dans `:root` du `style.css`.

```css
/* Couleurs principales */
--primary: #5c3d1e;           /* Marron */
--secondary: #1a472a;         /* Vert foncé */
--accent: #ef233c;            /* Rouge */

/* Espaces */
--space-sm: 8px;
--space-md: 12px;
--space-lg: 16px;
--space-xl: 20px;
--space-2xl: 24px;

/* Rayon */
--radius: 16px;
--radius-lg: 24px;

/* Ombres */
--shadow-sm: 0 2px 8px ...
--shadow-md: 0 8px 16px ...
--shadow: 0 18px 45px ...

/* Transitions */
--transition: 200ms cubic-bezier(0.4, 0, 0.2, 1);
```

---

## 📝 EXEMPLE COMPLET - PAGE DE GESTION

```html
<!doctype html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Gestion des Employés</title>
    <link rel="stylesheet" th:href="@{/css/style.css}" />
</head>
<body>
    <!-- SIDEBAR -->
    <div th:replace="${sidebarTemplate} :: sidebar"></div>
    <div class="sidebar-backdrop"></div>

    <!-- MAIN LAYOUT -->
    <div class="main">
        <!-- HEADER -->
        <div th:replace="${headerTemplate} :: header"></div>

        <!-- CONTENU -->
        <div class="main-content">
            <!-- PAGE HEADER -->
            <section class="page-header">
                <div>
                    <p class="eyebrow">A2 - Administrateur</p>
                    <h1>Gestion des Employés</h1>
                    <p>Gérer les comptes et les permissions utilisateur</p>
                </div>
                <a class="btn btn-primary" th:href="@{/api/admin/employes/nouveau}">+ Nouveau Compte</a>
            </section>

            <!-- STATISTIQUES -->
            <section class="stats-grid">
                <article class="stat-card">
                    <div class="stat-label">Comptes Actifs</div>
                    <div class="stat-value" th:text="${totalActifs}">0</div>
                    <div class="stat-trend trend-success">Utilisateurs actifs</div>
                </article>
                <article class="stat-card">
                    <div class="stat-label">Comptes Inactifs</div>
                    <div class="stat-value" th:text="${totalInactifs}">0</div>
                    <div class="stat-trend trend-warning">Accès suspendus</div>
                </article>
            </section>

            <!-- RECHERCHE & FILTRES -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Filtres</h2>
                </div>
                <div class="card-body">
                    <div class="search-bar">
                        <input type="text" class="form-control search-input" placeholder="Rechercher..." />
                        <select class="form-control filter-select">
                            <option>Tous les rôles</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- TABLEAU -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Liste des Employés</h2>
                </div>
                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Nom</th>
                                <th>Email</th>
                                <th>Rôle</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr th:each="emp : ${employes}">
                                <td th:text="${emp.nom} + ' ' + ${emp.prenom}"></td>
                                <td th:text="${emp.email}"></td>
                                <td th:text="${emp.role.poste}"></td>
                                <td>
                                    <span class="badge" th:class="${emp.actif ? 'badge badge-success' : 'badge badge-danger'}"
                                          th:text="${emp.actif ? 'Actif' : 'Inactif'}"></span>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-outline">Éditer</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
```

---

## ✨ RÉSUMÉ DES RÈGLES

✅ **À FAIRE:**
- Utiliser les classes CSS du design system
- Mettre l'HTML dans des fichiers HTML (propre et lisible)
- Utiliser les variables CSS pour les couleurs
- Grouper le contenu en cartes et sections
- Utiliser des classes utilitaires (flex, grid, gap, etc.)

❌ **À NE PAS FAIRE:**
- Ajouter du `style="..."` inline
- Créer de nouveaux fichiers CSS
- Mélanger HTML et CSS
- Dupliquer des styles
- Utiliser des couleurs hard-codées

---

## 🚀 PROCHAINES ÉTAPES

1. Refactorisez toutes les pages avec ce design system
2. Remplacez tous les styles inline par des classes
3. Testez la responsivité (mobile, tablette, desktop)
4. Améliorez les pages au fur et à mesure

**Bonne chance! 🎨**
