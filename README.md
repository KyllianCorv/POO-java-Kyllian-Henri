# CSV Batch Processor

Programme batch Java qui surveille un dossier, parse des fichiers CSV de remboursements et les persiste dans une base PostgreSQL.

## Stack technique

- Java 21
- Maven
- PostgreSQL (via Docker)
- JDBC natif
- Apache Commons CSV
- Lombok
- SLF4J + Logback
- JUnit 5 + Mockito + H2 (tests)

## Lancer la base de données

```powershell
docker run --name batch-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=batch_db -p 5432:5432 -d postgres:16
```

Créer la table :

```powershell
Get-Content sql/init.sql | docker exec -i batch-postgres psql -U postgres -d batch_db
```

## Configuration

Modifier `src/main/resources/application.properties` si besoin :

```properties
db.url=jdbc:postgresql://localhost:5432/batch_db
db.username=postgres
db.password=postgres

batch.input.dir=input
batch.processed.dir=processed
batch.error.dir=error
```

## Lancer l'application

```powershell
mvn package
java -jar target/csv-batch-processor-1.0.0-jar-with-dependencies.jar
```

L'application scanne le dossier `input/`, traite tous les fichiers `users_YYYYMMDDHHMMSS.csv` trouvés, puis les déplace dans `processed/` (succès) ou `error/` (échec).

## Format des fichiers CSV

Nom du fichier : `users_<YYYYMMDDHHmmSS>.csv`

Contenu (sans en-tête) :

```
<Numero_Securite_Sociale>,<Nom>,<Prenom>,<Date_Naissance>,<Numero_Telephone>,<E_Mail>,<ID_Remboursement>,<Code_Soin>,<Montant_Remboursement>
```

Exemple :

```
1900515012345,Dupont,Jean,15/05/1990,0612345678,jean.dupont@mail.fr,RMB001,CS01,150.75
```

## Logique insert / update

L'`ID_Remboursement` est la clé primaire. Si un enregistrement avec cet ID existe déjà en base, il est mis à jour. Sinon, il est inséré.

## Tests

```powershell
mvn test
```

12 tests unitaires couvrant le modèle, le parser CSV, le DAO et le FolderWatcher.

## IDE

Installer l'extension **Lombok** dans l'IDE pour éviter les faux warnings sur les getters/setters.
