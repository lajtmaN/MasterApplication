pipeline {
	agent any

	options { 
		timestamps() 
	}

	stages {
		/*stage('Static Code') {
			steps {
				bat 'C:/tools/cloc.exe --by-file --xml --out=Visualizer/build/reports/cloc/cloc.xml Visualizer/src'
			}
		}*/
		stage('Compile') {
			steps {
				bat 'gradle assemble --build-file Visualizer/build.gradle'
			}
		}
		stage('Test') {
			steps {
				bat 'gradle check --build-file Visualizer/build.gradle'
			}
		}
		stage('Statistics') {
			steps {
				junit '**/build/test-results/test/*.xml'
			}
			/*steps {
				CLOCCount is not compatible with jenkinsfile yet...
			}*/
		}
		stage('Cleaning') {
			steps {
				bat 'gradle clean --build-file Visualizer/build.gradle'
			}
		}
	}
	post {
		success {
			slackSend channel: "#code", color: "good", message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
		}
		failure {
			slackSend channel: "#code", color: "danger", message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
		}
	}
}