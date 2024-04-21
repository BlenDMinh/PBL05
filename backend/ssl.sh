#!/bin/sh

exec keytool -genkeypair \
            -keyalg   RSA            \
            -alias    pbl5         \
            -keystore ${CATALINA_HOME}/conf/selfsigned.jks \
            -validity 365            \
            -keysize  2048\
            -dname "CN=pbl5.com, OU=FCT, O=UNL, L=Unknown, ST=Unknown, C=PT" \
            -storepass changeit \
            -keypass changeit
exec keytool -export  \
            -alias     pbl5         \
            -file      ${CATALINA_HOME}/conf/selfsigned.crt \
            -keystore  ${CATALINA_HOME}/conf/selfsigned.jks \
            -storepass changeit
exec keytool -import -noprompt -trustcacerts \
            -alias     pbl5               \
            -file      ${CATALINA_HOME}/conf/selfsigned.crt       \
            -keystore  ${CATALINA_HOME}/conf/selfsigned.ts        \
            -storepass changeit

                                           