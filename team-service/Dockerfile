# Use Amazon Corretto 17 as requested
FROM amazoncorretto:17 as builder

# Minimal, portable creation of an unprivileged user "app" w/o depending on groupadd/useradd.
# We create home and app folders and append minimal passwd/group entries (UID/GID 1000).
# This avoids failing on base images that lack the shadow/util-linux tools.
RUN set -eux; \
    mkdir -p /app /home/app; \
    # create a group entry for app (GID 1000) if it doesn't exist
    if ! grep -q '^app:' /etc/group 2>/dev/null; then echo 'app:x:1000:' >> /etc/group; fi; \
    # create a passwd entry for app (UID 1000) if it doesn't exist
    if ! grep -q '^app:' /etc/passwd 2>/dev/null; then echo 'app:x:1000:1000:App User:/home/app:/sbin/nologin' >> /etc/passwd; fi; \
    # set ownership and permissions
    chown -R 1000:1000 /home/app /app; \
    chmod 700 /home/app; chmod 755 /app

WORKDIR /app

# copy pre-built jar (wildcard so you don't need to pass build-arg)
COPY target/*.jar /app/app.jar

# verify jar exists and tighten permissions; create log dir
RUN if [ -f /app/app.jar ]; then \
      chown 1000:1000 /app/app.jar && chmod 750 /app/app.jar ; \
    else \
      echo "ERROR: No JAR found in target/*.jar. Run 'mvn clean package' before 'docker build'." >&2 && exit 1; \
    fi && \
    mkdir -p /app/log && chown 1000:1000 /app/log && chmod 700 /app/log

# Run as the numeric unprivileged user (UID 1000). Using numeric avoids depending on availability of user name mapping tools.
USER 1000:1000

# Expose the port used by your app (adjust if your app listens elsewhere)
EXPOSE 8080

# Default JVM options (overwrite via docker run -e JAVA_OPTS=...)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Container-aware JVM flags and run the jar
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/app.jar"]
