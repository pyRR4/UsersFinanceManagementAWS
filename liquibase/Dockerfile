FROM liquibase/liquibase:latest

WORKDIR /liquibase
COPY changelogs/ ./changelogs/
COPY run-migration.sh /liquibase/run-migration.sh

RUN chmod +x /liquibase/run-migration.sh