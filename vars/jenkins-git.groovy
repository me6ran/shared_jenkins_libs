def call() {

    pipeline {
        agent any
        stages {
            stage('Build') {
                environment {
                    venv_name = "test_venv"
                }
                steps {
                    sh """
                    python3 -m venv venv; source ./venv/bin/activate; pip install -r requirements.txt
                    python -u -m pytest -s -v test/test_basic_stuff.py

                    """
                    sh 'source ./my_env'
                    sh 'env'

                }
            }
            
            stage('Test') {
                steps {
                    script {
                        J_FILENAME = "new_output.json"
                        def prop_readr = load "helper.groovy"
                        test1 = prop_readr.get_property("my_prop.properties")
                        test_info = readProperties file: "my_file.txt"
                        echo "test_info.area is $test_info.area"
                        echo "test_info.integer is $test_info.integer"
                        echo "test_info.zone is $test_info.zone"
                        echo "test_info.notvalid is $test_info.notvalid"
                        if(!test_info.integer.toBoolean()){
                            echo "we found it is false"
                        }
                        else {
                            echo "we are not able to examine the 'false'"
                        }
                        sh label: "run python file", script: "python3 my_python.py -mos_tasks_filename $J_FILENAME"
                        def json_content = readFile(file: J_FILENAME)
                        archiveSingleFile(file_path=J_FILENAME)
                        echo "json content is $json_content"
                        echo "check env"
                        prop_readr.check_env()
                        // def jsonContent = readJSON text: jsonFile
                    }
                    echo "got the propertie info: $test1"
                    sh 'echo "Testing Hello World"'
                    sh label: "get env", script: "env"
                    sh '''
                        echo "Multiline shell steps works too"
                        ls -lah
                    '''
                }
            }
            
            stage('Deploy') {
                steps {
                    sh 'echo "Deploying Hello World"'
                    script {
                    echo "check env"
                    prop_readr.check_env()

                    }

                }
            }
            
        }
    }

    def archiveSingleFile(String file_path){
        sh label: "check current path", script: "pwd;"
        echo "Archiving $file_path"
        if (!fileExists(file_path)) {
            echo "ERROR: try to archive: $file_path but it does not exist."
            return
        }
        archiveArtifacts "$file_path"

    }
}