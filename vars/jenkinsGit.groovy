def call() {

    pipeline {
        agent { 
            dockerfile {
                filename "Dockerfile"
                args '--privileged --network=host'
            } 
            }
        parameters {
            booleanParam name: 'EnableBuild', defaultValue: false, description: 'would enable build process before running test'
        }
        stages {
            stage('Build') {
                environment {
                    venv_name = "test_venv"
                    PYTHONPATH="$WORKSPACE"
                }
                steps {
                    sh """
                    env | sort
                    uname -a
                    git --version
                    python3 -m venv venv; source ./venv/bin/activate; pip install -r requirements.txt
                    python -u -m pytest -s -v test/test_basic_stuff.py

                    // """
                    // sh 'source ./my_env'
                    // sh 'env'

                }
            }
            
            stage('Test') {
                steps {
                    script {
                        J_FILENAME = "new_output.json"
                        test1 = helper.get_property("my_prop.properties")
                        test_info = readProperties file: "my_file.txt"
                        sh label: "show my_file.txt", script: "cat my_file.txt"
                        echo "test_info.area is $test_info.area"
                        echo "test_info.integer is $test_info.integer"
                        echo "test_info.zone is $test_info.zone"
                        echo "test_info.notvalid is $test_info.notvalid"
                        def bVal = test_info.integer.toBoolean()
                        echo "test_info.integer.toBoolean() is $bVal"
                        if(!test_info.integer.toBoolean()){
                            echo "we found it is false"
                        }
                        else {
                            echo "we are not able to examine the 'false'"
                        }
                        sh label: "run python file", script: "source ./venv/bin/activate;python3 my_python.py -mos_tasks_filename $J_FILENAME"
                        def json_content = readFile(file: J_FILENAME)
                        helper.archiveSingleFile(file_path=J_FILENAME)
                        echo "json content is $json_content"
                        echo "check env"
                        helper.check_env()
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
                    helper.check_env()

                    }

                }
            }
            stage('Build_Downstream'){
                steps {
                    script {
                        helper.RunTestGitCheckout()
                    }
                }
            } // end of downstream run stage
        } // end of stage

    } // end of pipeline

}

