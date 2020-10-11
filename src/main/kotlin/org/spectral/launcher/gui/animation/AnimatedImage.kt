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

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import org.spectral.launcher.gui.animation.decoder.GifDecoder

class AnimatedImage(fileName: String, duration: Double) : Animation() {

    init {
        val decoder = GifDecoder()
        decoder.read(fileName)
        val sequence = mutableListOf<Image>()
        for(i in 0 until decoder.frameCount) {
            val img: WritableImage? = null
            val bufferedImg = decoder.getFrame(i)
            sequence.add(i, SwingFXUtils.toFXImage(bufferedImg, img))
        }

        this.init(sequence.toTypedArray(), duration)
    }
}