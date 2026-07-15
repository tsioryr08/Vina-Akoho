(function () {
  'use strict';

  const DEFAULT_OPTIONS = {
    loadingClass: 'is-loading',
    loadingText: 'Chargement…',
    errorMessage: 'Une erreur est survenue lors du chargement des données.',
    emptyMessage: 'Aucune donnée disponible.',
    debounceMs: 300,
  };

  function buildQueryString(form) {
    const params = new URLSearchParams();
    const elements = form.querySelectorAll('input, select, textarea');
    elements.forEach(function (el) {
      if (!el.name || el.disabled || el.type === 'submit' || el.type === 'button' || el.type === 'reset') {
        return;
      }
      if (el.type === 'checkbox' || el.type === 'radio') {
        if (el.checked) {
          params.append(el.name, el.value);
        }
        return;
      }
      if (el.value !== null && el.value !== undefined && el.value.trim() !== '') {
        params.append(el.name, el.value.trim());
      }
    });
    return params.toString();
  }

  function setButtonLoading(button, isLoading, originalText) {
    if (!button) return;
    if (isLoading) {
      button.dataset.originalText = button.textContent;
      button.textContent = DEFAULT_OPTIONS.loadingText;
      button.disabled = true;
    } else {
      button.textContent = button.dataset.originalText || originalText || 'Valider';
      button.disabled = false;
    }
  }

  function showError(container, message) {
    if (!container) return;
    let errorBox = container.querySelector('.ajax-error');
    if (!errorBox) {
      errorBox = document.createElement('div');
      errorBox.className = 'alert-box danger ajax-error';
      container.appendChild(errorBox);
    }
    errorBox.textContent = message;
    errorBox.style.display = 'block';
  }

  function clearError(container) {
    if (!container) return;
    const errorBox = container.querySelector('.ajax-error');
    if (errorBox) {
      errorBox.remove();
    }
  }

  function setupAjaxForm(form, options) {
    const opts = Object.assign({}, DEFAULT_OPTIONS, options || {});
    if (!form) return;

    const submitButton = form.querySelector('button[type="submit"], input[type="submit"]');
    const originalButtonText = submitButton ? submitButton.textContent : null;
    const container = form.closest('section, .card, main, .content') || document.body;

    let debounceTimer = null;

    form.addEventListener('submit', function (event) {
      event.preventDefault();
      clearError(container);

      const queryString = buildQueryString(form);
      const url = form.action + (queryString ? '?' + queryString : '');

      if (opts.debounceMs > 0) {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(function () {
          fetchData(url, form, container, submitButton, originalButtonText, opts);
        }, opts.debounceMs);
      } else {
        fetchData(url, form, container, submitButton, originalButtonText, opts);
      }
    });

    form.addEventListener('reset', function () {
      clearError(container);
      if (opts.onReset) {
        opts.onReset();
      }
    });
  }

  function fetchData(url, form, container, submitButton, originalButtonText, opts) {
    setButtonLoading(submitButton, true, originalButtonText);

    fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
      },
      credentials: 'same-origin',
    })
      .then(function (response) {
        if (!response.ok) {
          return response.text().then(function (text) {
            throw new Error('Erreur serveur (' + response.status + ')');
          });
        }
        return response.json();
      })
      .then(function (data) {
        if (opts.onSuccess) {
          opts.onSuccess(data, container, form);
        }
        clearError(container);
      })
      .catch(function (error) {
        console.error('AjaxFilter error:', error);
        showError(container, opts.errorMessage + (error.message ? ' (' + error.message + ')' : ''));
      })
      .finally(function () {
        setButtonLoading(submitButton, false, originalButtonText);
      });
  }

  window.VinaAjaxFilters = {
    setup: setupAjaxForm,
    buildQueryString: buildQueryString,
  };
})();
