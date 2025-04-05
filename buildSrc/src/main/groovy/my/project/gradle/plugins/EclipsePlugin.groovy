package my.project.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.model.ProjectDependency

class EclipsePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply org.gradle.plugins.ide.eclipse.EclipsePlugin

        project.eclipse.classpath {
            downloadSources = true
            downloadJavadoc = true

            // Needed because of https://github.com/eclipse/buildship/issues/1206
            // Hopefully this will one day be made unnecessary (but at the moment is not looking likely)
            file.whenMerged { classpath ->
                classpath.entries.findAll { entry -> entry instanceof ProjectDependency && entry.entryAttributes.test }
                    .each { it.entryAttributes['test'] = 'false' }
            }
        }

        project.eclipse.project {
            if (project.plugins.hasPlugin org.gradle.api.plugins.quality.CheckstylePlugin) {
                buildCommand 'net.sf.eclipsecs.core.CheckstyleBuilder'
                natures 'net.sf.eclipsecs.core.CheckstyleNature'
            }

            if (project.plugins.hasPlugin com.github.spotbugs.snom.SpotBugsPlugin) {
                buildCommand 'edu.umd.cs.findbugs.plugin.eclipse.findbugsBuilder'
                natures 'edu.umd.cs.findbugs.plugin.eclipse.findbugsNature'
            }
        }
    }
}
