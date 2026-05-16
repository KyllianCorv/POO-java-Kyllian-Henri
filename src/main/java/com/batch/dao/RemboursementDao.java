package com.batch.dao;

import com.batch.config.AppConfig;
import com.batch.model.Remboursement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemboursementDao {

    private final AppConfig config;

    public RemboursementDao(AppConfig config) {
        this.config = config;
    }

    public void sauvegarder(Remboursement r) throws SQLException {
        try (Connection conn = config.obtenirConnexion()) {
            if (existe(r.getIdRemboursement(), conn)) {
                mettreAJour(r, conn);
            } else {
                inserer(r, conn);
            }
        }
    }

    private boolean existe(String idRemboursement, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM remboursement WHERE id_remboursement = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idRemboursement);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void inserer(Remboursement r, Connection conn) throws SQLException {
        String sql = "INSERT INTO remboursement (id_remboursement, numero_secu, nom, prenom, date_naissance, numero_telephone, email, code_soin, montant_remboursement, timestamp_fichier) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            remplirStatement(ps, r);
            ps.executeUpdate();
        }
    }

    private void mettreAJour(Remboursement r, Connection conn) throws SQLException {
        String sql = "UPDATE remboursement SET numero_secu = ?, nom = ?, prenom = ?, date_naissance = ?, numero_telephone = ?, email = ?, code_soin = ?, montant_remboursement = ?, timestamp_fichier = ? WHERE id_remboursement = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNumeroSecu());
            ps.setString(2, r.getNom());
            ps.setString(3, r.getPrenom());
            ps.setDate(4, Date.valueOf(r.getDateNaissance()));
            ps.setString(5, r.getNumeroTelephone());
            ps.setString(6, r.getEmail());
            ps.setString(7, r.getCodeSoin());
            ps.setBigDecimal(8, r.getMontantRemboursement());
            ps.setTimestamp(9, Timestamp.valueOf(r.getTimestampFichier()));
            ps.setString(10, r.getIdRemboursement());
            ps.executeUpdate();
        }
    }

    private void remplirStatement(PreparedStatement ps, Remboursement r) throws SQLException {
        ps.setString(1, r.getIdRemboursement());
        ps.setString(2, r.getNumeroSecu());
        ps.setString(3, r.getNom());
        ps.setString(4, r.getPrenom());
        ps.setDate(5, Date.valueOf(r.getDateNaissance()));
        ps.setString(6, r.getNumeroTelephone());
        ps.setString(7, r.getEmail());
        ps.setString(8, r.getCodeSoin());
        ps.setBigDecimal(9, r.getMontantRemboursement());
        ps.setTimestamp(10, Timestamp.valueOf(r.getTimestampFichier()));
    }

    public List<Remboursement> listerTous() throws SQLException {
        List<Remboursement> remboursements = new ArrayList<>();

        String sql = "SELECT id_remboursement, numero_secu, nom, prenom, date_naissance, " +
                "numero_telephone, email, code_soin, montant_remboursement, timestamp_fichier " +
                "FROM remboursement";

        try (Connection conn = config.obtenirConnexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Remboursement r = new Remboursement();

                r.setIdRemboursement(rs.getString("id_remboursement"));
                r.setNumeroSecu(rs.getString("numero_secu"));
                r.setNom(rs.getString("nom"));
                r.setPrenom(rs.getString("prenom"));
                r.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
                r.setNumeroTelephone(rs.getString("numero_telephone"));
                r.setEmail(rs.getString("email"));
                r.setCodeSoin(rs.getString("code_soin"));
                r.setMontantRemboursement(rs.getBigDecimal("montant_remboursement"));
                r.setTimestampFichier(rs.getTimestamp("timestamp_fichier").toLocalDateTime());

                remboursements.add(r);
            }
        }

        return remboursements;
    }
}
