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

import javafx.animation.Interpolator
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import org.spectral.launcher.gui.animation.AnimatedImage
import tornadofx.*
import kotlin.time.milliseconds

/**
 * The launcher JavaFX view
 *
 * @property root VBox
 */
class LauncherView : View("Spectral") {

    private var progressBar: ProgressBar by singleAssign()
    private var status: Label by singleAssign()

    /*
     * Animated Logo
     */
    private val animationLogoPath = LauncherView::class.java.getResource("/graphics/logo-animated.gif").toExternalForm()
    private val animatedLogo = AnimatedImage(animationLogoPath, 5000.0)

    /**
     * Updates the status label.
     *
     * @param status String
     */
    fun updateStatus(status: String) {
        Platform.runLater { this.status.text = status }
    }

    /**
     * Updates the progress bar with a easing animation.
     *
     * @param progress Double
     */
    fun updateProgress(progress: Double) {
        Platform.runLater {
            timeline {
                keyframe(480.millis) {
                    keyvalue(this@LauncherView.progressBar.progressProperty(), progress, Interpolator.EASE_IN)
                }
            }.play()
        }
    }

    /**
     * Add progress to the progress bar.
     *
     * @param progress Double
     */
    fun addProgress(progress: Double) {
        val newProgressValue = this.progressBar.progress + progress
        this.updateProgress(newProgressValue)
    }

    override val root = vbox {
        setPrefSize(400.0, 400.0)
        alignment = Pos.CENTER
        paddingTop = -48.0

        /*
         * Spectral animated logo.
         */
        animatedLogo.cycleCount = 1
        animatedLogo.play()
        animatedLogo.imageView.fitWidth = 196.0
        animatedLogo.imageView.fitHeight = 196.0
        add(animatedLogo.imageView)

        /*
         * Spectral text label
         */
        label("S P E C T R A L") {
            font = Font(32.0)
        }

        /*
         * Loading progress bar
         */
        progressbar(0.0) {
            progressBar = this
            prefWidth = 0.0
            paddingTop = 32.0
        }

        /*
         * The status text
         */
        label("Initializing Launcher...") {
            status = this
            font = Font(12.0)
            paddingTop = 32.0
        }
    }

    init {
        timeline {
            keyframe(480.millis) {
                keyvalue(progressBar.prefWidthProperty(), 300.0, interpolator = Interpolator.EASE_IN)
            }
        }.then(timeline {
            keyframe(880.millis) {
                keyvalue(progressBar.progressProperty(), 0.05, interpolator = Interpolator.EASE_IN)
            }
        })
    }
}