#!/bin/bash
set -e

mariadb --host=bunny-emu-database --user=root --password=mariadb bunny < "/docker-entrypoint-initdb.d/base_sqls/auth/auth.sql"
mariadb --host=bunny-emu-database --user=root --password=mariadb bunny < "/docker-entrypoint-initdb.d/base_sqls/characters/character.sql"