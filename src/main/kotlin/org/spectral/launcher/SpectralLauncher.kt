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

import org.tinylog.kotlin.Logger

/**
 * Global controller for starting the spectral launcher.
 */
object SpectralLauncher {

    /**
     * JVM Static entry into the code.
     *
     * @param args Array<String>
     */
    @JvmStatic
    fun main(args: Array<String>) {
        Logger.info("Initializing...")

        /*
         * Start the launcher
         */
        this.launch()
    }

    /**
     * Launches the Spectral launcher program.
     */
    fun launch() {
        Logger.info("Preparing Spectral launcher.")


    }
}