#!/bin/bash
# startup.sh

mv "/opt/jboss/wildfly/standalone/$DEPLOYMENT" /opt/jboss/wildfly/standalone/deployments/


/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0