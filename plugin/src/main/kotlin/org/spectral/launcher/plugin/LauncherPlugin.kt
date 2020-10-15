/*
 * Copyright (C) 2020 Kyle Escobar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spectral.launcher.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskContainer
import org.spectral.launcher.plugin.task.GenerateManifestTask
import java.net.URI

/**
 * The Spectral Launcher Gradle Plugin
 */
open class LauncherPlugin : Plugin<Project> {

    /**
     * The targeted gradle project using this plugin.
     */
    private lateinit var project: Project

    /**
     * Apply the plugin.
     *
     * @param target Project
     */
    override fun apply(target: Project) {
        this.project = target

        /*
         * Force add the public Spectral Powered Maven repository
         * to the current project.
         */
        addGradleRepo(project, "Spectral Powered", "https://repo.spectralpowered.org/")

        /*
         * Force add the spectral launcher api as a gradle project dependency
         */
        addGradleDependency(project, "implementation", "org.spectral.launcher", "launcher", project.version.toString())

        /*
         * Register plugin gradle tasks.
         */
        val tasks: TaskContainer = project.tasks

        tasks.register("runLauncher", JavaExec::class.java) {
            it.description = "Runs the Spectral Client launcher."
        }

        tasks.register("generateManifest", GenerateManifestTask::class.java)
    }

    /**
     * Adds a maven repository to a project.
     *
     * @param target Project
     * @param name String
     * @param url String
     * @return MavenArtifactRepository
     */
    private fun addGradleRepo(target: Project, name: String, url: String): MavenArtifactRepository {
        return target.repositories.maven {
            it.name = name
            it.url = URI(url)
        }
    }

    /**
     * Adds a gradle dependency to a target project.
     *
     * @param target Project
     * @param configuration String
     * @param group String
     * @param id String
     * @param version String
     */
    private fun addGradleDependency(target: Project, configuration: String, group: String, id: String, version: String) {
        target.dependencies.add(configuration, "$group:$id:$version")
    }
}