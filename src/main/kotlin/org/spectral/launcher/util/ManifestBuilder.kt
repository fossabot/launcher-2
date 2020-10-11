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

package org.spectral.launcher.util

import org.spectral.launcher.manifest.AppFile
import org.spectral.launcher.manifest.AppManifest
import org.tinylog.kotlin.Logger
import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import javax.xml.bind.JAXB

object ManifestBuilder {

    /**
     * Main method for building a manifest.xml of a gradle project.
     *
     * Argument format:
     * 0 -> remote base url (http://archive.spectralpowered.org/latest/
     * 1 -> launcher class name (org.spectral.client.launcher.Launcher)
     * 2 -> version string ("1.0.0")
     * 3 -> dependencies folder (deps/)
     * 4 -> cache directory (bin/)
     *
     * @param args Array<String>
     */
    @JvmStatic
    fun main(args: Array<String>) {
        if(args.size != 5) {
            throw IllegalArgumentException("Invalid specified arguments.")
        }

        val baseURI = URI.create(args[0])
        val launcherClass = args[1]
        val version = args[2]
        val depsPath = Paths.get(args[3])
        val cacheDir = args[4]

        Logger.info("Generating manifest...")

        val manifest = create(baseURI, launcherClass, depsPath)
        manifest.version = version
        manifest.cacheDir = cacheDir

        /*
         * Marshal the generated manifest.
         */
        Logger.info("Saving generated manifest to file: 'manifest.xml' in deps folder.")
        JAXB.marshal(manifest, depsPath.resolve("manifest.xml").toFile())
    }

    /**
     * Creates a App manifest instance from the provided arguments.
     *
     * @param baseURI URI
     * @param launcherClass String
     * @param depsPath Path
     * @return AppManifest
     */
    private fun create(baseURI: URI, launcherClass: String, depsPath: Path): AppManifest {
        val manifest = AppManifest()

        manifest.ts = System.currentTimeMillis()
        manifest.uri = baseURI
        manifest.launcherClass = launcherClass

        Files.walkFileTree(depsPath, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                if(!Files.isDirectory(file) && !file.fileName.toString().startsWith("launcher")) {
                    manifest.files.add(AppFile(depsPath, file))
                }

                return FileVisitResult.CONTINUE
            }
        })

        return manifest
    }
}