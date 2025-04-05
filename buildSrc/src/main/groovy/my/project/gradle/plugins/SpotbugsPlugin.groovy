package my.project.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpotbugsPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply com.github.spotbugs.snom.SpotBugsPlugin

        def confDir = Utils.findConfDir(project)
        project.spotbugs {
            excludeFilter = new File(confDir, "spotbugs/exclude.xml")
        }
        project.spotbugsMain {
            reports {
                html {
                    enabled = true
                }
            }
        }
        project.spotbugsTest {
            enabled = false
        }
    }
}
