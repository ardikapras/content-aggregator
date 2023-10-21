#!/usr/bin/env bash

set -eu

DB_NAME=content
DATABASE_HOST=$1
POSTGRES_PASSWORD=$2
POSUSER_PASSWORD=$3

current_dir=$(dirname $0)

export LIQUIBASE_HOST=${DATABASE_HOST}

export LIQUIBASE_PASSWORD=${POSTGRES_PASSWORD}

echo "Running initialise on ${DB_NAME}"
"${current_dir}"/setup_liquibase.sh initialise; liquibase --log-level INFO --log-file=liquibase.log update

echo "Running prepare on ${DB_NAME}"
"${current_dir}"/setup_liquibase.sh prepare; liquibase --log-level INFO --log-file=liquibase.log update

export LIQUIBASE_PASSWORD=${POSUSER_PASSWORD}

echo "Running migrate on ${DB_NAME}"
"${current_dir}"/setup_liquibase.sh migrate; liquibase  --log-level INFO  --log-file=liquibase.log update --contexts="test"

# currently there is no function/stored procedure/view to be run in here
#echo "Running replace on ${DB_NAME}"
#"${current_dir}"/setup_liquibase.sh replace; liquibase --log-level INFO --log-file=liquibase.log update --contexts="test"
