pipeline {
    environment {
        FULL_PATH_BRANCH = "${env.BRANCH_NAME}"
        RELEASE_BRANCH = FULL_PATH_BRANCH.substring(FULL_PATH_BRANCH.lastIndexOf('/') + 1, FULL_PATH_BRANCH.length()).trim()
    }

    tools{
        maven 'maven3'
        jdk 'OpenJDK17'
    }

    agent any

    stages {
        stage('initialization') {
            steps {
                sh 'java -version'
                sh 'mvn --version'
                sh 'echo HOME=${HOME}'
                sh 'echo PATH=${PATH}'
                sh 'echo M2_HOME=${M2_HOME}'
                echo "build=${env.BUILD_NUMBER}"
                echo "APP_VERSION=${env.APP_VERSION}"
            }
        }

        stage('build') {
            when {
                branch 'qa'
            }
            steps {
                 sh 'mvn clean package versions:set -DautoVersionSubmodules=true'
                 sh 'mvn deploy -Dmaven.test.skip=true'
            }
        }

        stage('build release') {
            when {
                expression { BRANCH_NAME ==~ /release\/[0-9]+\.[0-9]+/ }
            }
            steps {
                script {
                    def buildVersion = "${RELEASE_BRANCH}.${BUILD_NUMBER}"
                    echo "buildVersion=${buildVersion}"

                    sh "mvn -B -DskipTests clean versions:set -DnewVersion=${buildVersion} -f pom.xml"
                    sh "mvn deploy"
                }
            }
        }
    }

    post {
        always {
            nssSendJobResult(recipients: "AndewilEventsChannel")
        }
    }

}