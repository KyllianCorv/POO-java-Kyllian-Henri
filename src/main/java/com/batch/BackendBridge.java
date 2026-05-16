package com.batch;

import com.batch.dao.RemboursementDao;
import com.batch.model.Remboursement;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import com.batch.config.AppConfig;
import com.batch.watcher.FolderWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BackendBridge {

    private final WebEngine engine;
    private final AppConfig config;
    private final FolderWatcher watcher;

    public BackendBridge(WebEngine engine) {

        this.engine = engine;
        this.config = new AppConfig();
        this.watcher = new FolderWatcher(config);
    }

    private void scanDirectory(Path dir, java.util.function.Consumer<String> callback) {

        try {

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                return;
            }

            List<String> files =
                    Files.list(dir)
                            .filter(Files::isRegularFile)
                            .map(path -> path.getFileName().toString())
                            .collect(Collectors.toList());

            for (String file : files) {
                callback.accept(file);
            }

        } catch (IOException e) {

            e.printStackTrace();

            sendStatus("Erreur scan dossier : " + dir.getFileName());
        }
    }

    // =========================================================
    // JAVASCRIPT -> JAVA
    // =========================================================

    /**
     * Ajout d'un fichier CSV
     */
    public void addFile() {

        System.out.println("[JAVA] addFile() appelé");

        Platform.runLater(() -> {

            try {
                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Sélectionner un fichier CSV");

                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("CSV Files", "*.csv")
                );

                File selectedFile =
                        fileChooser.showOpenDialog(null);

                if (selectedFile == null) {
                    sendStatus("Aucun fichier sélectionné");
                    return;
                }

                Path projectRoot =
                        Paths.get(System.getProperty("user.dir"));

                Path inputDir =
                        projectRoot.resolve("input");

                Files.createDirectories(inputDir);

                Path targetFile =
                        inputDir.resolve(selectedFile.getName());

                Files.copy(
                        selectedFile.toPath(),
                        targetFile,
                        StandardCopyOption.REPLACE_EXISTING
                );

                sendStatus("Fichier ajouté dans INPUT : " + selectedFile.getName());

                refreshFiles();

            } catch (Exception e) {

                e.printStackTrace();

                sendStatus("Erreur addFile : " + e.getMessage());
            }
        });
    }

    /**
     * Refresh des listes de fichiers
     */
    public void refreshFiles() {

        System.out.println("[JAVA] refreshFiles() appelé");

        Platform.runLater(() -> {

            try {

                clearAllFiles();

                Path projectRoot =
                        Paths.get(System.getProperty("user.dir"));

                Path inputDir =
                        projectRoot.resolve("input");

                Path processedDir =
                        projectRoot.resolve("processed");

                Path errorsDir =
                        projectRoot.resolve("error");

                scanDirectory(inputDir, this::addInputFile);
                scanDirectory(processedDir, this::addProcessedFile);
                scanDirectory(errorsDir, this::addErrorFile);

                sendStatus("Dossier mise à jour");

            } catch (Exception e) {

                e.printStackTrace();

                sendStatus("Erreur refresh : " + e.getMessage());
            }
        });
    }

    /**
     * Lancement du traitement CSV
     */
    public void launchCsvProcessing() {

        System.out.println(
                "[JAVA] launchCsvProcessing() appelé"
        );

        Logger log = LoggerFactory.getLogger(Main.class);
        log.info("=== CSV Batch Processor - Demarrage ===");

        try {
            watcher.traiterDossier();
        } catch (Exception e) {
            log.error("Erreur critique : {}", e.getMessage(), e);
            System.exit(1);
        }

        log.info("=== CSV Batch Processor - Termine ===");
        refreshFiles();
    }

    /**
     * Chargement des données DB
     */
    public void loadDatabaseData() {

        System.out.println("[JAVA] loadDatabaseData() appelé");

        clearDatabaseTable();

        RemboursementDao dao =
                new RemboursementDao(config);

        List<Remboursement> list =
                null;
        try {
            list = dao.listerTous();
            for (Remboursement r : list) {

                addDatabaseRow(

                        r.getIdRemboursement(),
                        r.getNumeroSecu(),
                        r.getNom(),
                        r.getPrenom(),
                        r.getDateNaissance().toString(),
                        r.getNumeroTelephone(),
                        r.getEmail(),
                        r.getCodeSoin(),
                        r.getMontantRemboursement().toString(),
                        r.getTimestampFichier().toString()
                );
            }

            sendStatus("Données chargées : " + list.size());

        } catch (SQLException e) {
            sendStatus("DB Erreur refresh : " + e.getMessage());
        }
    }

    // =========================================================
    // JAVA -> JAVASCRIPT
    // =========================================================

    /**
     * Mise à jour du message de status
     */
    public void sendStatus(String message) {

        Platform.runLater(() -> {

            engine.executeScript(
                    "updateStatus('" + escape(message) + "')"
            );
        });
    }

    /**
     * Mise à jour de la barre de progression
     */
    public void updateProgress(String status) {

        Platform.runLater(() -> {

            try {
                engine.executeScript(
                        "updateProgress('" + escape(status) + "')"
                );
            } catch (Exception e) {
                System.out.println("JS error: " + e.getMessage());
            }
        });
    }

    /**
     * Ajout d'un fichier dans INPUT
     */
    public void addInputFile(String filename) {

        Platform.runLater(() -> {

            engine.executeScript(
                    "addInputFile('" + escape(filename) + "')"
            );
        });
    }

    /**
     * Ajout d'un fichier dans PROCESSED
     */
    public void addProcessedFile(String filename) {

        Platform.runLater(() -> {

            engine.executeScript(
                    "addProcessedFile('" + escape(filename) + "')"
            );
        });
    }

    /**
     * Ajout d'un fichier dans ERRORS
     */
    public void addErrorFile(String filename) {

        Platform.runLater(() -> {

            engine.executeScript(
                    "addErrorFile('" + escape(filename) + "')"
            );
        });
    }

    /**
     * Clear des listes frontend
     */
    public void clearAllFiles() {

        Platform.runLater(() -> {

            engine.executeScript(
                    "clearAllFiles()"
            );
        });
    }

    /**
     * Ajout d'une ligne dans la table DB
     */
    public void addDatabaseRow(
            String idRemboursement,
            String numeroSecu,
            String nom,
            String prenom,
            String dateNaissance,
            String numeroTelephone,
            String email,
            String codeSoin,
            String montantRemboursement,
            String timestampFichier
    ) {

        Platform.runLater(() -> {

            try {

                engine.executeScript(
                        "addDatabaseRow("
                                + "'" + escape(idRemboursement) + "', "
                                + "'" + escape(numeroSecu) + "', "
                                + "'" + escape(nom) + "', "
                                + "'" + escape(prenom) + "', "
                                + "'" + escape(dateNaissance) + "', "
                                + "'" + escape(numeroTelephone) + "', "
                                + "'" + escape(email) + "', "
                                + "'" + escape(codeSoin) + "', "
                                + "'" + escape(montantRemboursement) + "', "
                                + "'" + escape(timestampFichier) + "'"
                                + ")"
                );

            } catch (Exception e) {

                System.out.println("JS error addDatabaseRow: " + e.getMessage());
            }
        });
    }

    /**
     * Clear table DB
     */
    public void clearDatabaseTable() {

        Platform.runLater(() -> {

            engine.executeScript(
                    "clearDatabaseTable()"
            );
        });
    }

    // =========================================================
    // SECURITE JAVASCRIPT
    // =========================================================

    private String escape(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
    }
}