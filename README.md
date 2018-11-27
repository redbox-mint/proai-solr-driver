# Proai SOLR Driver

Enables a SOLR index as a Proai data source.

## Running

### Via docker

Use [Peppermint's](https://github.com/redbox-mint/peppermint) docker compose: clone that project and run `docker-compose up`

### Manually (high-level)

- Build driver (see instructions below)
- Deploy a SOLR index
- Deploy Proai inside an application container
- Configure and deploy the [proai.properties](https://github.com/redbox-mint/proai-solr-driver/blob/master/proai.properties) into Proai.

## Configuration

See comments in the [Proai configuration](https://github.com/redbox-mint/proai-solr-driver/blob/master/proai.properties)

## Building
- Run: `./gradlew shadowJar`. `*-fat.jar` will contain all dependencies.
