pipeline {
    agent any
    options {
        skipStagesAfterUnstable()
    }
    environment {
        accountId = "${sh(returnStdout: true, script: 'echo $AwsAccountId').trim()}"
        region = "${sh(returnStdout: true, script: 'echo $AwsRegion').trim()}"
        paramsFile = "${sh(returnStdout: true, script: 'echo $AwsCfParamsFile').trim()}"
    }
    stages {
        stage('Clone repository') {
            steps {
                checkout scm
            }
        }
        stage('Unit & Integration Tests') {
            steps {
                script {
                    try {
                        sh './gradlew clean test --no-daemon'
                    } finally {
                        junit '**/build/test-results/test/*.xml'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    app = docker.build("mfattah/github-lister")
                }
            }
        }

        stage('Upload Image') {
            steps {
                script {
                    docker.withRegistry("https://${accountId}.dkr.ecr.${region}.amazonaws.com", "ecr:${region}:jenkins-user") {
                        app.push("latest")
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    withCredentials([aws(credentialsId: 'jenkins-user')]) {
                        docker.image("xueshanf/awscli:latest").inside("--entrypoint \"\" -v \"${paramsFile}:/params.json\" -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e AWS_DEFAULT_REGION") {
                            sh "aws cloudformation deploy --region=${region} --template-file cf-stack.yaml --stack-name github-lister-stack --parameter-overrides file:///params.json"
                        }
                    }
                }
            }
        }
    }
}