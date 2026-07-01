(function () {
  "use strict";

  function getDefaultRoleForCurrentPage() {
    const path = window.location.pathname;

    if (path.indexOf("/ventes") === 0 || path.indexOf("/clients") === 0 || path.indexOf("/livraisons") === 0 || path.indexOf("/layout") === 0) {
      return "responsableCommercial";
    }

    return null;
  }

  const role = window.VinaAkohoAuth.getRole() || getDefaultRoleForCurrentPage();

  if (!role) {
    window.location.href = window.VinaAkohoAuth.projectRelativeUrl("index.html");
    return;
  }

  window.VinaAkohoAuth.enforceModuleAccess(role);

  const PAGE_LABELS = {
    "admin-dashboard.html": "Dashboard",
    "admin-clients-nouveau.html": "Clients - Enregistrement",
    "admin-clients-liste.html": "Clients - Base de données",
    "admin-clients-detail.html": "Détail Client",
    "admin-clients-edit.html": "Éditer Client",
    "admin-article-nouveau.html": "Article - Catalogue",
    "admin-catalogue-stock.html": "Catalogue Stocks",
    "responsable-achat-dashboard.html": "Dashboard Achats",
    "responsable-achat-previsions.html": "Prévisions de Stocks",
    "responsable-achat-plan-achat.html": "Plan d'achat",
    "responsable-achat-bon-commande.html": "Bon de Commande",
    "responsable-achat-suivi-livraisons.html": "Suivi de livraison",
    "responsable-achat-verification-livraison.html": "Vérification Livraison",
    "responsable-achat-enregistrement-stock.html": "Enregistrement Stock",
    "responsable-achat-suggestions.html": "Suggestions Réapprovisionnement",
    "responsable-production-dashboard.html": "Dashboard Production",
    "responsable-production-previsions.html": "Prévisions",
    "responsable-production-plan-achat.html": "Plan d’Achat",
    "responsable-production-simulation.html": "Simulation Rentabilité",
    "responsable-production-stocks-finis.html": "Suivi Produits Finis",
    "responsable-production-matieres-ajout.html": "Ajouter Matieres",
    "responsable-production-matieres-suivi.html": "Suivi Stock Matieres",
    "responsable-production-matieres-creation.html": "Creer Fiche Produit",
    "responsable-production-matieres-previsions.html": "Previsions & Suggestions",
    "responsable-production-produits-finis.html": "Suivi Produits Finis",
    "gestionnaire-stock-dashboard.html": "Dashboard Stock",
    "gestionnaire-stock-mouvements.html": "Mouvements de Stock",
    "gestionnaire-stock-alertes.html": "Alertes de seuil",
    "gestionnaire-stock-lots.html": "Lots & Péremptions",
    "gestionnaire-stock-inventaire.html": "Inventaire périodique",
    "gestionnaire-stock-rapport.html": "Rapport de Stock",
    "gestionnaire-stock-create-produit.html": "Création Fiche Produit",
    "creation-produit.html": "Création Produit",
    "create.html": "Creer Produit",
    "gestionnaire-stock-ajout-stock.html": "Entrée en Stock",
    "gestionnaire-stock-suggestions.html": "Suggestions de promotions",
    "gestionnaire-stock-suivi-stock.html": "Suivi Stocks PAMP",
    "responsable-commercial-dashboard.html": "Dashboard Commercial",
    "responsable-commercial-ventes.html": "Liste des Ventes",
    "responsable-commercial-ventes-nouvelles.html": "Nouvelle Vente",
    "responsable-commercial-ventes-detail.html": "Détail Vente",
    "responsable-commercial-ventes-historique.html": "Historique Ventes",
    "comptable-dashboard.html": "Dashboard Comptable",
    "comptable-finances.html": "Vue d’ensemble financière",
    "comptable-depenses.html": "Dépenses",
    "comptable-cout-de-revient.html": "Coût de revient",
    "comptable-cout-de-revient-synthese.html": "Synthèse coût de revient",
    "comptable-rapports-financiers.html": "Rapports financiers",
    "livreur-dashboard.html": "Dashboard Livreur",
    "livraisons.html": "Liste des livraisons",
    "livraison-detail.html": "Détail d’une livraison",
    "livraison-nouvelle.html": "Nouvelle livraison",
    "livraison-zones.html": "Zones à forte demande",
    "clients.html": "F5 Clients",
    "clients-detail.html": "Détail Client",
    "clients-detail-edit.html": "Éditer Client",
    "clients-nouveau.html": "Clients - Enregistrement",
    "clients-liste.html": "Clients - Base de données",
    "article-nouveau.html": "Article - Catalogue",
    "catalogue-stock.html": "Catalogue NutriFarm",
    "create-produit.html": "Création Fiche Produit",
    "ajout-stock.html": "Entrée en Stock",
    "suggestions.html": "Fiche Descriptive FIFO",
    "suivi-stock.html": "Suivi Stocks PAMP",
    "ventes.html": "Liste des Ventes",
    "ventes-nouvelles.html": "Nouvelle Vente",
    "ventes-detail.html": "Détail Vente",
    "ventes-historique.html": "Historique Ventes",
    "previsions.html": "F8 Prévisions",
    "plan-achat.html": "Plan d’Achat",
    "simulation.html": "Simulation Rentabilité",
    "depenses.html": "Dépenses",
    "cout-de-revient.html": "Coût de revient",
    "cout-de-revient-synthese.html": "Synthèse coût de revient",
    "rapports-financiers.html": "Rapports financiers",
    "produits.html": "F1 Produits",
    "matieres.html": "F2 Matières premières",
    "stocks.html": "F3 Stocks",
    "ventes.html": "F4 Ventes",
    "clients.html": "F5 Clients",
    "livraisons.html": "F6 Livraisons",
    "finances.html": "F7 Finances",
    "previsions.html": "F8 Prévisions",
    "statistiques.html": "F10 Statistiques",
    "admin-utilisateurs.html": "Gestion des Utilisateurs",
    "admin-utilisateurs-nouveau.html": "Créer Utilisateur",
    "admin-utilisateurs-detail.html": "Détail Utilisateur",
    "admin-finances-rapports.html": "Rapports Financiers",
    "admin-statistiques.html": "Statistiques Globales",
    "admin-statistiques-production.html": "Statistiques Production",
    "admin-statistiques-ventes.html": "Statistiques Ventes",
    "admin-statistiques-geographic.html": "Statistiques Géographiques",
    "admin-parametre.html": "Paramètres Système",
    "admin-parametres-permissions-edit.html": "Édition Permissions Rôle",
    "admin-previsions.html": "Prévisions & Simulations"
  };

  function loadPartial(path) {
    return fetch(path)
      .then(function (response) {
        if (!response.ok) {
          throw new Error("Impossible de charger " + path);
        }

        return response.text();
      });
  }

  function setActiveSidebarLink() {
    const current = normalizeUrl(window.location.href);

    document.querySelectorAll("#vina-sidebar a").forEach(function (link) {
      const href = link.getAttribute("href");

      if (!href) {
        return;
      }

      const target = normalizeUrl(new URL(href, window.location.href).href);

      if (target === current) {
        link.classList.add("active");
      }
    });
  }

  function normalizeUrl(url) {
    return new URL(url).pathname.replace(/\/+$/, "");
  }

  function updateBreadcrumb() {
    const breadcrumb = document.getElementById("vina-breadcrumb");
    const fileName = window.location.pathname.split("/").pop();
    const label = PAGE_LABELS[fileName] || "VINA AKOHO";

    if (breadcrumb) {
      breadcrumb.textContent = label;
    }
  }

  function updateRolePill() {
    const rolePill = document.getElementById("vina-role-pill");
    const config = window.VinaAkohoAuth.ROLE_CONFIG[role];

    if (rolePill && config) {
      rolePill.textContent = config.label;
    }
  }

  function setupLogout() {
    const button = document.getElementById("vina-logout-button");

    if (!button) {
      return;
    }

    button.addEventListener("click", function () {
    window.VinaAkohoAuth.logout();
  });
  }

  function setupMobileMenu() {
    const button = document.getElementById("vina-menu-button");
    const sidebar = document.getElementById("vina-sidebar");

    if (!button || !sidebar) {
      return;
    }

    let backdrop = document.getElementById("vina-sidebar-backdrop");

    if (!backdrop) {
      backdrop = document.createElement("div");
      backdrop.id = "vina-sidebar-backdrop";
      backdrop.className = "sidebar-backdrop";
      document.body.appendChild(backdrop);
    }

    function closeMenu() {
      sidebar.classList.remove("is-open");
      backdrop.classList.remove("is-open");
    }

    button.addEventListener("click", function () {
      const isOpen = sidebar.classList.toggle("is-open");
      backdrop.classList.toggle("is-open", isOpen);
    });

    backdrop.addEventListener("click", closeMenu);

    window.addEventListener("resize", function () {
      if (window.innerWidth > 900) {
        closeMenu();
      }
    });
  }

  function getProjectRootUrl() {
    const scripts = document.getElementsByTagName("script");
    const appShellScript = Array.from(scripts).find(function (script) {
      return script.src &&
        (script.src.indexOf("/assets/js/app-shell.js") !== -1 || script.src.indexOf("/static/js/app-shell.js") !== -1);
    });

    if (!appShellScript) {
      return window.VinaAkohoAuth.projectRelativeUrl("");
    }

    return appShellScript.src
      .replace("/assets/js/app-shell.js", "/")
      .replace("/static/js/app-shell.js", "/");
  }

  document.addEventListener("DOMContentLoaded", function () {
    const rootUrl = getProjectRootUrl();

    Promise.all([
      loadPartial(rootUrl + "layout/header.html"),
      loadPartial(rootUrl + "layout/" + role + ".html"),
      loadPartial(rootUrl + "layout/footer.html")
    ])
      .then(function (results) {
        document.getElementById("vina-header").innerHTML = results[0];
        document.getElementById("vina-sidebar").innerHTML = results[1];
        document.getElementById("vina-footer").innerHTML = results[2];

        updateRolePill();
        updateBreadcrumb();
        setActiveSidebarLink();
        setupLogout();
        setupMobileMenu();
      })
      .catch(function (error) {
        console.warn(error.message);
      });
  });
})();
