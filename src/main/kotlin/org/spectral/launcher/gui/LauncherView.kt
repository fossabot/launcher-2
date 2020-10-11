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

import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import org.spectral.launcher.gui.animation.AnimatedImage
import tornadofx.*

/**
 * The launcher JavaFX view
 *
 * @property root VBox
 */
class LauncherView : View("Spectral") {

    private var logoPane: Pane by singleAssign()

    /*
     * Animated Logo
     */
    private val animationLogoPath = LauncherView::class.java.getResource("/graphics/logo-animation.gif").toExternalForm()
    private val animatedLogo = AnimatedImage(animationLogoPath, 5000.0)

    override val root = vbox {
        setPrefSize(450.0, 400.0)
        alignment = Pos.CENTER

        /*
         * Spectral animated logo.
         */
        animatedLogo.cycleCount = 1
        animatedLogo.play()
        animatedLogo.imageView.fitWidth = 128.0
        animatedLogo.imageView.fitHeight = 128.0
        add(animatedLogo.imageView)

        /*
         * Spectral text label
         */
        label("S P E C T R A L") {
            font = Font(32.0)
            paddingTop = 32.0
        }

        /*
         * Loading progress bar
         */
        progressbar(0.1) {
            prefWidth = 350.0
            paddingTop = 32.0
        }

        /*
         * The status text
         */
        label("Initializing Launcher...") {
            font = Font(16.0)
            paddingTop = 16.0
        }
    }
}