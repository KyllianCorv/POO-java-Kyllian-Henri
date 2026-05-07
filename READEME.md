Infos : Attention VSCODE install extension lombok
docker : docker run --name batch-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=batch_db -p 5432:5432 -d postgres:16
Bdd : Get-Content sql/init.sql | docker exec -i batch-postgres psql -U postgres -d batch_db
test : mvn test



Info avancement : 
Kyllian : Normalement j'ai poussé le code qu'on à fais le weekend dernier ensemble

Il reste à faire : 
Partie 4 — DAO (insert/update en BDD via JDBC)
Partie 5 — FolderWatcher (surveillance du dossier, orchestration, déplacement des fichiers)
Partie 6 — Main complet + README
Partie 7 — Interface graphique