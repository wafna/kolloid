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

APP_NAME := kolloid
SERVER := ${APP_NAME}_server_1
DATABASE := ${APP_NAME}_db_dev
NETWORK_NAME := ${APP_NAME}_network

images: FORCE
	@docker images

network: FORCE
	@docker network inspect ${NETWORK_NAME}

prune: FORCE
	@docker system prune -a

ps: FORCE
	@docker ps

dev.rm: FORCE
	@docker container rm -f ${DATABASE}

dev.start: FORCE
	@cd containers/dev && docker-compose up --detach

dev.stop: FORCE
	@docker stop ${DATABASE} && docker rm ${DATABASE}

dev.psql: FORCE
	@psql -h localhost -p 5432 -d kolloid -U kolloid_user

db.psql: FORCE
	@docker exec -it ${DATABASE} psql -d kolloid -U kolloid_user

db.logs: FORCE
	@docker logs -f ${DATABASE}
