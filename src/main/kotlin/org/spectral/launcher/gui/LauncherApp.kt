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

package org.spectral.launcher.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.spectral.launcher.SpectralLauncher
import tornadofx.*

/**
 * The launcher JavaFX application.
 */
class LauncherApp : Application() {

    private val view = LauncherView()
    private lateinit var stage: Stage

    /**
     * Updates the progress value on the application view.
     *
     * @param progress Double
     */
    internal fun updateProgress(progress: Double) {
       view.updateProgress(progress)
    }

    /**
     * Adds a progress value to the current progress bar value.
     *
     * @param progress Double
     */
    internal fun addProgress(progress: Double) {
        view.addProgress(progress)
    }

    /**
     * Updates the status text in the view.
     *
     * @param status String
     */
    internal fun updateStatus(status: String) {
       view.updateStatus(status)
    }

    /**
     * Start the JavaFX application.
     *
     * @param stage Stage
     */
    override fun start(stage: Stage) {
        FX.registerApplication(this, stage)

        this.stage = Stage(StageStyle.UNDECORATED)
        this.stage.icons.add(Image(LauncherApp::class.java.getResource("/graphics/logo-app-icon.png").toExternalForm()))

        val scene = Scene(view.root)

        /*
         * Import the spectral CSS theme stylesheet.
         */
        scene.stylesheets.add(LauncherApp::class.java.getResource("/css/spectral.css").toExternalForm())

        this.stage.scene = scene

        this.stage.show()

        Thread {
            Thread.sleep(650)
            /*
             * Set the spectral launcher application instance.
             *
             * After this value is set, the observable subscription fire on this
             * launch sequence thread.
             */
            SpectralLauncher.app.set(this)
        }.start()
    }
}