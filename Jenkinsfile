pipeline {
  agent 'any'
   tools{
     jdk 'jdk 21'
 }
  stages {
    stage('Checkout') {
      steps {
        script {
            checkout([$class: 'GitSCM', branches: [[name: '*/maven']], userRemoteConfigs: [[url: 'https://github.com/Kirillkuss/library']]])
        }
      }
    }
    
    stage('clean') {
      steps {
        bat(script: 'mvn clean')
      }
    }
    
    stage('package') {
      steps {
        bat(script: 'mvn package')
      }
    }

    
  }
    post {
        always {
            allure includeProperties: false, jdk: '', properties: [[key: 'allure.results.directory', value: 'target/allure-results']], report: 'target/allure-report', results: [[path: 'target/allure-results'], commandline: '2.24.0']
            junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults : true, skipPublishingChecks: true)
            }
        }
}