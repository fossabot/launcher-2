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

import java.io.File
import java.net.URL
import java.net.URLClassLoader

/**
 * The spectral client launcher classloader.
 *
 * @param parent ClassLoader
 * @constructor
 */
class LauncherClassLoader(parent: ClassLoader) : URLClassLoader(
    buildClasspath(System.getProperty("java.class.path")),
    parent
) {

    /**
     * Adds URLs to load to the class loader object.
     *
     * @param urls List<URL>
     */
    fun addUrls(urls: List<URL>) {
       urls.forEach { url ->
           this.addURL(url)
       }
    }

    companion object {

        private fun buildClasspath(classPath: String): Array<URL> {
            if(classPath.trim().isEmpty()) {
                return emptyArray()
            }

            var cp = classPath
            val urls = mutableListOf<URL>()

            var pos: Int
            while(cp.indexOf(File.pathSeparatorChar).also { pos = it } > -1) {
                val part = cp.substring(0, pos)
                addClasspathPart(urls, part)
                cp = cp.substring(pos + 1)
            }

            addClasspathPart(urls, cp)

            return urls.toTypedArray()
        }

        private fun addClasspathPart(urls: MutableList<URL>, part: String) {
            if(part.trim().isEmpty()) {
                return
            }

            urls.add(File(part).toURI().toURL())
        }
    }
}