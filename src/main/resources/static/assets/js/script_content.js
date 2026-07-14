(function () {
  "use strict";

  function normalizePath(path) {
    const clean = (path || "/").split("?")[0].split("#")[0];
    const trimmed = clean.replace(/\/+$/, "");
    return trimmed || "/";
  }

  function setActiveSidebarLink() {
    const sidebar = document.getElementById("vina-sidebar");

    if (!sidebar) {
      return;
    }

    const currentPath = normalizePath(window.location.pathname);
    const links = Array.from(sidebar.querySelectorAll("a.sidebar-link[href]"));
    const sectionKeywords = [
      "production",
      "matieres-premieres",
      "produits",
      "ventes",
      "clients",
      "livraison",
      "stock",
      "comptable",
      "admin"
    ];

    function getTargetPath(link) {
      const href = link.getAttribute("href");

      if (!href || href.startsWith("javascript:")) {
        return null;
      }

      return normalizePath(new URL(href, window.location.origin).pathname);
    }

    function getDashboardLink() {
      return links.find(function (link) {
        const label = (link.textContent || "").toLowerCase();
        return label.includes("dashboard");
      }) || links[0] || null;
    }

    links.forEach(function (link) {
      link.classList.remove("active");
      link.removeAttribute("aria-current");
    });

    let bestMatch = null;
    let bestScore = -1;

    links.forEach(function (link) {
      const targetPath = getTargetPath(link);

      if (!targetPath) {
        return;
      }

      const isExactMatch = targetPath === currentPath;
      const isPrefixMatch = targetPath !== "/" && currentPath.startsWith(targetPath + "/");
      const isIncludedMatch = targetPath !== "/" && currentPath.includes(targetPath);

      if (!isExactMatch && !isPrefixMatch && !isIncludedMatch) {
        return;
      }

      const score = (isExactMatch ? 3000 : isPrefixMatch ? 2000 : 1000) + targetPath.length;

      if (score >= bestScore) {
        bestScore = score;
        bestMatch = link;
      }
    });

    if (!bestMatch) {
      const hasKnownKeyword = sectionKeywords.some(function (keyword) {
        return currentPath.includes(keyword);
      });

      if (hasKnownKeyword) {
        bestMatch = getDashboardLink();
      }
    }

    if (bestMatch) {
      bestMatch.classList.add("active");
      bestMatch.setAttribute("aria-current", "page");
    }
  }

  function setupMobileMenu() {
    const button = document.getElementById("vina-menu-button");
    const sidebar = document.getElementById("vina-sidebar");
    const backdrop = document.getElementById("vina-sidebar-backdrop");

    if (!button || !sidebar || !backdrop) {
      return;
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

  function setupLogout() {
    if (window.__vinaLogoutListenerAttached) {
      return;
    }

    window.__vinaLogoutListenerAttached = true;

    document.addEventListener("click", function (event) {
      const button = event.target.closest("#vina-logout-button");

      if (!button) {
        return;
      }

      fetch('/api/logout', {
        method: 'POST',
        credentials: 'same-origin'
      }).catch(function () {
        return null;
      }).finally(function () {
        window.location.href = '/';
      });
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    setupMobileMenu();
    setupLogout();
    setActiveSidebarLink();
  });
})();
