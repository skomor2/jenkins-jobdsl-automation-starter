job('greeting-demo-app-1') {
    description 'Build and test the app'
    triggers {
        scm('H/2 * * * *')
    }
    wrappers {
        timestamps()
        colorizeOutput()
    }
    publishers {
        chucknorris()
        checkstyle('**/checkstyle-result.xml') {
            healthLimits(3, 20)
            thresholdLimit('high')
            defaultEncoding('UTF-8')
            canRunOnFailed(true)
            useStableBuildAsReference(true)
            useDeltaValues(true)
            computeNew(true)
            shouldDetectModules(true)
            thresholds(
                    unstableTotal: [all: 1, high: 2, normal: 3, low: 4],
                    failedTotal: [all: 5, high: 6, normal: 7, low: 8],
                    unstableNew: [all: 9, high: 10, normal: 11, low: 12],
                    failedNew: [all: 13, high: 14, normal: 15, low: 16]
            )

        }

        jacocoCodeCoverage {
            execPattern('build/jacoco')
            exclusionPattern('*Spec.class')
        }
        publishHtml {
            report('build/reports/tests/test') {
                reportName('Report')
            }
        }
        postBuildTask {
            downstreamParameterized {
                trigger('downstream1') {
                    parameters {
                        predefinedProp('COMMIT_ID', '$COMMIT_ID')
                    }
                }
            }
        }
    }
    scm {
        git '/home/jenkins/greeting-demo-app'
    }
    steps {

        shell 'date > /home/jenkins/greeting-demo-app/data'
        shell 'cd /home/jenkins/greeting-demo-app/data && git add data'
        shell 'cd /home/jenkins/greeting-demo-app/data && git commit -m data'
        gradle('build')

    }



}
job('downstream1') {
    parameters {
        stringParam('COMMIT_ID')

    }

    steps {
        shell "echo $COMMIT_ID"
    }

}