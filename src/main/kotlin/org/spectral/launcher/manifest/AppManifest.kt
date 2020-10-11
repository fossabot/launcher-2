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

import java.net.URI
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

/**
 * Represents the manifest.xml manifest file which represents a current
 * version of the client as well as any libraries.
 */
@XmlRootElement(name = "Application")
class AppManifest {

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


}