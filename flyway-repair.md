Suite à un conflit lors de la migration, pour éviter de recreer la base, utiliser flyway-repair 
est aussi une option:
```text
mvn org.flywaydb:flyway-maven-plugin:repair \
  -Dflyway.url=jdbc:postgresql://localhost:5432/vinakoho \
  -Dflyway.user=vinakoho \
  -Dflyway.password=vinakoho \
  -Dflyway.locations=classpath:db/migration
  
```