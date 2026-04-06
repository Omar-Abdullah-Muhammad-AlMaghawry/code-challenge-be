pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-17'
    }

    environment {
        APP_NAME     = 'my-spring-app'
        IMAGE_TAG    = "${env.GIT_COMMIT[0..7]}"
        GITHUB_TOKEN = credentials('github-token')
        GITHUB_REPO  = 'Omar-Abdullah-Muhammad-AlMaghawry/code-challenge-be'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${APP_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Upload JAR to GitHub Release') {
            steps {
                script {
                    def jarFile = sh(
                        script: 'ls target/*.jar | head -1',
                        returnStdout: true
                    ).trim()

                    sh """
                        # Create release
                        curl -X POST \
                          -H "Authorization: token ${GITHUB_TOKEN}" \
                          -H "Content-Type: application/json" \
                          https://api.github.com/repos/${GITHUB_REPO}/releases \
                          -d '{
                            "tag_name": "build-${IMAGE_TAG}",
                            "name": "Build ${IMAGE_TAG}",
                            "body": "Jenkins build #${env.BUILD_NUMBER}"
                          }' > release.json

                        # Extract upload URL
                        UPLOAD_URL=\$(cat release.json \
                          | grep upload_url \
                          | cut -d'"' -f4 \
                          | cut -d'{' -f1)

                        # Upload JAR
                        curl -X POST \
                          -H "Authorization: token ${GITHUB_TOKEN}" \
                          -H "Content-Type: application/java-archive" \
                          --data-binary @${jarFile} \
                          "\${UPLOAD_URL}?name=${APP_NAME}-${IMAGE_TAG}.jar"

                        echo "✅ JAR uploaded to GitHub Releases"
                    """
                }
            }
        }
    }

    post {
        success { echo '✅ Pipeline passed!' }
        failure { echo '❌ Pipeline failed!' }
        always  { cleanWs() }
    }
}