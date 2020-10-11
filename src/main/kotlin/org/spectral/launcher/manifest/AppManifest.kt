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

import org.spectral.launcher.util.DataDirectory
import org.spectral.launcher.util.Platform
import java.io.File
import java.net.URI
import java.nio.file.Path
import javax.xml.bind.JAXB
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * Represents the manifest.xml manifest file which represents a current
 * version of the client as well as any libraries.
 */
@XmlRootElement(name = "Application")
class AppManifest private constructor() {

    /**
     * The timestamp this manifest was created at.
     */
    @XmlAttribute
    var ts: Long = -1L

    /**
     * The URI where latest artifacts can be downloaded at.
     */
    @XmlAttribute
    lateinit var uri: URI

    /**
     * The main class which extends the SpectralLauncher abstract class.
     */
    @XmlAttribute(name = "main")
    lateinit var mainClass: String

    /**
     * The application files this manifest in for.
     */
    @XmlElement
    val files = mutableListOf<AppFile>()

    /**
     * The version of this manifest or application.
     */
    @XmlElement
    lateinit var version: String

    /**
     * The path where the application files are stored at on the local
     * system running this launcher process.
     */
    @XmlElement
    lateinit var cacheDir: String

    /**
     * The name of this manifest file.
     */
    val filename: String = "manifest.xml"

    /**
     * Gets the path of the manifest file given a resolved cache directory.
     *
     * @param cacheDir Path
     * @return Path
     */
    fun getPath(cacheDir: Path): Path {
        return cacheDir.resolve(this.filename)
    }

    /**
     * Gets the cache directory respective to the current running
     * platform operating system.
     *
     * @return Path
     */
    fun resolveCacheDir(): Path {
        /*
         * The spectral data directory.
         */
        val dataDirPath = DataDirectory.forPlatform(Platform.current())

        /*
         * Return the resolved cache directory path.
         */
        return dataDirPath.resolve(cacheDir)
    }

    /**
     * Gets whether this app manifest is a newer version that a provided
     * [other] manifest object.
     *
     * @param other AppManifest
     * @return Boolean
     */
    fun isNewerThan(other: AppManifest): Boolean {
        return ts > other.ts
    }

    companion object {
        /**
         * Creates a application manifest object from a provided
         * URI. This can either be a remote server or a local file.
         *
         * @param uri URI
         * @return AppManifest
         */
        fun load(uri: URI): AppManifest {
            /*
             * If the provided URI is a local file. We can just
             * directly unmarshal the manifest file.
             */
            if(uri.scheme == "file") {
               return JAXB.unmarshal(File(uri.path), AppManifest::class.java)
            }

            /*
             * Otherwise the uri must be a remote file. We need to make
             * a connection to the remote URI server to download the manfiest file.
             */
            val connection = uri.toURL().openConnection()

            /*
             * Stream / download the bytes from the remote manifest file.
             */
            connection.getInputStream().use { reader ->
                return JAXB.unmarshal(reader, AppManifest::class.java)
            }
        }
    }
}