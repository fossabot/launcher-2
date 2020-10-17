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

import javafx.application.Platform
import org.tinylog.kotlin.Logger

/**
 * Represents a abstraction class which handles the launcher tasks.
 */
abstract class AbstractLauncher {

    /**
     * The logic invoked when the launcher logic is to start.
     */
    abstract fun onLaunch()

    /**
     * The logic invoked when the launch sequence completes.
     */
    abstract fun onComplete()

    /**
     * Tells the launcher everything is complete and to close and invoke 'onComplete' implementation
     * logic.
     */
    fun complete() {
        Logger.info("Launcher has completed successfully. Continuing to application startup.")

        /*
         * Close the javafx application.
         */
        Platform.exit()

        /*
         * Invoke the onComplete logic.
         */
        this.onComplete()
    }

    /**
     * Updates the progress bar value to specific double percentage between 0.0 - 1.0
     *
     * @param progress Double
     */
    fun updateProgress(progress: Double) {
        SpectralLauncher.app.get().updateProgress(progress)
    }

    /**
     * Adds a progress bar value to the current progress bar value.
     *
     * @param progress Double
     */
    fun addProgress(progress: Double) {
        SpectralLauncher.app.get().addProgress(progress)
    }

    /**
     * Updates the status text in the launcher view.
     *
     * @param status String
     */
    fun updateStatus(status: String) {
       SpectralLauncher.app.get().updateStatus(status)
    }
}