pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-17'
    }

    environment {
        APP_NAME  = 'code-challenge-be'
        IMAGE_TAG = "${env.GIT_COMMIT[0..7]}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Branch: ${env.BRANCH_NAME}"
                checkout scm
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
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
                echo 'Building JAR...'
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${APP_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Done') {
            steps {
                echo "✅ Image built: ${APP_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        success { echo '✅ Pipeline passed!' }
        failure { echo '❌ Pipeline failed!' }
        always  { cleanWs() }
    }
}