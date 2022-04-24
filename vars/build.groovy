def call(Map buildStageSwitches = [:]) {

    pipeline {

        agent any

        tools {
            jdk '11.0.15_8'
            maven 'Maven-3.8.5'
        }

        stages {
            stage('Build') {

                steps {
                    sh "mvn -Dmaven.test.failure.ignore=true clean package"
                }

                post {
                    success {
                        junit '**/target/surefire-reports/TEST-*.xml'
                        archiveArtifacts 'target/*.jar'
                    }
                }
            }
        }

        post {
            always {
                cleanWs()
            }
            failure {
                emailext attachLog: true, body: """
$JOB_NAME - $BUILD_NUMBER failed.
URL: $BUILD_URL
Please see attachment for console output.""",
                        recipientProviders: [culprits()], subject: 'Jenkins Build Failed: $JOB_NAME'
            }
        }
    }
}