def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        node {
            load ".envvars/env-vars.groovy"    
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
                        sh "echo ${env.imageVersion}"
                        sh "sudo  docker tag ${config.dockerImageName} ${config.image}:${env.imageVersion}"
                }  
                stage ('Docker image push') {
                        //sh "echo ${imageVersion}"
                        sh "sudo docker login -u pavanraj29 -p Pavan@123"
                        sh "sudo docker push ${config.image}:${env.imageVersion}"
                }      
            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }
        }
    }
