import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

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

object Project {
    const val version = "1.0.0"
    const val kotlinVersion = "1.4.10"
    const val gradleVersion = "6.6.1"
    val jvmVersion = JavaVersion.VERSION_11.toString()
}

object Plugin {
    object Version {
        const val openjfx = "0.0.9"
        const val shadowjar = "6.1.0"
    }

    const val openjfx = "org.openjfx.javafxplugin"
    const val shadowjar = "com.github.johnrengelman.shadow"
}

object Library {
    private object Version {
        const val tinylog = "2.1.2"
        const val jaxb = "2.3.1"
        const val tornadofx = "1.7.20"
    }

    const val tinylogImpl = "org.tinylog:tinylog-impl:${Version.tinylog}"
    const val tinylogApi = "org.tinylog:tinylog-api-kotlin:${Version.tinylog}"
    const val jaxb = "javax.xml.bind:jaxb-api:${Version.jaxb}"
    const val jaxbRuntime = "org.glassfish.jaxb:jaxb-runtime:${Version.jaxb}"
    const val tornadofx = "no.tornado:tornadofx:${Version.tornadofx}"
}

val PluginDependenciesSpec.openjfx: PluginDependencySpec get() {
    return id(Plugin.openjfx) version(Plugin.Version.openjfx)
}

val PluginDependenciesSpec.shadowjar: PluginDependencySpec get() {
    return id(Plugin.shadowjar) version(Plugin.Version.shadowjar)
}