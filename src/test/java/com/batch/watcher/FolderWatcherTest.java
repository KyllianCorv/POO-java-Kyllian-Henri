package com.batch.watcher;

import com.batch.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FolderWatcherTest {

    private static final String URL = "jdbc:h2:mem:watcherdb;DB_CLOSE_DELAY=-1";

    @TempDir
    Path dossierTemp;

    private Path entree;
    private Path traite;
    private Path erreur;
    private AppConfig config;
    private FolderWatcher watcher;

    @BeforeEach
    void init() throws Exception {
        entree = dossierTemp.resolve("input");
        traite = dossierTemp.resolve("processed");
        erreur = dossierTemp.resolve("error");
        Files.createDirectories(entree);
        Files.createDirectories(traite);
        Files.createDirectories(erreur);

        try (Connection conn = DriverManager.getConnection(URL)) {
            String schema = Files.readString(Path.of("src/test/resources/schema-test.sql"));
            conn.createStatement().execute(schema);
            conn.createStatement().execute("DELETE FROM remboursement");
        }

        config = mock(AppConfig.class);
        when(config.obtenirDossierEntree()).thenReturn(entree.toString());
        when(config.obtenirDossierTraite()).thenReturn(traite.toString());
        when(config.obtenirDossierErreur()).thenReturn(erreur.toString());
        when(config.obtenirConnexion()).thenAnswer(inv -> DriverManager.getConnection(URL));

        watcher = new FolderWatcher(config);
    }

    @Test
    void traiterDossier_deplaceFichierDansTraite() throws Exception {
        Path fichier = entree.resolve("users_20260501000000.csv");
        Files.writeString(fichier,
                "1900515012345,Dupont,Jean,15/05/1990,0612345678,jean@mail.fr,RMB001,CS01,150.75\n"
        );

        watcher.traiterDossier();

        assertFalse(Files.exists(fichier));
        assertTrue(Files.exists(traite.resolve("users_20260501000000.csv")));
    }

    @Test
    void traiterDossier_fichierInvalide_deplaceDansErreur() throws Exception {
        Path fichier = entree.resolve("users_20260501000000.csv");
        Files.writeString(fichier, "donnees;invalides;format\n");

        watcher.traiterDossier();

        assertFalse(Files.exists(fichier));
        assertTrue(Files.exists(erreur.resolve("users_20260501000000.csv")));
    }

    @Test
    void traiterDossier_ignoreFichiersNonConformes() throws Exception {
        Path fichierIgnore = entree.resolve("export.csv");
        Files.writeString(fichierIgnore, "quelque chose");

        watcher.traiterDossier();

        assertTrue(Files.exists(fichierIgnore));
    }
}
