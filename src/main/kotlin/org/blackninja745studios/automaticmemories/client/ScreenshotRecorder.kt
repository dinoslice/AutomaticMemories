package org.blackninja745studios.automaticmemories.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.Framebuffer
import net.minecraft.util.Util
import org.blackninja745studios.automaticmemories.client.config.Configuration
import java.io.File
import java.util.Optional

typealias MinecraftScreenshotRecorder = net.minecraft.client.util.ScreenshotRecorder

object ScreenshotRecorder {
    fun saveScreenshot(saveDirectory: File, prefix: String, frameBuffer: Framebuffer, messageReceiver: (Optional<String>) -> Unit) {
        if (RenderSystem.isOnRenderThread())
            saveScreenshotInner(saveDirectory, prefix, frameBuffer, messageReceiver)
        else
            RenderSystem.recordRenderCall {
                saveScreenshotInner(saveDirectory, prefix, frameBuffer, messageReceiver)
            }
    }

    private fun saveScreenshotInner(saveDirectory: File, prefix: String, frameBuffer: Framebuffer, messageReceiver: (Optional<String>) -> Unit) {
        val nativeImage = MinecraftScreenshotRecorder.takeScreenshot(frameBuffer)
        saveDirectory.mkdirs()

        val screenshotFile = assignScreenshotFilename(saveDirectory, prefix)

        Util.getIoWorkerExecutor().execute {
            try {
                nativeImage.writeTo(screenshotFile)

                messageReceiver(Optional.of(screenshotFile.absolutePath))

                AutomaticMemories.LOGGER.info("Saved automatic screenshot as $screenshotFile, next screenshot in ${Configuration.INTERVAL_MS} ms.")
            } catch(e: Exception) {
                AutomaticMemories.LOGGER.error("Couldn't save screenshot", e)

                messageReceiver(Optional.empty())
            } finally {
                nativeImage.close()
            }
        }
    }

    private fun assignScreenshotFilename(directory: File, prefix: String): File {
        val name = prefix + Util.getFormattedCurrentTime()

        var i = 1
        var file = File(directory, name)

        val recreateFile = {
            file = File(directory, name + (if (i == 1) { "" } else { "_$i" } + ".png"))
            file
        }

        while (recreateFile().exists()) {
            ++i
        }

        return file
    }
}