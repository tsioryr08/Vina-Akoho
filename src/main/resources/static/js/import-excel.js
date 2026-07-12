/**
 * Composant générique d'import Excel en 2 étapes (aperçu -> confirmation).
 * Usage : initImportExcel({ urlApercu, urlImport, inputFileId, formId, resultatContainerId })
 */
function initImportExcel(config) {
  const form = document.getElementById(config.formId);
  const inputFile = document.getElementById(config.inputFileId);
  const resultatContainer = document.getElementById(config.resultatContainerId);
  let dernierFichier = null;

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    dernierFichier = inputFile.files[0];
    if (!dernierFichier) {
      alert("Veuillez sélectionner un fichier.");
      return;
    }
    lancerApercu();
  });

  function lancerApercu() {
    const formData = new FormData();
    formData.append("fichier", dernierFichier);

    resultatContainer.innerHTML = "<p>Analyse du fichier en cours...</p>";

    fetch(config.urlApercu, { method: "POST", body: formData })
      .then((res) => res.json())
      .then((reponse) => afficherResultat(reponse.data, false))
      .catch(() => {
        resultatContainer.innerHTML =
          '<div class="info-banner error">Erreur lors de la lecture du fichier.</div>';
      });
  }

  function confirmerImport(forcerDoublons) {
    const formData = new FormData();
    formData.append("fichier", dernierFichier);

    resultatContainer.innerHTML = "<p>Import en cours...</p>";

    fetch(config.urlImport + "?forcerDoublons=" + forcerDoublons, {
      method: "POST",
      body: formData,
    })
      .then((res) => res.json())
      .then((reponse) => afficherResultat(reponse.data, true))
      .catch(() => {
        resultatContainer.innerHTML =
          '<div class="info-banner error">Erreur lors de l\'import.</div>';
      });
  }

  function afficherResultat(data, estImportReel) {
    let html = "";

    html += `<div class="info-banner ${data.lignesImportees.length > 0 ? "success" : ""}">`;
    html += `${data.totalLignesLues} ligne(s) lue(s) — `;
    html += `<strong>${data.lignesImportees.length}</strong> valide(s)`;
    html += estImportReel ? " importée(s)" : " prête(s) à importer";
    html += `, <strong>${data.erreurs.length}</strong> erreur(s), `;
    html += `<strong>${data.doublons.length}</strong> doublon(s).</div>`;

    if (data.erreurs.length > 0) {
      html += `<h4>Erreurs</h4><table class="data-table"><thead><tr><th>Ligne</th><th>Message</th></tr></thead><tbody>`;
      data.erreurs.forEach((e) => {
        html += `<tr><td>${e.numeroLigne}</td><td>${e.message}</td></tr>`;
      });
      html += `</tbody></table>`;
    }

    if (data.doublons.length > 0) {
      html += `<h4>Doublons détectés</h4><table class="data-table"><thead><tr><th>Ligne</th><th>Valeur</th><th>Message</th></tr></thead><tbody>`;
      data.doublons.forEach((d) => {
        html += `<tr><td>${d.numeroLigne}</td><td>${d.cleConflit}</td><td>${d.message}</td></tr>`;
      });
      html += `</tbody></table>`;

      if (!estImportReel) {
        html += `<div class="form-actions">
          <button type="button" class="btn btn-primary" id="btnImporterSansDoublons">Importer les lignes valides (ignorer les doublons)</button>
          <button type="button" class="btn btn-secondary" id="btnImporterAvecDoublons">Continuer quand même (importer aussi les doublons)</button>
        </div>`;
      }
    } else if (!estImportReel && data.lignesImportees.length > 0) {
      html += `<div class="form-actions">
        <button type="button" class="btn btn-primary" id="btnConfirmerImport">Confirmer l'import de ${data.lignesImportees.length} ligne(s)</button>
      </div>`;
    }

    resultatContainer.innerHTML = html;

    const btnConfirmer = document.getElementById("btnConfirmerImport");
    if (btnConfirmer) btnConfirmer.addEventListener("click", () => confirmerImport(false));

    const btnSansDoublons = document.getElementById("btnImporterSansDoublons");
    if (btnSansDoublons) btnSansDoublons.addEventListener("click", () => confirmerImport(false));

    const btnAvecDoublons = document.getElementById("btnImporterAvecDoublons");
    if (btnAvecDoublons) btnAvecDoublons.addEventListener("click", () => confirmerImport(true));
  }
}