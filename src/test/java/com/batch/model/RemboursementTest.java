package com.batch.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class RemboursementTest {

    @Test
    void constructeurComplet_renseigneTousLesChamps() {
        LocalDate dateNaissance = LocalDate.of(1990, 5, 15);
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 10, 8, 30, 0);
        BigDecimal montant = new BigDecimal("150.75");

        Remboursement r = new Remboursement(
                "RMB001", "1900515012345", "Dupont", "Jean",
                dateNaissance, "0612345678", "jean.dupont@mail.fr",
                "CS01", montant, timestamp
        );

        assertEquals("RMB001", r.getIdRemboursement());
        assertEquals("1900515012345", r.getNumeroSecu());
        assertEquals("Dupont", r.getNom());
        assertEquals("Jean", r.getPrenom());
        assertEquals(dateNaissance, r.getDateNaissance());
        assertEquals("0612345678", r.getNumeroTelephone());
        assertEquals("jean.dupont@mail.fr", r.getEmail());
        assertEquals("CS01", r.getCodeSoin());
        assertEquals(montant, r.getMontantRemboursement());
        assertEquals(timestamp, r.getTimestampFichier());
    }

    @Test
    void setters_modifientLesValeurs() {
        Remboursement r = new Remboursement();
        r.setIdRemboursement("RMB002");
        r.setNom("Martin");
        r.setMontantRemboursement(new BigDecimal("200.00"));

        assertEquals("RMB002", r.getIdRemboursement());
        assertEquals("Martin", r.getNom());
        assertEquals(new BigDecimal("200.00"), r.getMontantRemboursement());
    }
}
