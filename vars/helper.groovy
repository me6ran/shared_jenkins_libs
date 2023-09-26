def get_property(String prop_file){
    echo "reading file $prop_file"
    prop = readProperties file:prop_file
    echo "returning prop.TEST_1: $prop.TEST_1"
    String ret = "NOTHING"
    if (prop?.TEST_1){
        ret = "$prop.TEST_1"
    }

    return ret
}

def check_env() {
    sh label: "check env", script: "env"
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

def RunTestGitCheckout(){
    def triggeredJob = build job: 'test_git_checkout',
    parameters: [booleanParam(name: 'myBooleanParameter', value: true)]
    echo "triggeredJob: $triggeredJob"
    echo "Triggered build number: ${triggeredJob.number}"
    echo "Triggered build result: ${triggeredJob.result}"
    echo "Triggered build fullDisplayName: ${triggeredJob.fullDisplayName}"
    echo "Triggered build duration: ${triggeredJob.duration}"
}

return this
