Infos : Attention VSCODE install extension lombok
docker : docker run --name batch-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=batch_db -p 5432:5432 -d postgres:16
Bdd : Get-Content sql/init.sql | docker exec -i batch-postgres psql -U postgres -d batch_db
test : mvn test