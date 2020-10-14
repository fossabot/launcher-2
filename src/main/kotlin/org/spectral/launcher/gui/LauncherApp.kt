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
import tornadofx.*

/**
 * The launcher JavaFX application.
 */
class LauncherApp : Application() {

    private val view = LauncherView()
    private lateinit var stage: Stage

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
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch<LauncherApp>()
        }
    }
}