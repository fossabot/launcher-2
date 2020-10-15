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

package org.spectral.launcher.plugin.task

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.spectral.launcher.manifest.AppFile
import org.spectral.launcher.manifest.AppManifest
import java.io.File
import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import javax.xml.bind.JAXB

/**
 * Generates an application launcher manifest for the current
 * application project.
 */
abstract class GenerateManifestTask : LauncherTask() {

    init {
        this.description = "Generate an application manifest file and dependency structure folder."
    }

    /**
     * The remote uri of the spectral archive server.
     */
    @get:Input
    @get:Option(option = "uri", description = "The remote URI of the archive server.")
    abstract var uri: String

    /**
     * The folder to output all the dependency jars and manifest file to.
     */
    @get:Input
    @get:Option(option = "outputDir", description = "The folder to output dependency jars and manifest file to.")
    abstract var outputDir: String

    /**
     * The version of this manifest file. This will tell the client
     * what version it's launching.
     */
    @get:Input
    @get:Option(option = "version", description = "The version string of the application being launched.")
    abstract var version: String

    /**
     * The internal classpath format to the launcher class which
     * extends the AbstractLauncher class.
     */
    @get:Input
    @get:Option(option = "launcherClass", description = "The classpath of the application launcher class.")
    abstract var launcherClass: String

    /**
     * The base directory path where local cached files for the launcher
     * are stored on the local system.
     */
    @get:Input
    @get:Option(option = "cacheDir", description = "The base directory where local cached launcher files are stored.")
    abstract var cacheDir: String

    /**
     * The task execution action
     */
    @TaskAction
    fun taskAction() {
        println("Generating application manifest...")
        /*
         * Build the application manifest file.
         */
        val manifest = AppManifest()

        manifest.uri = URI.create(this.uri)
        manifest.version = this.version
        manifest.cacheDir = this.cacheDir
        manifest.launcherClass = this.launcherClass
        manifest.ts = System.currentTimeMillis()

        Files.walkFileTree(Paths.get(outputDir), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                if(!Files.isDirectory(file) && !file.fileName.toString().startsWith("launcher")) {
                    println("Found runtime dependency Jar: '$file'.")
                    manifest.files.add(AppFile(Paths.get(outputDir), file))
                }

                return FileVisitResult.CONTINUE
            }
        })

        println("Exporting generated manifest to output directory.")
        JAXB.marshal(manifest, Paths.get(outputDir).resolve(manifest.filename).toFile())
    }
}