pipeline {
    agent any

    tools {
        maven "apache-maven-3.6.1"
        jdk "oracle-jdk-8u212"
    }

    environment {
//        REPOSITORY = "docker.nexus.archi-lab.io/archilab"
//        IMAGE = "prox-api-gateway"
        SERVERNAME = "fsygs15.inf.fh-koeln.de"
        SERVERPORT = "22413"
        SSHUSER = "jenkins"
        YMLFILENAME = "docker-compose-api-gateway.yml"
    }

    stages {
        stage("Build") {
            steps {
                sh "mvn clean deploy -Drevision=dev-${BUILD_NUMBER}"
//                sh "docker image save -o ${IMAGE}.tar ${REPOSITORY}/${IMAGE}"
            }
        }

        stage("Deploy") {
            steps {
//                sh "scp -P ${SERVERPORT} -v ${IMAGE}.tar ${SSHUSER}@${SERVERNAME}:~/"
                sh "scp -P ${SERVERPORT} -v ${YMLFILENAME} ${SSHUSER}@${SERVERNAME}:/srv/prox/"
                sh "ssh -p ${SERVERPORT} ${SSHUSER}@${SERVERNAME} " +
//                        "'docker image load -i ${IMAGE}.tar; " +
                        "-o SendEnv=POM_ARTIFACTID -o SendEnv=POM_VERSION " +
                        "'export POM_ARTIFACTID=${POM_ARTIFACTID}; export POM_VERSION=${POM_VERSION} " +
                        "docker network inspect prox &> /dev/null || docker network create prox " +
                        "docker-compose -p prox -f /srv/prox/${YMLFILENAME} up -d'"
            }
        }
    }
}
