marketmap
=========

How to run the application?

1) Checkout the project

2) Change the database.properties to match your mysql credentials.

3) Do mvn clean install on the root project.

4) Go inside web project and run mvn jetty:run to run the web application

Running on Cloud Foundry

You have to increase perm gen space using following command
vmc env-add libereco JAVA_OPTS=-XX:MaxPermSize=512m

