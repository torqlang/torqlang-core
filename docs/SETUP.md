# Setup Guide

## Install Java 17 or later

https://jdk.java.net/
https://docs.aws.amazon.com/corretto/

## Install Maven

https://maven.apache.org/install.html

### or

Download and install the latest Maven build

~~~
sudo mkdir -p /usr/local/apache-maven
sudo chmod 777 /usr/local/apache-maven
mkdir /usr/local/apache-maven/apache-maven-3.9.4
tar -xvf apache-maven-3.9.4-bin.tar.gz -C /usr/local/apache-maven
~~~

Add the following lines to `~/.bashrc`

~~~
# MAVEN
export M2_HOME=/usr/local/apache-maven/apache-maven-3.9.4
export M2=$M2_HOME/bin
export PATH=$M2:$PATH
~~~

Reload the environment without restarting

~~~
source ~/.bashrc
~~~

Verify that Maven is running

~~~
mvn -version
~~~

You should see something similar to the following

~~~
Maven home: /usr/local/apache-maven/apache-maven-3.9.4
Java version: 17.0.8.1, vendor: Private Build, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.2.0-32-generic", arch: "amd64", family: "unix"
~~~

Install Maven Wrapper

~~~
mvn wrapper:wrapper
~~~

Configure your IDE to use Maven Wrapper