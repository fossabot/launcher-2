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

package org.spectral.launcher

import org.spectral.launcher.manifest.AppManifest
import org.tinylog.kotlin.Logger
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.bind.JAXB

/**
 * Represents the control abstraction for displaying the
 * Spectral launcher while background tasks are performed.
 */
class SpectralLauncher(private val provider: Launcher) {

    /**
     * The application manifest instance.
     */
    internal lateinit var manifest: AppManifest

    fun updateProgress(progress: Double) {
        provider.updateProgress(progress)
    }

    fun updateStatus(status: String) {
        provider.updateStatus(status)
    }

    /**
     * Creates a class loader instance where all the application files will be loaded
     * into for launching.
     *
     * @param cacheDir Path
     * @return ClassLoader
     */
    fun createClassLoader(cacheDir: Path): ClassLoader {
        val libs = manifest.files.map { it.toURL(cacheDir) }
        val systemClassLoader = ClassLoader.getSystemClassLoader()

        return if(systemClassLoader is LauncherClassLoader) {
            systemClassLoader.addUrls(libs)
            systemClassLoader
        } else {
            val classloader = URLClassLoader(libs.toTypedArray())
            Thread.currentThread().contextClassLoader = classloader
            classloader
        }
    }

    /**
     * Checks and updates the local manifest file if needed.
     */
    fun updateManifest() {
        Logger.info("Updating application manifest.")

        updateProgress(0.1)
        updateStatus("Fetching latest manifest...")

        syncManifest()
    }

    /**
     * Syncs the local manifest file with the embedded or remote manifest file.
     */
    internal fun syncManifest() {
        Logger.info("Loading embedded manifest file.")

        val embeddedManifest = SpectralLauncher::class.java.getResource("/manifest.xml")
        manifest = JAXB.unmarshal(embeddedManifest, AppManifest::class.java)

        val cacheDir = manifest.resolveCacheDir()
        val manifestPath = manifest.getPath(cacheDir)

        if(Files.exists(manifestPath)) {
            Logger.info("Local application manifest found. Loading manifest file.")
            manifest = JAXB.unmarshal(manifestPath.toFile(), AppManifest::class.java)
        }

        try {
            Logger.info("Checking remote manifest version.")

            val remoteManifest = AppManifest.load(manifest.uri)

            if(remoteManifest != manifest) {
                Logger.info("Update to the the local manifest is required.")

                if(remoteManifest.isNewerThan(manifest)) {
                    Logger.info("Updating the local manifest file to version ${remoteManifest.version}")
                    manifest = remoteManifest
                    /*
                     * Save the remote manifest over the local copy.
                     */
                    JAXB.marshal(manifest, manifestPath.toFile())
                }
            }
        } catch(e : Exception) {
            Logger.warn(e) { "Unable to update manifest from remote URI." }
        }
    }
}