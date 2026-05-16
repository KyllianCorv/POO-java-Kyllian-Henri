//
// NAVIGATION
//

function showPage(pageId, button) {

    const pages = document.querySelectorAll(".page");

    pages.forEach(p => p.classList.remove("active-page"));

    document.getElementById(pageId)
        .classList.add("active-page");

    document.querySelectorAll(".nav-button")
        .forEach(b => b.classList.remove("active"));

    button.classList.add("active");

    // 👇 AUTO LOAD DB PAGE
    if (pageId === "database-page") {

        try {
            javaBackend.loadDatabaseData();
        } catch (e) {
            console.log(e);
        }
    }
}

function waitForJavaBackend(callback) {

    const interval = setInterval(() => {

        if (window.javaBackend &&
            typeof window.javaBackend.refreshFiles === "function") {

            clearInterval(interval);
            callback();
        }

    }, 100);
}

//
// ACTIONS PAGE 1
//

function addFile() {

    updateStatus("Ouverture du sélecteur de fichier...");

    try {
        javaBackend.addFile();
    } catch (e) {
        updateStatus("Erreur Java : " + e);
    }
}

function refreshFiles() {

    updateStatus("Actualisation des fichiers...");

    try {
        javaBackend.refreshFiles();
    } catch (e) {
        updateStatus("Erreur Java : " + e);
    }
}

function clearAllFiles() {

    const input =
        document.getElementById("input-files");

    const processed =
        document.getElementById("processed-files");

    const errors =
        document.getElementById("error-files");

    if (input) input.innerHTML = "";
    if (processed) processed.innerHTML = "";
    if (errors) errors.innerHTML = "";
}

function addInputFile(filename) {

    const list =
        document.getElementById("input-files");

    if (!list) return;

    const item =
        document.createElement("li");

    item.textContent = filename;

    list.appendChild(item);
}

function addProcessedFile(filename) {

    const list =
        document.getElementById("processed-files");

    if (!list) return;

    const item =
        document.createElement("li");

    item.textContent = filename;

    list.appendChild(item);
}

function addErrorFile(filename) {

    const list =
        document.getElementById("error-files");

    if (!list) return;

    const item =
        document.createElement("li");

    item.textContent = filename;

    list.appendChild(item);
}

function launchCsvProcessing() {

    try {

        updateProgress("Lancement du traitement CSV...");

        javaBackend.launchCsvProcessing();

        stopLoader();

    } catch (e) {

        stopLoader("Erreur Java : " + e);
    }
}

//
// PAGE 2
//

function loadDatabaseData() {

    updateStatus("Chargement des données...");

    try {
        javaBackend.loadDatabaseData();
    } catch (e) {
        updateStatus("Erreur Java : " + e);
    }
}

function clearDatabaseTable() {

    const tbody =
        document.getElementById("database-content");

    if (!tbody) return;

    tbody.innerHTML = "";
}

function addDatabaseRow(idRemboursement, numeroSecu, nom, prenom, dateNaissance, numeroTelephone, email, codeSoin, montantRemboursement, timestampFichier) {

    const tbody =
        document.getElementById("database-content");

    if (!tbody) return;

    const row =
        document.createElement("tr");

    row.innerHTML = `
        <td>${idRemboursement}</td>
        <td>${numeroSecu}</td>
        <td>${nom}</td>
        <td>${prenom}</td>
        <td>${dateNaissance}</td>
        <td>${numeroTelephone}</td>
        <td>${email}</td>
        <td>${codeSoin}</td>
        <td>${montantRemboursement}</td>
        <td>${timestampFichier}</td>
    `;

    tbody.appendChild(row);
}

//
// STATUS
//

function updateStatus(message) {

    document.getElementById("status-message")
        .innerText = message;
}

//
// PROGRESS
//

function stopLoader(status) {

    const loader =
        document.getElementById("loader");

    const text =
        document.getElementById("progress-status");

    if (loader) {
        loader.style.display = "none";
    }

    if (text) {
        text.innerText = status || "Terminé";
    }
}

function updateProgress(status) {

    const loader =
        document.getElementById("loader");

    const text =
        document.getElementById("progress-status");

    if (loader) {
        loader.style.display = "inline-block";
    }

    if (text) {
        text.innerText = status;
    }
}

//
// JAVA -> JAVASCRIPT
//

function receiveMessageFromJava(message) {

    updateStatus(message);
}

//
// refresh des dossiers au démarrage
//

window.addEventListener("DOMContentLoaded", () => {

    updateStatus("Chargement de l'application...");

    waitForJavaBackend(() => {

        updateStatus("Synchronisation des fichiers...");

        try {
            javaBackend.refreshFiles();

            updateStatus("Prêt");

        } catch (e) {
            updateStatus("Erreur init Java : " + e);
        }
    });
});