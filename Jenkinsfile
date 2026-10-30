pipeline {
    agent any

    environment {
        IMAGE_NAME = 'kodercrish/calculator'
        IMAGE_TAG  = "${BUILD_NUMBER}"
    }

    triggers {
        // Check GitHub every minute and build only if there are new commits
        pollSCM('* * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    // Publish JUnit results (build fails if any test failed)
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG -t $IMAGE_NAME:latest .'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DOCKER_USER',
                                                  passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $IMAGE_NAME:$IMAGE_TAG
                        docker push $IMAGE_NAME:latest
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                // IMAGE_TAG from the environment block tells Compose which image to run
                sh 'docker compose up -d'

                // Wait until the app answers through Nginx (max ~2.5 minutes)
                sh '''
                    for i in $(seq 1 30); do
                        if curl -fs http://localhost/actuator/health | grep -q UP; then
                            echo "Application is healthy"
                            exit 0
                        fi
                        echo "Waiting for application... ($i/30)"
                        sleep 5
                    done
                    echo "Application did not become healthy"
                    exit 1
                '''
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
    }
}
