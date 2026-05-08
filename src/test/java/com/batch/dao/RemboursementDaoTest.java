package com.batch.dao;

import com.batch.config.AppConfig;
import com.batch.model.Remboursement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemboursementDaoTest {

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private AppConfig config;
    private RemboursementDao dao;

    @BeforeEach
    void init() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String schema = Files.readString(Path.of("src/test/resources/schema-test.sql"));
            conn.createStatement().execute(schema);
            conn.createStatement().execute("DELETE FROM remboursement");
        }

        config = mock(AppConfig.class);
        when(config.obtenirConnexion()).thenAnswer(inv -> DriverManager.getConnection(URL));

        dao = new RemboursementDao(config);
    }

    private Remboursement remboursementTest(String id) {
        return new Remboursement(
                id, "1900515012345", "Dupont", "Jean",
                LocalDate.of(1990, 5, 15), "0612345678", "jean@mail.fr",
                "CS01", new BigDecimal("150.75"),
                LocalDateTime.of(2026, 5, 1, 0, 0, 0)
        );
    }

    @Test
    void sauvegarder_insereLigneQuandAbsente() throws Exception {
        dao.sauvegarder(remboursementTest("RMB001"));

        try (Connection conn = DriverManager.getConnection(URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM remboursement WHERE id_remboursement = 'RMB001'");
            rs.next();
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    void sauvegarder_metAJourQuandDejaPresente() throws Exception {
        dao.sauvegarder(remboursementTest("RMB001"));

        Remboursement modifie = remboursementTest("RMB001");
        modifie.setNom("Martin");
        modifie.setMontantRemboursement(new BigDecimal("999.99"));
        dao.sauvegarder(modifie);

        try (Connection conn = DriverManager.getConnection(URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT nom, montant_remboursement FROM remboursement WHERE id_remboursement = 'RMB001'");
            rs.next();
            assertEquals("Martin", rs.getString("nom"));
            assertEquals(new BigDecimal("999.99"), rs.getBigDecimal("montant_remboursement"));
        }
    }

    @Test
    void sauvegarder_deuxLignesDifferentes() throws Exception {
        dao.sauvegarder(remboursementTest("RMB001"));
        dao.sauvegarder(remboursementTest("RMB002"));

        try (Connection conn = DriverManager.getConnection(URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM remboursement");
            rs.next();
            assertEquals(2, rs.getInt(1));
        }
    }
}
