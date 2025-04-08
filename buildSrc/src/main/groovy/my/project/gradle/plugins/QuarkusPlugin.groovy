package my.project.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.Task
import org.gradle.api.tasks.TaskInputs
import org.gradle.api.tasks.TaskOutputs
import org.gradle.api.tasks.TaskAction

class QuarkusPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply io.quarkus.gradle.QuarkusPlugin
        project.plugins.apply my.project.gradle.plugins.JacocoPlugin

        project.compileJava.dependsOn project.compileQuarkusGeneratedSourcesJava
        project.compileTestJava.dependsOn project.compileQuarkusTestGeneratedSourcesJava
        project.sourcesJar.dependsOn project.compileQuarkusGeneratedSourcesJava

        project.test {
            finalizedBy project.jacocoTestReport
            jacoco {
                excludeClassLoaders = ["*QuarkusClassLoader"] 
                destinationFile = project.layout.buildDirectory.file("jacoco-quarkus.exec").get().asFile 
            }
            project.jacocoTestReport.enabled = false 
        }

        project.jacocoTestCoverageVerification.executionData.from "$project.buildDir/jacoco-quarkus.exec"
    }
}
