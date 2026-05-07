package com.batch.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class AppConfig {

    private final Properties proprietes = new Properties();

    public AppConfig() {
        chargerProprietes();
    }

    private void chargerProprietes() {
        try (InputStream flux = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (flux == null) {
                throw new RuntimeException("Fichier application.properties introuvable");
            }
            proprietes.load(flux);
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du fichier de configuration", e);
        }
    }

    public Connection obtenirConnexion() throws SQLException {
        String url = proprietes.getProperty("db.url");
        String user = proprietes.getProperty("db.username");
        String password = proprietes.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    public String obtenirDossierEntree() {
        return proprietes.getProperty("batch.input.dir");
    }

    public String obtenirDossierTraite() {
        return proprietes.getProperty("batch.processed.dir");
    }

    public String obtenirDossierErreur() {
        return proprietes.getProperty("batch.error.dir");
    }
}
