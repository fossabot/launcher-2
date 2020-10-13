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

import org.spectral.launcher.gui.LauncherApp
import org.spectral.launcher.manifest.AppManifest
import org.tinylog.kotlin.Logger
import java.nio.file.Files
import javax.xml.bind.JAXB

/**
 * Represents a launch provider instance.
 */
abstract class SpectralLauncher {

    abstract fun updateProgress(progress: Double)

    abstract fun addProgress(progress: Double)

    abstract fun updateStatus(status: String)

    /**
     * Launches the spectral launcher.
     */
    fun launch() {
        Logger.info("Initializing Spectral launcher...")
        tornadofx.launch<LauncherApp>()
    }

    open fun init() { throw UnsupportedOperationException() }

    lateinit var manifest: AppManifest

    internal fun updateManifest() {
        Logger.info("Preparing to update application manifest.")

        updateProgress(0.1)
        updateStatus("Loading application manifest...")

        syncManifest()
    }

    private fun syncManifest() {
        Logger.info("Loading embedded manifest file.")

        val embeddedManifest = SpectralLauncher::class.java.getResource("/manifest.xml")
        manifest = JAXB.unmarshal(embeddedManifest, AppManifest::class.java)

        val cacheDir = manifest.resolveCacheDir()
        val manifestPath = manifest.getPath(cacheDir)

        /*
         * Check if a local manifest is present in the cache
         * data directory.
         */
        if(Files.exists(manifestPath)) {
            Logger.info("Found local manifest. Loading manifest from cache directory.")
            manifest = JAXB.unmarshal(manifestPath.toFile(), AppManifest::class.java)
        }

        /*
         * Check if the remote manifest is a newer version.
         */
        try {
            val remoteManifest = AppManifest.load(manifest.resolveRemoteURI())

            if(remoteManifest.isNewerThan(manifest)) {
                Logger.info("A new manifest version was found. Updating local manifest.")
                updateStatus("Updating local manifest...")

                manifest = remoteManifest
                JAXB.marshal(manifest, manifestPath.toFile())
            }
        } catch (e : Exception) {
            Logger.warn("Failed to update from remote manifest.")
        }
    }
}