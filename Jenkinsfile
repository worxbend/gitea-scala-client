pipeline {
  agent any

  options {
    buildDiscarder(
      logRotator(
        artifactDaysToKeepStr: '7',
        artifactNumToKeepStr: '10',
        daysToKeepStr: '14',
        numToKeepStr: '20'
      )
    )
    timestamps()
  }

  environment {
    GITEA_URL = ''
    GITEA_TOKEN = ''
  }

  stages {
    stage('Compile') {
      steps {
        sh './mill __.compile'
      }
    }

    stage('Unit Tests') {
      steps {
        sh './mill __.test'
      }
    }

    stage('Integration Tests') {
      steps {
        sh './mill it.test'
      }
    }

    stage('Examples') {
      steps {
        sh './mill examples.run'
      }
    }

    stage('Compatibility Check') {
      steps {
        sh './mill compatibility.check'
      }
    }

    stage('Publishable Artifacts') {
      steps {
        sh './mill __.docJar __.sourceJar __.publishArtifacts'
      }
    }
  }
}
