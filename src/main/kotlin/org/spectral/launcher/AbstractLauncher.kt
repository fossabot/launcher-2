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
 * Represents a abstraction class which handles the launcher tasks.
 */
abstract class AbstractLauncher {

    /**
     * The launch context this launcher was started with?
     */
    internal lateinit var ctx: LaunchContext

    /**
     * Launches the extended type's launcher.
     *
     * @param ctx LaunchContext
     */
    internal fun start(ctx: LaunchContext) {
        Logger.info("Starting initialized launcher tasks.")
    }
}