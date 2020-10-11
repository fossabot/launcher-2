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

import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.launch
import tornadofx.setStageIcon

/**
 * The launcher JavaFX application.
 */
class LauncherApp : App(LauncherView::class) {

    init {
        setStageIcon(Image("/graphics/logo-app-icon.png"))
        importStylesheet("/css/spectral.css")
    }

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)
        super.start(stage)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch<LauncherApp>()
        }
    }
}