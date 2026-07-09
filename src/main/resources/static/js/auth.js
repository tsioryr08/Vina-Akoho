(function () {
  "use strict";

  const ROLE_KEY = "vina_akoho_role";

  const ROLE_ALIASES = {
    stock: "gestionnaireStock",
    commercial: "responsableCommercial",
    finance: "comptable",
    user: "livreur"
  };

  const ROLE_CONFIG = {
    admin: { label: "RO01 - Administrateur", dashboardFile: "pages/admin-dashboard.html" },
    responsableAchat: { label: "RO06 - Responsable Achats", dashboardFile: "pages/responsable-achat-dashboard.html" },
    responsableProduction: { label: "RO02 - Responsable Production", dashboardFile: "pages/responsable-production-dashboard.html" },
    gestionnaireStock: { label: "RO03 - Gestionnaire de Stock", dashboardFile: "pages/gestionnaire-stock-dashboard.html" },
    responsableCommercial: { label: "RO04 - Responsable Commercial", dashboardFile: "pages/responsable-commercial-dashboard.html" },
    comptable: { label: "RO05 - Comptable", dashboardFile: "pages/comptable-dashboard.html" },
    livreur: { label: "RO07 - Livreur", dashboardFile: "pages/livreur-dashboard.html" }
  };

  const MODULE_ACCESS = {
    "pages/admin-dashboard.html": ["admin"],
    "pages/admin-clients-nouveau.html": ["admin", "responsableCommercial"],
    "pages/admin-clients-liste.html": ["admin", "responsableCommercial"],
    "pages/admin-article-nouveau.html": ["admin", "gestionnaireStock"],
    "pages/admin-catalogue-stock.html": ["admin", "gestionnaireStock"],
    "pages/gestionnaire-stock-create-produit.html": ["admin", "gestionnaireStock", "responsableAchat"],
    "pages/creation-produit.html": ["admin", "gestionnaireStock", "responsableAchat"],
    "pages/gestionnaire-stock-ajout-stock.html": ["admin", "gestionnaireStock", "responsableAchat"],
    "pages/gestionnaire-stock-suivi-stock.html": ["admin", "gestionnaireStock", "responsableAchat"],
    "pages/responsable-commercial-dashboard.html": ["admin", "responsableCommercial"],
    "pages/responsable-commercial-ventes.html": ["admin", "responsableCommercial"],
    "pages/responsable-commercial-ventes-nouvelles.html": ["admin", "responsableCommercial"],
    "pages/responsable-commercial-ventes-detail.html": ["admin", "responsableCommercial"],
    "pages/responsable-commercial-ventes-historique.html": ["admin", "responsableCommercial"],
    "pages/responsable-commercial-livraison-liste.html": ["admin", "responsableCommercial"],
    "pages/responsable-commercial-livraison-detail.html": ["admin", "responsableCommercial"],
    "pages/responsable-achat-dashboard.html": ["responsableAchat"],
    "pages/responsable-production-dashboard.html": ["admin", "responsableProduction"],
    "pages/responsable-production-previsions.html": ["admin", "responsableProduction", "responsableAchat", "responsableCommercial"],
    "pages/responsable-production-plan-achat.html": ["admin", "responsableProduction", "responsableAchat"],
    "pages/responsable-production-simulation.html": ["admin", "responsableProduction"],
    "pages/gestionnaire-stock-dashboard.html": ["admin", "gestionnaireStock"],
    "pages/comptable-dashboard.html": ["comptable"],
    "pages/comptable-finances.html": ["admin", "comptable"],
    "pages/comptable-depenses.html": ["admin", "comptable"],
    "pages/comptable-cout-de-revient.html": ["admin", "comptable"],
    "pages/comptable-cout-de-revient-synthese.html": ["admin", "comptable"],
    "pages/comptable-rapports-financiers.html": ["admin", "comptable"],
    "pages/livreur-dashboard.html": ["livreur"],
    "pages/livraisons.html": ["admin", "responsableCommercial", "livreur"],
    "pages/livraison-detail.html": ["admin", "responsableCommercial", "livreur"],
    "pages/livraison-nouvelle.html": ["admin", "responsableCommercial", "livreur"],
    "pages/livraison-zones.html": ["admin", "responsableCommercial", "livreur"],
    "pages/gestionnaire-stock-suggestions.html": ["admin", "gestionnaireStock", "responsableCommercial"],
    "pages/produits.html": ["admin", "responsableProduction", "gestionnaireStock"],
    "pages/matieres.html": ["admin", "responsableAchat", "responsableProduction", "gestionnaireStock"],
    "pages/stocks.html": ["admin", "responsableAchat", "responsableProduction", "gestionnaireStock"],
    "pages/ventes.html": ["admin", "responsableCommercial"],
    "pages/clients.html": ["admin", "responsableCommercial", "livreur"],
    "pages/livraisons.html": ["admin", "responsableCommercial", "livreur"],
    "pages/finances.html": ["admin", "responsableAchat", "comptable"],
    "pages/previsions.html": ["admin", "responsableAchat", "responsableProduction", "responsableCommercial"],
    "pages/statistiques.html": ["admin", "responsableCommercial", "comptable"]
  };

  function getPathSegments() {
    return window.location.pathname.replace(/\/+$/, "").split("/").filter(Boolean);
  }

  function getFileDirSegments() {
    const segments = getPathSegments();
    const projectIndex = segments.indexOf("VINA_AKOHO_DESIGN");

    if (projectIndex >= 0) {
      return segments.slice(projectIndex + 1, -1);
    }

    return segments.slice(0, -1);
  }

  function getCurrentModuleKey() {
    const segments = getPathSegments();
    const projectIndex = segments.indexOf("VINA_AKOHO_DESIGN");
    const moduleSegments = projectIndex >= 0 ? segments.slice(projectIndex + 1) : segments;

    return moduleSegments.join("/");
  }

  function getProjectRootUrl() {
    const scripts = document.getElementsByTagName("script");
    const authScript = Array.from(scripts).find(function (script) {
      return script.src &&
        (script.src.indexOf("/assets/js/auth.js") !== -1 || script.src.indexOf("/static/js/auth.js") !== -1);
    });

    if (!authScript) {
      return new URL("/", window.location.href).href;
    }

    return authScript.src
      .replace("/assets/js/auth.js", "/")
      .replace("/static/js/auth.js", "/");
  }

  function projectRelativeUrl(relativePath) {
    return new URL(relativePath, getProjectRootUrl()).href;
  }

  function normalizeRole(role) {
    return ROLE_ALIASES[role] || role;
  }

  function getRole() {
    const storedRole = localStorage.getItem(ROLE_KEY);
    const normalizedRole = normalizeRole(storedRole);

    if (storedRole && normalizedRole !== storedRole) {
      localStorage.setItem(ROLE_KEY, normalizedRole);
    }

    return normalizedRole;
  }

  function redirectIfAuthenticated() {
    const role = getRole();

    if (role && ROLE_CONFIG[role]) {
      redirectToDashboard(role);
    }
  }

  function redirectToDashboard(role) {
    const config = ROLE_CONFIG[role];

    if (!config) {
      window.location.href = projectRelativeUrl("index.html");
      return;
    }

    window.location.href = projectRelativeUrl(config.dashboardFile);
  }

  function login(role, password) {
    if (!ROLE_CONFIG[role]) {
      return { ok: false, message: "Rôle non reconnu." };
    }

    if (password !== "123") {
      return { ok: false, message: "Mot de passe incorrect." };
    }

    localStorage.setItem(ROLE_KEY, role);
    redirectToDashboard(role);

    return { ok: true };
  }

  function logout() {
    localStorage.removeItem(ROLE_KEY);

    fetch(projectRelativeUrl("api/logout"), {
      method: "POST",
      credentials: "same-origin"
    }).catch(function () {
      return null;
    }).finally(function () {
      window.location.href = projectRelativeUrl("index.html");
    });
  }

  function requireRole() {
    const role = getRole();

    if (!role || !ROLE_CONFIG[role]) {
      window.location.href = projectRelativeUrl("index.html");
      return null;
    }

    return role;
  }

  function enforceModuleAccess(role) {
    return true;
  }

  window.VinaAkohoAuth = {
    ROLE_CONFIG,
    MODULE_ACCESS,
    getRole,
    login,
    logout,
    redirectIfAuthenticated,
    requireRole,
    enforceModuleAccess,
    projectRelativeUrl
  };
})();
