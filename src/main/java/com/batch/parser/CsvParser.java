package com.batch.parser;

import com.batch.model.Remboursement;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public List<Remboursement> parser(String cheminFichier) throws IOException {
        LocalDateTime timestamp = extraireTimestamp(cheminFichier);
        List<Remboursement> remboursements = new ArrayList<>();

        try (Reader reader = new FileReader(cheminFichier)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : records) {
                remboursements.add(construireRemboursement(record, timestamp));
            }
        }

        return remboursements;
    }

    private Remboursement construireRemboursement(CSVRecord record, LocalDateTime timestamp) {
        return new Remboursement(
                record.get(6).trim(),
                record.get(0).trim(),
                record.get(1).trim(),
                record.get(2).trim(),
                LocalDate.parse(record.get(3).trim(), FORMAT_DATE),
                record.get(4).trim(),
                record.get(5).trim(),
                record.get(7).trim(),
                new BigDecimal(record.get(8).trim()),
                timestamp
        );
    }

    public LocalDateTime extraireTimestamp(String cheminFichier) {
        String nomFichier = cheminFichier.replaceAll(".*[\\\\/]", "");
        String valeur = nomFichier.replace("users_", "").replace(".csv", "");
        return LocalDateTime.parse(valeur, FORMAT_TIMESTAMP);
    }
}
