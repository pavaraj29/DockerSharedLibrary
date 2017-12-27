def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        node {
            // Clean workspace before doing anything
            deleteDir()

            try {
                stage ('Clone') {
                    sh "sudo rm -rf node-js-sample"    
                    checkout scm
                }
                stage ('Docker image build') {
                    sh "sudo docker build -t ${config.dockerImageName} ."
                }
                stage ('Docker image tag') {
                    sh "sudo  docker tag ${config.dockerImageName} ${config.image}"
                }    
            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }
        }
    }
