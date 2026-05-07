CREATE TABLE IF NOT EXISTS remboursement (
    id_remboursement     VARCHAR(50)    PRIMARY KEY,
    numero_secu          VARCHAR(15)    NOT NULL,
    nom                  VARCHAR(100)   NOT NULL,
    prenom               VARCHAR(100)   NOT NULL,
    date_naissance       DATE           NOT NULL,
    numero_telephone     VARCHAR(20),
    email                VARCHAR(150),
    code_soin            VARCHAR(20)    NOT NULL,
    montant_remboursement NUMERIC(10,2) NOT NULL,
    timestamp_fichier    TIMESTAMP      NOT NULL
);
