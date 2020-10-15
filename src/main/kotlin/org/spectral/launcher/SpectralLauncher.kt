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

import javafx.beans.property.SimpleObjectProperty
import org.spectral.launcher.gui.LauncherApp
import org.spectral.launcher.manifest.AppManifest
import org.tinylog.kotlin.Logger
import tornadofx.launch
import tornadofx.onChangeOnce
import java.nio.file.Files
import javax.xml.bind.JAXB

/**
 * Global controller for starting the spectral launcher.
 */
object SpectralLauncher {

    /**
     * The late initialized JavaFX application instance.
     */
    internal var app = SimpleObjectProperty<LauncherApp>()

    /**
     * The launch context of this process.
     */
    internal lateinit var ctx: LaunchContext

    /**
     * Setup JavaFX application subscriptions
     */
    init {
        app.onChangeOnce {
            /*
             * Sync the local manifest file.
             */
            this.updateManifest()
        }
    }

    /**
     * JVM Static entry into the code.
     *
     * @param args Array<String>
     */
    @JvmStatic
    fun main(args: Array<String>) {
        /*
         * Start the launcher
         */
        this.launch()
    }

    /**
     * Launches the Spectral launcher program.
     */
    fun launch() {
        Logger.info("Initializing Launcher...")

        /*
         * Create a launch context.
         */
        ctx = LaunchContext()

        /*
         * Start the JavaFX application.
         */
        launch<LauncherApp>()
    }

    /**
     * Synchronizes the local manifest file on the system with
     * the latest remote copy.
     */
    fun updateManifest() {
        Logger.info("Updating local application manifest.")

        /*
         * Update the progress and status.
         */
        app.get().updateProgress(0.1)
        app.get().updateStatus("Synchronizing manifest...")

        this.syncManifest()
    }

    /**
     * Loads the embedded manifest file from the resources of this application.
     * From there, it trys to fetch the manifest verison at the remote URI of the embedded version.
     * If the remote manifest is a newer version, then its downloaded and replaced with the local copy
     * provided the local copy is not newer as well.
     */
    private fun syncManifest() {
        Logger.info("Loading embedded application manifest.")

        val embeddedManifest = SpectralLauncher::class.java.getResource("/app.xml")
        ctx.manifest = JAXB.unmarshal(embeddedManifest, AppManifest::class.java)

        val cacheDir = ctx.manifest.resolveCacheDir()
        val manifestPath = ctx.manifest.getPath(cacheDir)

        /*
         * If a local manifest file exists, load that now.
         */
        if(Files.exists(manifestPath)) {
            Logger.info("Found local application manifest version. Loading from local system.")
            ctx.manifest = JAXB.unmarshal(manifestPath.toFile(), AppManifest::class.java)
        }

        /*
         * Try to check if the remote manifest version is newer.
         */
        try {
            val remoteManifest = AppManifest.load(ctx.manifest.resolveRemoteURI())

            /*
             * Check if the remote manifest is not the same as the current manifest.
             */
            if(remoteManifest != ctx.manifest) {
                /*
                 * Check if the remote manifest is newer than the currently
                 * loaded manifest.
                 */
                if(remoteManifest.isNewerThan(ctx.manifest)) {
                    Logger.info("Found newer remote application manifest. Downloading now.")

                    /*
                     * Update the progress status
                     */
                    app.get().updateStatus("Downloading latest manifest...")

                    ctx.manifest = remoteManifest
                    JAXB.marshal(ctx.manifest, manifestPath.toFile())
                }
            }
        } catch(e : Exception) {
            Logger.warn("Unable to fetch remote application manifest.")
        }
    }
}