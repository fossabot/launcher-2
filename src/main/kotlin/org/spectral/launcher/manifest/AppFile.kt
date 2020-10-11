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

package org.spectral.launcher.manifest

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.Adler32
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

/**
 * Represents an application Jar file which is the application or one of it's
 * library dependency files.
 */
@XmlAccessorType(XmlAccessType.FIELD)
class AppFile() {

    /**
     * The name of the file
     */
    @field:XmlAttribute
    lateinit var file: String

    /**
     * The checksum hash of the file.
     */
    @field:XmlAttribute
    var checksum: Long = -1L

    /**
     * The size in bytes of the file.
     */
    @field:XmlAttribute
    var size: Long = -1L

    /**
     * Create a application file from a [basePath] and [file] path.
     *
     * @param basePath Path
     * @param file Path
     * @constructor
     */
    constructor(basePath: Path, file: Path) : this() {
        this.file = basePath.relativize(file).toString().replace("\\", "/")
        this.size = Files.size(file)
        this.checksum = checksum(file)
    }

    /**
     * Gets the [URL] of this application file respecting a provided cache directory.
     *
     * @param cacheDir Path
     * @return URL
     */
    fun toURL(cacheDir: Path): URL {
        return cacheDir.resolve(file).toFile().toURI().toURL()
    }

    companion object {
        /**
         * Calculates the checksum of a file at a provided path.
         *
         * @param path Path
         * @return Long
         */
        private fun checksum(path: Path): Long {
            Files.newInputStream(path).use { reader ->
                val checksum = Adler32()
                val buf = ByteArray(16384)

                var read: Int
                while(reader.read(buf).also { read = it } > -1) {
                    checksum.update(buf, 0, read)
                }

                return checksum.value
            }
        }
    }
}