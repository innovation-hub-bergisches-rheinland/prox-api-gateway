pipeline {
    agent any

    tools {
        maven "apache-maven-3.6.1"
        jdk "oracle-jdk-8u212"
    }

    environment {
        REPOSITORY = "ptb-gp-ss2019.archi-lab.io"
        IMAGE = "api-gateway"
        SERVERNAME = "fsygs15.inf.fh-koeln.de"
        SERVERPORT = "22413"
        SSHUSER = "jenkins"
        YMLFILENAME = "docker-compose-api-gateway.yml"
    }

    stages {
        stage("Build") {
            steps {
                sh "mvn clean package"
                sh "docker image save -o ${IMAGE}.tar ${REPOSITORY}/${IMAGE}"
            }
        }

        stage("Deploy") {
            steps {
                sh "scp -P ${SERVERPORT} -v ${IMAGE}.tar ${SSHUSER}@${SERVERNAME}:~/"
                sh "scp -P ${SERVERPORT} -v ${YMLFILENAME} ${SSHUSER}@${SERVERNAME}:/srv/projektboerse/"
                sh "ssh -p ${SERVERPORT} ${SSHUSER}@${SERVERNAME} " +
                    "'docker image load -i ${IMAGE}.tar; " +
                    "docker network inspect ptb &> /dev/null || docker network create ptb; " +
                    "docker-compose -p ptb -f /srv/projektboerse/${YMLFILENAME} up -d'"
            }
        }
    }
}
