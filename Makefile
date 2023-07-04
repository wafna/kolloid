FORCE: ;

test: FORCE
	@./gradlew test

server: FORCE
	@./gradlew :server:run

browser: FORCE
	@./gradlew :browser:browserDevelopmentRun --continuous

build: FORCE
	@./gradlew formatKotlin clean build --warning-mode all

spiff: FORCE
	@./gradlew formatKotlin lintKotlin detekt

yarn: FORCE
	@./gradlew kotlinUpgradeYarnLock

# Docker commands.

APP_NAME := kjs
SERVER := ${APP_NAME}_server_1
DATABASE := ${APP_NAME}_database_1
NETWORK_NAME := ${APP_NAME}_default

images: FORCE
	@docker images

network: FORCE
	@docker network inspect ${NETWORK_NAME}

prune: FORCE
	@docker system prune -a

ps: FORCE
	@docker ps

rm: FORCE
	@docker container rm -f ${DATABASE} ${SERVER}

up: FORCE
	@cd deploy && docker-compose up --build --detach

stop: FORCE
	@docker stop ${DATABASE} ${SERVER}

app_sh: FORCE
	@docker exec -it ${SERVER} /bin/bash

db_sh: FORCE
	@docker exec -it ${DABASE} /bin/sh

app_logs: FORCE
	@docker logs -f ${SERVER}

db_logs: FORCE
	@docker logs -f ${DATABASE}
