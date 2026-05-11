package com.batch.watcher;

import com.batch.config.AppConfig;
import com.batch.dao.RemboursementDao;
import com.batch.model.Remboursement;
import com.batch.parser.CsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

public class FolderWatcher {

    private static final Logger log = LoggerFactory.getLogger(FolderWatcher.class);

    private final AppConfig config;
    private final CsvParser parser;
    private final RemboursementDao dao;

    public FolderWatcher(AppConfig config) {
        this.config = config;
        this.parser = new CsvParser();
        this.dao = new RemboursementDao(config);
    }

    public void traiterDossier() {
        Path dossierEntree = Paths.get(config.obtenirDossierEntree());
        Path dossierTraite = Paths.get(config.obtenirDossierTraite());
        Path dossierErreur = Paths.get(config.obtenirDossierErreur());

        creerSiAbsent(dossierEntree);
        creerSiAbsent(dossierTraite);
        creerSiAbsent(dossierErreur);

        List<Path> fichiers = listerFichiersCsv(dossierEntree);

        if (fichiers.isEmpty()) {
            log.info("Aucun fichier a traiter dans {}", dossierEntree);
            return;
        }

        for (Path fichier : fichiers) {
            traiterFichier(fichier, dossierTraite, dossierErreur);
        }
    }

    private void traiterFichier(Path fichier, Path dossierTraite, Path dossierErreur) {
        log.info("Traitement de {}", fichier.getFileName());
        try {
            List<Remboursement> remboursements = parser.parser(fichier.toString());
            for (Remboursement r : remboursements) {
                dao.sauvegarder(r);
            }
            deplacer(fichier, dossierTraite);
            log.info("{} traite avec succes ({} lignes)", fichier.getFileName(), remboursements.size());
        } catch (Exception e) {
            log.error("Erreur lors du traitement de {} : {}", fichier.getFileName(), e.getMessage());
            deplacer(fichier, dossierErreur);
        }
    }

    private List<Path> listerFichiersCsv(Path dossier) {
        try (Stream<Path> stream = Files.list(dossier)) {
            return stream
                    .filter(p -> p.getFileName().toString().matches("users_\\d{14}\\.csv"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.error("Impossible de lister le dossier {} : {}", dossier, e.getMessage());
            return List.of();
        }
    }

    private void deplacer(Path fichier, Path destination) {
        try {
            Files.move(fichier, destination.resolve(fichier.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Impossible de deplacer {} vers {} : {}", fichier.getFileName(), destination, e.getMessage());
        }
    }

    private void creerSiAbsent(Path dossier) {
        try {
            Files.createDirectories(dossier);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de creer le dossier " + dossier, e);
        }
    }
}
