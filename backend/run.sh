#!/bin/sh

keytool -delete -alias tomcat -keystore ${CATALINA_HOME}/conf/selfsigned.jks -storepass changeit

keytool -genkeypair \
            -keyalg   RSA            \
            -alias    tomcat         \
            -keystore ${CATALINA_HOME}/conf/selfsigned.jks \
            -validity 365            \
            -keysize  2048\
            -dname "CN=pbl5, OU=FCT, O=UNL, L=Unknown, ST=Unknown, C=PT" \
            -storepass changeit \
            -keypass changeit
keytool -export  \
            -alias     tomcat         \
            -file      ${CATALINA_HOME}/conf/selfsigned.crt \
            -keystore  ${CATALINA_HOME}/conf/selfsigned.jks \
            -storepass changeit
keytool -import -noprompt -trustcacerts \
            -alias     tomcat               \
            -file      ${CATALINA_HOME}/conf/selfsigned.crt       \
            -keystore  ${CATALINA_HOME}/conf/selfsigned.ts        \
            -storepass changeit

exec ${CATALINA_HOME}/bin/catalina.sh jpda run
