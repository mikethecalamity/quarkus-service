package my.project.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class BaseProjectPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply JavaPlugin
        project.plugins.apply EclipsePlugin
        project.plugins.apply JacocoPlugin
        project.plugins.apply CheckstylePlugin
        project.plugins.apply SpotbugsPlugin
    }
}
