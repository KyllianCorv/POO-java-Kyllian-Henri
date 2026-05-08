Infos : Attention VSCODE install extension lombok
docker : docker run --name batch-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=batch_db -p 5432:5432 -d postgres:16
Bdd : Get-Content sql/init.sql | docker exec -i batch-postgres psql -U postgres -d batch_db
test : mvn test



Info avancement : 
Kyllian : Normalement j'ai poussé le code qu'on a fait le weekend dernier ensemble
08/05 : il reste la partie 6 : henri je te laisse faire la partie du Main, avec les Tests U normalement tous est ok chaque methode passe si j'ai le temps je ferais la partie de l'interface graphique en bonus pour dire que ce soit plus agreable à utiliser