FROM jboss/wildfly:19.0.0.Final

USER root
ADD standalone.xml ${JBOSS_HOME}/standalone/configuration/
RUN mkdir -p /home/test/
ADD deployments/startup.sh /home/
RUN chmod +x /home/startup.sh
WORKDIR ${JBOSS_HOME}/modules/system/layers/base/com/mysql/main

EXPOSE 8080
EXPOSE 9990

USER jboss

ADD deployments/MSRoadNetwork.war /opt/jboss/wildfly/standalone/

ENV MULTICONT no

WORKDIR /opt/jboss/wildfly/standalone/

RUN /opt/jboss/wildfly/bin/add-user.sh unisannio unisannio --silent

ENTRYPOINT ["/home/startup.sh"]