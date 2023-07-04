# syntax=docker/dockerfile:1
FROM openjdk:17-oracle
WORKDIR /usr/share
COPY --chown=daemon:daemon build/distributions/kjs.tar .
RUN tar -xf kjs.tar && rm kjs.tar
USER daemon:daemon
CMD ["./kwap/bin/server"]
