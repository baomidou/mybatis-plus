package com.baomidou.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.Conf2ScopeMappingContainer
import org.gradle.api.artifacts.maven.MavenPom
import org.gradle.api.artifacts.maven.PomFilterContainer
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.Upload

class MavenDeployPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(MavenPlugin)
        Conf2ScopeMappingContainer scopeMappings = project.conf2ScopeMappings
        scopeMappings.addMapping(MavenPlugin.COMPILE_PRIORITY + 1,
            project.configurations.getByName("provided"), Conf2ScopeMappingContainer.PROVIDED)
        // Add a temporary new optional scope
        scopeMappings.addMapping(org.gradle.api.plugins.MavenPlugin.COMPILE_PRIORITY + 2,
            project.configurations.getByName("optional"), "optional")
        // Add a hook to replace the optional scope
        project.afterEvaluate {
            project.tasks.withType(Upload).each { applyToUploadTask(project, it) }
        }
    }

    private void applyToUploadTask(Project project, Upload upload) {
        upload.repositories.withType(PomFilterContainer).each{ applyToPom(project, it) }
    }

    private void applyToPom(Project project, PomFilterContainer pomContainer) {
        pomContainer.pom.whenConfigured { MavenPom pom ->
            pom.dependencies.findAll{ it.scope == "optional" }.each {
                it.scope = "compile"
                it.optional = true
            }
        }
    }

}
