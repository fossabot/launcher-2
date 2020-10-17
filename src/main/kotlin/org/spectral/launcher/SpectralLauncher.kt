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

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXMLLoader
import org.spectral.launcher.gui.LauncherApp
import org.spectral.launcher.manifest.AppManifest
import org.tinylog.kotlin.Logger
import tornadofx.launch
import tornadofx.onChangeOnce
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.Files
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.xml.bind.JAXB


/**
 * Global controller for starting the spectral launcher.
 */
object SpectralLauncher {

    /**
     * The launcher implementation instance.
     */
    internal lateinit var launcher: AbstractLauncher

    /**
     * The late initialized JavaFX application instance.
     */
    internal var app = SimpleObjectProperty<LauncherApp>()

    /**
     * The launch context of this process.
     */
    internal lateinit var ctx: LaunchContext

    /**
     * Setup JavaFX application subscriptions
     */
    init {
        app.onChangeOnce {
            if(!this::launcher.isInitialized) {
                this.ignoreSSLCertificates()
                /*
                 * Sync the local manifest file.
                 */
                this.updateManifest()

                /*
                 * Sync the application files
                 */
                this.updateFiles()
            }

            /*
             * Create the application environment.
             */
            this.launchApplicationEnvironment()
        }
    }

    /**
     * JVM Static entry into the code.
     *
     * @param args Array<String>
     */
    @JvmStatic
    fun main(args: Array<String>) {
        /*
         * Start the launcher
         */
        this.launch()
    }

    /**
     * Launches with a set launcher implementation instance.
     *
     * @param launcher AbstractLauncher
     */
    fun launch(launcher: AbstractLauncher) {
        this.launcher = launcher
        this.launch()
    }

    /**
     * Launches the Spectral launcher program.
     */
    fun launch() {
        Logger.info("Initializing Launcher...")

        /*
         * Create a launch context.
         */
        ctx = LaunchContext()

        /*
         * Start the JavaFX application.
         */
        launch<LauncherApp>()
    }

    /**
     * Synchronizes the local manifest file on the system with
     * the latest remote copy.
     */
    fun updateManifest() {
        Logger.info("Updating local application manifest.")

        /*
         * Update the progress and status.
         */
        app.get().updateProgress(0.1)
        app.get().updateStatus("Synchronizing manifest...")

        this.syncManifest()
    }

    /**
     * Synchronizes local application dependency files.
     */
    fun updateFiles() {
        Logger.info("Updating local application files.")

        /*
         * Update the progress and status.
         */
        app.get().updateProgress(0.2)
        app.get().updateStatus("Synchronizing application files...")

        this.syncFiles()
    }

    /**
     * Loads the embedded manifest file from the resources of this application.
     * From there, it trys to fetch the manifest verison at the remote URI of the embedded version.
     * If the remote manifest is a newer version, then its downloaded and replaced with the local copy
     * provided the local copy is not newer as well.
     */
    private fun syncManifest() {
        Logger.info("Loading embedded application manifest.")

        val embeddedManifest = SpectralLauncher::class.java.getResource("/manifest.xml")
        ctx.manifest = JAXB.unmarshal(embeddedManifest, AppManifest::class.java)

        val cacheDir = ctx.manifest.resolveCacheDir()
        val manifestPath = ctx.manifest.getPath(cacheDir)

        Files.createDirectories(cacheDir)

        /*
         * If a local manifest file exists, load that now.
         */
        if(Files.exists(manifestPath)) {
            Logger.info("Found local application manifest version. Loading from local system.")
            ctx.manifest = JAXB.unmarshal(manifestPath.toFile(), AppManifest::class.java)
        }

        /*
         * Try to check if the remote manifest version is newer.
         */
        try {
            val remoteManifest = AppManifest.load(ctx.manifest.resolveRemoteURI())

            /*
             * Check if the remote manifest is not the same as the current manifest.
             */
            if(remoteManifest != ctx.manifest) {
                /*
                 * Check if the remote manifest is newer than the currently
                 * loaded manifest.
                 */
                if(remoteManifest.isNewerThan(ctx.manifest)) {
                    Logger.info("Found newer remote application manifest. Downloading now.")

                    /*
                     * Update the progress status
                     */
                    app.get().updateStatus("Downloading latest manifest...")

                    ctx.manifest = remoteManifest
                    JAXB.marshal(ctx.manifest, manifestPath.toFile())

                    Logger.info("Completed download of manifest v${ctx.manifest.version}.")
                    app.get().updateStatus("Updated manifest to v${ctx.manifest.version}...")
                }
            }
        } catch (e: Exception) {
            Logger.warn(e, "Unable to fetch remote application manifest.")
        }
    }

    private fun syncFiles() {
        Logger.info("Scanning local cache for files requiring an update.")

        val cacheDir = ctx.manifest.resolveCacheDir()

        /*
         * The library files which require an update.
         */
        val needsUpdate = ctx.manifest.files
            .filter { it.needsUpdate(cacheDir) }

        /*
         * If nothing requires an update update the progress and move on.
         */
        if(needsUpdate.isEmpty()) {
            app.get().updateProgress(0.2)
            app.get().updateStatus("Application is update to date.")

            return
        }

        /*
         * Calculate the total bytes that is required to download for updating
         * the library files. These values are used for calculating the
         * proper progress bar value.
         */
        val totalBytes = needsUpdate.map { it.size }.sum()
        var totalDownloaded = 0L

        /*
         * Download the latest library files.
         */
        needsUpdate.forEach { lib ->
            Logger.info("Downloading application library file: '${lib.file}'. size=${lib.size} bytes.")

            val target = cacheDir.resolve(lib.file).toAbsolutePath()
            Files.createDirectories(target.parent)

            val separator = if(ctx.manifest.uri.path.endsWith("/")) "" else "/"
            val uri = URI.create(ctx.manifest.uri.toString() + separator + lib.file)

            try {
                val input = openDownloadStream(uri)
                val output = Files.newOutputStream(target)

                val buf = ByteArray(65536)
                var read: Int
                while(input.read(buf).also { read = it } > -1) {
                    output.write(buf, 0, read)
                    totalDownloaded += read

                    /*
                     * Calculate and update the progress and status.
                     */
                    val progress = totalDownloaded.toDouble() / totalBytes.toDouble()
                    app.get().updateProgress(progress)
                    app.get().updateStatus("Downloading file ${lib.file}...")
                }
            } catch (e: Exception) {
                Logger.error(e) { "Failed to download an application file from archive server." }

                app.get().updateStatus("Failed to download application file. Check your internet connection.")
                app.get().updateProgress(1.0)
            }
        }

        /*
         * Attempt to update files again. This should show all files are up to date.
         */
        this.updateFiles()
    }

    /**
     * Ignores the SSL certificates by accepting any self-signed.
     *
     * @throws CertificateException
     */
    private fun ignoreSSLCertificates() {
        val trustManager = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }
            })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustManager, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)

        val hostnameVerifier = HostnameVerifier { s: String?, sslSession: SSLSession? -> true }
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
    }

    /**
     * Opens a byte stream to download a file from a provided URI.
     *
     * @param uri URI
     * @return InputStream
     */
    private fun openDownloadStream(uri: URI): InputStream {
        if(uri.scheme == "file") {
            return Files.newInputStream(File(uri.path).toPath())
        }

        val connection = uri.toURL().openConnection()
        return connection.getInputStream()
    }

    /**
     * Creates a application class loader with all the dependencies loaded which are specified in the
     * application manifest file..
     */
    private fun createClassLoader(): ClassLoader {
        Logger.info("Creating application class loader.")

        app.get().updateStatus("Creating application environment...")

        val cacheDir = ctx.manifest.resolveCacheDir()
        val libs = ctx.manifest.files.map { it.toURL(cacheDir) }
        val systemClassLoader = ClassLoader.getSystemClassLoader()

        if(systemClassLoader is LauncherClassLoader) {
            systemClassLoader.addUrls(libs)
            return systemClassLoader
        } else {
            val classloader = URLClassLoader(libs.toTypedArray())
            Thread.currentThread().contextClassLoader = classloader

            /*
             * Update the JavaFX FXML loader thread to the current class loader.
             */
            FXMLLoader.setDefaultClassLoader(classloader)

            /*
             * We need to inform JavaFX of the classloader which is going to be
             * continuing the request threads.
             */
            Platform.runLater {
                Thread.currentThread().contextClassLoader = classloader
            }

            return classloader
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun launchApplicationEnvironment() {
        Logger.info("Preparing application environment.")

        val launcherInstance = if(!this::launcher.isInitialized) {
            /*
             * Create the class loader with all of the application dependencies pre-loaded.
             */
            val classloader = this.createClassLoader()

            /*
             * Load the the launcher class from the application manifest file.
             */
            val launcherClass = classloader.loadClass(ctx.manifest.launcherClass) as Class<out AbstractLauncher>
            launcherClass.getDeclaredConstructor().newInstance()
        } else {
            this.launcher
        }

        Logger.info("Handing off launch sequence to the Spectral client launcher class.")

        app.get().updateProgress(0.25)
        app.get().updateStatus("Launcher handing off to Spectral client...")

        /*
         * Hand off the launch sequence to the implement launcher logic
         * by calling the loaded launcher class's 'onLaunch()' logic.
         */
        launcherInstance.onLaunch()
    }
}