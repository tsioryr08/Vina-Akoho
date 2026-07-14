(function () {
  "use strict";

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
  });
})();
