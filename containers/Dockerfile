# syntax=docker/dockerfile:1
FROM openjdk:17-oracle
WORKDIR /usr/share
COPY --chown=daemon:daemon build/distributions/kolloid.tar .
RUN tar -xf kolloid.tar && rm kolloid.tar
USER daemon:daemon
CMD ["./kolloid/bin/server"]
