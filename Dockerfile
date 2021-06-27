FROM tootukassa-docker.artifactory.nortal.com/rhel7:java11
COPY build/kv-*-runner.jar /opt/app/kv.jar
ENTRYPOINT ["java", \
            "-jar", \
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
            "/opt/app/kv.jar"]
