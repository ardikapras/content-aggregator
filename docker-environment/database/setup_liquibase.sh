#!/usr/bin/env bash

set -eu

stage=$1
LIQUIBASE_DATABASE=${LIQUIBASE_DATABASE:='content'}

export LIQUIBASE_HUB_MODE="off"
export LIQUIBASE_HOST=${LIQUIBASE_HOST:='localhost'}
export LIQUIBASE_PORT=${LIQUIBASE_PORT:-'5437'}
export LIQUIBASE_PASSWORD=${LIQUIBASE_PASSWORD:-'postgres'}
export LIQUIBASE_CLASSPATH=${LIQUIBASE_CLASSPATH:-''}
export LIQUIBASE_URL_OPTIONS=${LIQUIBASE_URL_OPTIONS:-''}
export LIQUIBASE_URL_APPLICATION_NAME='ApplicationName=Liquibase_'${stage}

case $stage in
initialise)
  export LIQUIBASE_USERNAME="postgres"
  export LIQUIBASE_COMMAND_UPDATE_USERNAME="postgres"
  export LIQUIBASE_URL="jdbc:postgresql://$LIQUIBASE_HOST:$LIQUIBASE_PORT/postgres"
  export LIQUIBASE_DATABASE_CHANGELOG_TABLE_NAME=databasechangelog_$LIQUIBASE_DATABASE
  export LIQUIBASE_DATABASE_CHANGELOG_LOCK_TABLE_NAME=databasechangeloglock_$LIQUIBASE_DATABASE
  export LIQUIBASE_COMMAND_CHANGELOG_FILE="database/sql/db.changelog-initialise.yaml"
  export LIQUIBASE_LIQUIBASE_SCHEMA_NAME="public"
  ;;
prepare)
  export LIQUIBASE_USERNAME="postgres"
  export LIQUIBASE_URL="jdbc:postgresql://$LIQUIBASE_HOST:$LIQUIBASE_PORT/$LIQUIBASE_DATABASE"
  export LIQUIBASE_DATABASE_CHANGELOG_TABLE_NAME=databasechangelog_prepare
  export LIQUIBASE_DATABASE_CHANGELOG_LOCK_TABLE_NAME=databasechangeloglock_prepare
  export LIQUIBASE_COMMAND_CHANGELOG_FILE="database/sql/db.changelog-prepare.yaml"
  export LIQUIBASE_LIQUIBASE_SCHEMA_NAME="public"
  ;;
*)
  export LIQUIBASE_USERNAME=${LIQUIBASE_USERNAME:="posuser"}
  export LIQUIBASE_URL="jdbc:postgresql://$LIQUIBASE_HOST:$LIQUIBASE_PORT/$LIQUIBASE_DATABASE"
  export LIQUIBASE_DATABASE_CHANGELOG_TABLE_NAME=databasechangelog_$stage
  export LIQUIBASE_DATABASE_CHANGELOG_LOCK_TABLE_NAME=databasechangeloglock_$stage
  export LIQUIBASE_COMMAND_CHANGELOG_FILE="database/sql/db.changelog-$stage.yaml"
  export LIQUIBASE_LIQUIBASE_SCHEMA_NAME="liquibase"
  ;;
esac

echo "
url: ${LIQUIBASE_URL}?${LIQUIBASE_URL_APPLICATION_NAME}&${LIQUIBASE_URL_OPTIONS}
username: $LIQUIBASE_USERNAME
password: $LIQUIBASE_PASSWORD
classpath: $LIQUIBASE_CLASSPATH
databaseChangelogTableName: $LIQUIBASE_DATABASE_CHANGELOG_TABLE_NAME
databaseChangelogLockTableName: $LIQUIBASE_DATABASE_CHANGELOG_LOCK_TABLE_NAME
changeLogFile: $LIQUIBASE_COMMAND_CHANGELOG_FILE
hub.mode: $LIQUIBASE_HUB_MODE
liquibaseSchemaName: $LIQUIBASE_LIQUIBASE_SCHEMA_NAME
" > liquibase.properties
