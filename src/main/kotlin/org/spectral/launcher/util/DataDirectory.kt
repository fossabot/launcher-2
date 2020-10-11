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

package org.spectral.launcher.util

import org.tinylog.kotlin.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Utility for getting the platform specific home folder
 * where spectral saves it's data.
 */
object DataDirectory {

    /**
     * The name of the lowest data directory folder for the spectral data folder path.
     */
    private const val directoryName = "spectral"

    /**
     * Caches the data directory path for the current platform running this program.
     */
    val path: Path by lazy { this.forPlatform(Platform.current()) }

    /**
     * Gets the path of the spectral data folder depending on platform specifics.
     *
     * @param platform Platform
     * @return Path
     */
    fun forPlatform(platform: Platform): Path {
        /*
         * Build the path depending on platform.
         */
        val path = when(platform) {
            /*
             * MAC OSX
             */
            Platform.MAC -> {
                Paths.get(System.getProperty("user.home"))
                    .resolve("Library")
                    .resolve("Application Support")
                    .resolve(directoryName)
            }

            /*
             * Microsoft Windows
             */
            Platform.WINDOWS -> {
                Paths.get(System.getProperty("user.home"))
                    .resolve("AppData")
                    .resolve("Roaming")
                    .resolve(directoryName)
            }

            /*
             * Linux and Other Platforms
             */
            Platform.LINUX, Platform.OTHER -> {
                Paths.get(System.getProperty("user.home"))
                    .resolve(".$directoryName")
            }
        }

        /*
         * Create the directory if it does not exist.
         */
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path)
            } catch(e : IOException) {
                Logger.error(e) { "Failed to create Spectral data folder at '$path'." }
                throw e
            }
        }

        return path
    }
}