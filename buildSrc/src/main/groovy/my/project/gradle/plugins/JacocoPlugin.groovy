package my.project.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class JacocoPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply org.gradle.testing.jacoco.plugins.JacocoPlugin

        project.jacoco {
            toolVersion '0.8.10'
        }

        project.test {
            finalizedBy project.jacocoTestReport
        }

        def coverageDir = new File("${project.buildDir}/coverage")
        def coverageXml = new File(coverageDir, "coverageReport.xml")
        project.jacocoTestReport {
            dependsOn project.test
            reports {
                csv.required = false
                xml.required = true
                xml.destination = coverageXml
                html.destination = coverageDir

            }
            doLast {
                // print the metrics
                def xml = new XmlParser(false, false).parseText(coverageXml.text.replaceAll("<!DOCTYPE[^>]*>", ""))
                xml.counter.each {
                    def total = Integer.valueOf(it.@missed as String) + Integer.valueOf(it.@covered as String)
                    def covered = Integer.valueOf(it.@covered as String)

                    def pcnt = Math.floor(10000.0 * covered / total) / 100
                    println "  - " + it.attribute('type') + " " + it.attribute('covered') + "/" + total + " = " + pcnt  + "%"
                }
            }
        }

        project.check {
            dependsOn project.jacocoTestReport
            dependsOn project.jacocoTestCoverageVerification
        }
    }
}
