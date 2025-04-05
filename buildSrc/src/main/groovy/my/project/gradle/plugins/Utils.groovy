package my.project.gradle.plugins

import org.gradle.api.Project

class Utils {

    static File findPluginDir(Project project) {
        return new File(project.getRootDir(), 'buildSrc/gradle')
    }

    static File findConfDir(Project project) {
        return new File(project.getRootDir(), 'conf')
    }
}
