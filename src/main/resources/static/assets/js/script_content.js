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
    const button = document.getElementById("vina-logout-button");
    if (!button) return;

    button.addEventListener("click", function () {
      window.location.href = '/';
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    setupMobileMenu();
    setupLogout();
  });
})();
