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

package org.spectral.launcher.gui.animation

import javafx.animation.Interpolator
import javafx.animation.Transition
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Duration
import kotlin.math.floor
import kotlin.math.min

open class Animation : Transition() {

    lateinit var sequence: Array<Image>
    lateinit var imageView: ImageView
    private var frameCount = 0
    private var lastIndex = -1

    fun init(sequence: Array<Image>, duration: Double) {
        this.sequence = sequence
        this.imageView = ImageView(sequence[0])
        this.frameCount = sequence.size
        cycleCount = 1
        cycleDuration = Duration.millis(duration)
        interpolator = Interpolator.LINEAR
    }

    override fun interpolate(frac: Double) {
        val index = min(floor(frac * frameCount).toInt(), frameCount - 1)
        if(index != lastIndex) {
            imageView.image = sequence[index]
            lastIndex = index
        }
    }
}