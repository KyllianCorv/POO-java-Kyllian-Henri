package com.batch.parser;

import com.batch.model.Remboursement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    private CsvParser parser;

    @BeforeEach
    void init() {
        parser = new CsvParser();
    }

    @Test
    void parser_retourneLaBonneListe(@TempDir Path dossierTemp) throws IOException {
        Path fichier = dossierTemp.resolve("users_20240110083000.csv");
        Files.writeString(fichier,
                "1900515012345,Dupont,Jean,15/05/1990,0612345678,jean.dupont@mail.fr,RMB001,CS01,150.75\n" +
                "2850322098765,Martin,Sophie,22/03/1985,0698765432,sophie.martin@mail.fr,RMB002,CS02,200.00\n"
        );

        List<Remboursement> liste = parser.parser(fichier.toString());

        assertEquals(2, liste.size());

        Remboursement r = liste.get(0);
        assertEquals("RMB001", r.getIdRemboursement());
        assertEquals("1900515012345", r.getNumeroSecu());
        assertEquals("Dupont", r.getNom());
        assertEquals("Jean", r.getPrenom());
        assertEquals(LocalDate.of(1990, 5, 15), r.getDateNaissance());
        assertEquals("0612345678", r.getNumeroTelephone());
        assertEquals("jean.dupont@mail.fr", r.getEmail());
        assertEquals("CS01", r.getCodeSoin());
        assertEquals(new BigDecimal("150.75"), r.getMontantRemboursement());
        assertEquals(LocalDateTime.of(2024, 1, 10, 8, 30, 0), r.getTimestampFichier());
    }

    @Test
    void extraireTimestamp_retourneLeBonTimestamp() {
        LocalDateTime ts = parser.extraireTimestamp("input/users_20240110083000.csv");
        assertEquals(LocalDateTime.of(2024, 1, 10, 8, 30, 0), ts);
    }

    @Test
    void extraireTimestamp_avecCheminWindows() {
        LocalDateTime ts = parser.extraireTimestamp("C:\\batch\\input\\users_20241231235959.csv");
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59, 59), ts);
    }

    @Test
    void parser_fichierVide_retourneListeVide(@TempDir Path dossierTemp) throws IOException {
        Path fichier = dossierTemp.resolve("users_20240110083000.csv");
        Files.writeString(fichier, "");

        List<Remboursement> liste = parser.parser(fichier.toString());

        assertTrue(liste.isEmpty());
    }
}
