package my.project.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class CheckstylePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply org.gradle.api.plugins.quality.CheckstylePlugin

        def confDir = Utils.findConfDir(project)
        def config = new File(confDir, "checkstyle/checkstyle.xml")
        project.checkstyle {
            toolVersion = '10.18.2'
            configDirectory = config.getParentFile()
            configFile = config
            configProperties = [samedir: config.getParent()]
            maxWarnings = 0
        }
    }
}
