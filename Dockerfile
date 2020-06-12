FROM maven:3-jdk-14 as BUILD

COPY . /usr/src/app
RUN mvn --batch-mode -DskipTests -f /usr/src/app/pom.xml clean package

FROM openjdk:14-jdk
EXPOSE 8080

RUN yum -y install git
RUN yum -y install wget
RUN wget https://releases.hashicorp.com/terraform/0.12.26/terraform_0.12.26_linux_386.zip
RUN yum -y install unzip
RUN unzip terraform_0.12.26_linux_386.zip
RUN cp -rf terraform /usr/bin

COPY --from=BUILD /usr/src/app/target/*.jar /opt/target/oneclick.jar

WORKDIR /opt/target

CMD ["java", "-jar", "oneclick.jar"]
