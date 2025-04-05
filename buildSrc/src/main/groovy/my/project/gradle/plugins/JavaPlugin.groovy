package my.project.gradle.plugins

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc

class JavaPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply org.gradle.api.plugins.JavaPlugin

        project.java {
            sourceCompatibility = JavaVersion.VERSION_17
            withJavadocJar()
            withSourcesJar()
        }

        project.compileJava {
            options.encoding = 'UTF-8'
            options.compilerArgs << '-parameters'
        }

        project.compileTestJava {
            options.encoding = 'UTF-8'
        }

        project.test {
            useJUnitPlatform()
        }

        // Force javadoc compilation on builds
        project.build {
            dependsOn project.javadoc
            if (project.tasks.findByName('testJavadoc')) {
                dependsOn project.testJavadoc
            }
        }

        // TODO one day
        project.tasks.withType(Javadoc).all {
            enabled = false
        }
    }
}
