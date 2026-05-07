package com.batch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Remboursement {

    private String idRemboursement;
    private String numeroSecu;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String numeroTelephone;
    private String email;
    private String codeSoin;
    private BigDecimal montantRemboursement;
    private LocalDateTime timestampFichier;
}
