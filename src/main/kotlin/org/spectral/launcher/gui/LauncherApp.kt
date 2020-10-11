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
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.spectral.launcher.Launcher
import org.spectral.launcher.SpectralLauncher
import org.spectral.launcher.manifest.AppManifest
import org.tinylog.kotlin.Logger
import tornadofx.*
import java.util.concurrent.CountDownLatch

/**
 * The launcher JavaFX application.
 */
class LauncherApp : Application() {

    private val view = LauncherView()

    private lateinit var primaryStage: Stage
    private lateinit var stage: Stage

    init {
        launcher = object : Launcher {
            override fun onLaunch() {}

            override fun onComplete() {}

            override val launcherApp = this@LauncherApp

            override fun updateStatus(status: String) {
                view.updateStatus(status)
            }

            override fun updateProgress(progress: Double) {
                view.updateProgress(progress)
            }
        }
    }

    /**
     * Start the JavaFX application.
     *
     * @param stage Stage
     */
    override fun start(stage: Stage) {
        FX.registerApplication(this, stage)

        this.primaryStage = stage
        this.stage = Stage(StageStyle.UNDECORATED)
        this.stage.icons.add(Image("/graphics/logo-app-icon.png"))

        val scene = Scene(view.root)

        /*
         * Import the spectral CSS theme stylesheet.
         */
        scene.stylesheets.add("/css/spectral.css")

        this.stage.scene = scene

        this.stage.show()

        Thread {
            Thread.sleep(1000)

            val spectraLauncher = SpectralLauncher(launcher)
            spectraLauncher.updateManifest()
        }.start()
    }

    private fun runAsync(action: () -> Unit) {
        if(Platform.isFxApplicationThread()) {
            action()
            return
        }

        val doneLatch = CountDownLatch(1)
        Platform.runLater {
            try {
                action()
            } finally {
                doneLatch.countDown()
            }
        }

        try {
            doneLatch.await()
        } catch (e : InterruptedException) {
            /*
             * Do nothing.
             */
        }
    }

    companion object {

        lateinit var launcher: Launcher

        /**
         * Start the launcher with a provided spectral launcher instance.
         */
        fun start() {
            Logger.info("Initializing launcher...")
            launch<LauncherApp>()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            this.start()
        }
    }
}