(function () {
  'use strict';

  function formatDecimal(value, fallback) {
    if (value === null || value === undefined || isNaN(value)) {
      return fallback !== undefined ? fallback : '0';
    }
    const n = parseFloat(value);
    if (Number.isInteger(n)) {
      return n.toLocaleString('fr-FR');
    }
    return n.toLocaleString('fr-FR', { minimumFractionDigits: 1, maximumFractionDigits: 2 });
  }

  function setText(id, text) {
    const el = document.getElementById(id);
    if (el) {
      el.textContent = text !== undefined ? text : '';
    }
  }

  function renderProductionDashboard(data) {
    setText('kpi-produits-finis', formatDecimal(data.quantiteProduitsFinis));
    setText('kpi-unite-produits', data.uniteProduits || 'Unité');
    setText('kpi-mp', formatDecimal(data.quantiteMatièresPremières));
    setText('kpi-unite-mp', data.uniteMps || 'Unité');
    setText('kpi-lots-produits', data.lotsProduits);
    setText('kpi-lots-expirants', data.lotsExpirantBientot);
    setText('kpi-produits-seuil', data.produitsSousSeuil);
    setText('kpi-mp-seuil', data.mpSousSeuilMin);

    const subtitleEl = document.getElementById('production-prevision-produits-subtitle');
    if (subtitleEl && data.joursAnalyse && data.joursCouverture && data.dateDebutPrevision && data.dateFinPrevision) {
      const fmt = d => d ? d.slice(0, 10) : '';
      subtitleEl.innerHTML =
        'Résultat sur <strong>' + data.joursAnalyse + ' jours</strong> : moyenne journalière = ventes ÷ ' +
        data.joursAnalyse + ', objectif = moyenne × ' + data.joursCouverture +
        ', à produire = max(objectif − stock, 0). ' +
        'Période analysée du <strong>' + fmt(data.dateDebutPrevision) + '</strong> au <strong>' + fmt(data.dateFinPrevision) + '</strong>.';
    }

    const prevProdBody = document.getElementById('prevision-production-tbody');
    if (prevProdBody && Array.isArray(data.previsionsProduction)) {
      prevProdBody.innerHTML = data.previsionsProduction.map(p => {
        const statutClass = p.statut === 'À PRODUIRE' ? 'badge-warning' :
                            p.statut === 'RECETTE MANQUANTE' ? 'badge-danger' : 'badge-success';
        const actionLink = (p.propositionProduction > 0 && p.recetteDisponible)
          ? '<a class="btn btn-orange btn-small" href="/api/production/entree-produit?produitId=' + p.produitId + '&quantite=' + p.propositionProduction + '">Produire</a>'
          : '<span>—</span>';
        return '<tr>' +
          '<td><strong>' + escapeHtml(p.produitNom) + '</strong><br /><small>' + escapeHtml(p.categorie) + '</small></td>' +
          '<td>' + formatDecimal(p.quantiteVendue) + ' ' + escapeHtml(p.unite) + '</td>' +
          '<td>' + formatDecimal(p.moyenneJournaliere) + ' ' + escapeHtml(p.unite) + '</td>' +
          '<td>' + formatDecimal(p.stockActuel) + ' ' + escapeHtml(p.unite) + '</td>' +
          '<td>' + formatDecimal(p.objectifStock) + ' ' + escapeHtml(p.unite) + '</td>' +
          '<td><strong>' + formatDecimal(p.propositionProduction) + ' ' + escapeHtml(p.unite) + '</strong></td>' +
          '<td><span class="badge ' + statutClass + '">' + escapeHtml(p.statut) + '</span></td>' +
          '<td>' + actionLink + '</td>' +
        '</tr>';
      }).join('') || '<tr><td colspan="8" class="empty-state">Aucun produit actif.</td></tr>';
    }

    const prevMpBody = document.getElementById('prevision-mp-tbody');
    if (prevMpBody && Array.isArray(data.previsionsMp)) {
      prevMpBody.innerHTML = data.previsionsMp.map(mp => {
        const statutClass = mp.statut === 'À COMMANDER' ? 'badge-warning' : 'badge-success';
        return '<tr>' +
          '<td><strong>' + escapeHtml(mp.nom) + '</strong><br /><small>' + escapeHtml(mp.code) + '</small></td>' +
          '<td>' + escapeHtml(mp.fournisseur) + '</td>' +
          '<td>' + formatDecimal(mp.besoinEstime) + ' ' + escapeHtml(mp.unite) + '</td>' +
          '<td>' + formatDecimal(mp.stockActuel) + ' ' + escapeHtml(mp.unite) + '</td>' +
          '<td>' + formatDecimal(mp.stockSecurite) + ' ' + escapeHtml(mp.unite) + '</td>' +
          '<td><strong>' + formatDecimal(mp.quantiteACommander) + ' ' + escapeHtml(mp.unite) + '</strong></td>' +
          '<td><span class="badge ' + statutClass + '">' + escapeHtml(mp.statut) + '</span></td>' +
        '</tr>';
      }).join('') || '<tr><td colspan="7" class="empty-state">Aucun besoin matière : stock produit suffisant ou recette active absente.</td></tr>';
    }
  }

  function renderHistorique(data) {
    const tbody = document.getElementById('historique-tbody');
    if (tbody && Array.isArray(data.productions)) {
      tbody.innerHTML = data.productions.map(p => {
        const highlightStyle = (data.highlightProductionId != null && data.highlightProductionId === p.idFabrication)
          ? 'background: rgba(255, 179, 71, 0.14);' : '';
        const peremption = p.datePeremption
          ? 'Péremption : ' + p.datePeremption.slice(0, 10)
          : 'Sans péremption';
        return '<tr style="' + highlightStyle + '">' +
          '<td><div><strong>FAB-' + p.idFabrication + '</strong></div><small class="text-muted">Lot #' + p.idLotProduit + '</small></td>' +
          '<td><div>' + escapeHtml(p.nomProduit) + '</div><small class="text-muted">' + peremption + '</small></td>' +
          '<td>' + formatDecimal(p.quantiteProduite) + ' unité(s)</td>' +
          '<td>' + formatDecimal(p.quantiteRestanteLot) + ' unité(s)</td>' +
          '<td>' + (p.dateFabrication ? p.dateFabrication.slice(0, 10) + ' ' + p.dateFabrication.slice(11, 16) : '') + '</td>' +
          '<td>' + escapeHtml(p.employeNomComplet) + '</td>' +
          '<td><a class="btn btn-sm btn-ghost" href="/api/production/historique/' + p.idFabrication + '">Voir détails</a></td>' +
        '</tr>';
      }).join('') || '<tr><td colspan="7" class="empty-state">Aucune production ne correspond aux filtres.</td></tr>';
    }

    const paginationEl = document.getElementById('historique-pagination');
    if (paginationEl && data.pagination) {
      const p = data.pagination;
      const params = new URLSearchParams(window.location.search);
      let html = '';
      if (p.currentPage > 0) {
        params.set('page', p.currentPage - 1);
        html += '<a class="btn btn-ghost" href="/api/production/historique/data?' + params.toString() + '">Précédent</a>';
      }
      html += '<span class="text-muted">Page ' + (p.currentPage + 1) + ' / ' + p.totalPages + '</span>';
      if (p.currentPage + 1 < p.totalPages) {
        params.set('page', p.currentPage + 1);
        html += '<a class="btn btn-ghost" href="/api/production/historique/data?' + params.toString() + '">Suivant</a>';
      }
      paginationEl.innerHTML = html;
    }

    const totalEl = document.getElementById('historique-total');
    if (totalEl && data.pagination) {
      totalEl.textContent = data.pagination.totalElements + ' fabrication(s) trouvée(s)';
    }
  }

  function renderMatiereListe(data) {
    const tbody = document.getElementById('matiere-tbody');
    if (tbody && Array.isArray(data.mps)) {
      tbody.innerHTML = data.mps.map(mp => {
        const seuilDisplay = mp.seuilMinimum != null ? mp.seuilMinimum + ' ' + escapeHtml(mp.uniteLibelle) : '—';
        const statutBadge = mp.statut === 'SEUIL ATTEINT'
          ? '<span class="badge badge-danger">SEUIL ATTEINT</span>'
          : '<span class="badge badge-success">Stock Correct</span>';
        return '<tr>' +
          '<td><strong>' + escapeHtml(mp.nom) + '</strong></td>' +
          '<td>' + escapeHtml(mp.fournisseurNom) + '</td>' +
          '<td>' + mp.quantiteStock + ' ' + escapeHtml(mp.uniteLibelle) + '</td>' +
          '<td>' + seuilDisplay + '</td>' +
          '<td>' + mp.pamp + ' Ar</td>' +
          '<td>' + statutBadge + '</td>' +
          '<td><a class="btn btn-small btn-ghost" href="/api/matieres-premieres/' + mp.id + '">Fiche →</a></td>' +
        '</tr>';
      }).join('') || '<tr><td colspan="7" class="empty-state-cell">Aucune matière première enregistrée.<br/><a href="/api/matieres-premieres/nouveau">En créer une →</a></td></tr>';
    }

    setText('matiere-total-articles', data.totalArticles);
    setText('matiere-total-stock', (data.totalStock || 0) + ' kg');
    setText('matiere-mp-seuil', data.matieresSeuil);
  }

  function renderStockDashboard(data) {
    const alertesPanel = document.getElementById('stock-alertes-panel');
    if (alertesPanel && Array.isArray(data.alertesMp)) {
      alertesPanel.innerHTML = data.alertesMp.map(mp => {
        const isCritical = mp.quantiteStock && mp.quantiteStock.signum() === 0;
        const cssClass = isCritical ? 'alert-box danger' : 'alert-box warning';
        const labelClass = isCritical ? 'alert-label critical' : 'alert-label attention';
        const labelText = isCritical ? 'CRITIQUE' : 'ATTENTION';
        const message = mp.nom + ' (' + mp.fournisseurNom + ') : Reste ' + mp.quantiteStock + ' ' + mp.uniteLibelle + ' (Seuil alerte : ' + mp.seuilMinimum + ' ' + mp.uniteLibelle + ').';
        return '<div class="' + cssClass + '"><span class="' + labelClass + '">' + labelText + '</span><span>' + escapeHtml(message) + '</span></div>';
      }).join('');
      alertesPanel.style.display = data.alertesMp.length > 0 ? 'block' : 'none';
    }

    const emptyHint = document.getElementById('stock-empty-hint');
    if (emptyHint && data.alertesMp && data.alertesMp.length === 0) {
      emptyHint.style.display = 'block';
    } else if (emptyHint) {
      emptyHint.style.display = 'none';
    }

    const produitsBody = document.getElementById('stock-produits-tbody');
    if (produitsBody && Array.isArray(data.produits)) {
      produitsBody.innerHTML = data.produits.map(p => {
        const badge = p.statut === 'SEUIL ATTEINT'
          ? '<span class="badge badge-light-red">Seuil Atteint</span>'
          : '<span class="badge badge-light-green">Normal</span>';
        return '<tr>' +
          '<td>' + escapeHtml(p.nom) + '</td>' +
          '<td>' + escapeHtml(p.ref) + '</td>' +
          '<td>' + p.quantiteStock + '</td>' +
          '<td>' + badge + '</td>' +
        '</tr>';
      }).join('') || '<tr><td colspan="4" style="text-align: center; padding: 24px; color: #64748b;">Aucun produit enregistré.</td></tr>';
    }
  }

  function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  }

  window.VinaDashboardFilters = {
    renderProductionDashboard: renderProductionDashboard,
    renderHistorique: renderHistorique,
    renderMatiereListe: renderMatiereListe,
    renderStockDashboard: renderStockDashboard,
    formatDecimal: formatDecimal,
    escapeHtml: escapeHtml,
  };
})();
