marketmap
=========

How to build the application?

1) Checkout the project using git clone git@github.com:dushkindigital/marketmap.git

2) Run the mvn clean install on the root project. This will run test cases on hsqldb so no configuration is required.

3) While building the application you might get errors if you don't have ebay jars. There are four ebay jars that you need to manually install in your local repository.

4) Go inside web project and run mvn jetty:run to run the web application

Running on Cloud Foundry
=========================

1) You have to increase perm gen space using following command

	vmc env-add libereco JAVA_OPTS=-XX:MaxPermSize=512m

2) While running mvn jetty:run you might get java.lang.OutOfMemoryError: PermGen space 

	export MAVEN_OPTS="-Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"