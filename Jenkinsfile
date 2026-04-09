pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-17'
    }

    environment {
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
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: '*/target/*.jar'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -f auth-service/Dockerfile      -t auth-service:${IMAGE_TAG}      ."
                sh "docker build -f dashboard-service/Dockerfile -t dashboard-service:${IMAGE_TAG} ."
                sh "docker build -f stock-service/Dockerfile     -t stock-service:${IMAGE_TAG}     ."
            }
        }

        stage('Upload JARs to GitHub Release') {
            steps {
                script {
                    sh """
                        curl -X POST \
                          -H "Authorization: token ${GITHUB_TOKEN}" \
                          -H "Content-Type: application/json" \
                          https://api.github.com/repos/${GITHUB_REPO}/releases \
                          -d '{
                            "tag_name": "build-${IMAGE_TAG}",
                            "name": "Build ${IMAGE_TAG}",
                            "body": "Jenkins build #${env.BUILD_NUMBER}"
                          }' > release.json

                        UPLOAD_URL=\$(cat release.json \
                          | grep upload_url \
                          | cut -d'"' -f4 \
                          | cut -d'{' -f1)

                        for SERVICE in auth-service dashboard-service stock-service; do
                            JAR=\$(ls \${SERVICE}/target/*.jar | head -1)
                            curl -X POST \
                              -H "Authorization: token ${GITHUB_TOKEN}" \
                              -H "Content-Type: application/java-archive" \
                              --data-binary @\${JAR} \
                              "\${UPLOAD_URL}?name=\${SERVICE}-${IMAGE_TAG}.jar"
                        done

                        echo "✅ JARs uploaded to GitHub Releases"
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