package org.blackninja745studios.automaticmemories.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.Framebuffer
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import org.blackninja745studios.automaticmemories.client.config.Configuration
import java.io.File
import java.util.function.Consumer

typealias MinecraftScreenshotRecorder = net.minecraft.client.util.ScreenshotRecorder

object ScreenshotRecorder {
    fun saveScreenshot(saveDirectory: File, prefix: String, frameBuffer: Framebuffer, messageReceiver: Consumer<Text>) {
        if (RenderSystem.isOnRenderThread())
            saveScreenshotInner(saveDirectory, prefix, frameBuffer, messageReceiver)
        else
            RenderSystem.recordRenderCall {
                saveScreenshotInner(saveDirectory, prefix, frameBuffer, messageReceiver)
            }
    }

    private fun saveScreenshotInner(saveDirectory: File, prefix: String, frameBuffer: Framebuffer, messageReceiver: Consumer<Text>) {
        val nativeImage = MinecraftScreenshotRecorder.takeScreenshot(frameBuffer)
        saveDirectory.mkdirs()

        val screenshotFile = assignScreenshotFilename(saveDirectory, prefix)

        Util.getIoWorkerExecutor().execute {
            try {
                nativeImage.writeTo(screenshotFile)

                val text = Text.translatable("automaticmemories.screenshot.success.clickable")
                    .formatted(Formatting.UNDERLINE)
                    .styled {
                        style: Style ->
                        style.withClickEvent(
                            ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotFile.absolutePath)
                        )
                    }

                messageReceiver.accept(AutomaticMemories.addChatPrefix(
                    Text.translatable(
                        "automaticmemories.screenshot.success.full", text,
                        ScreenshotTimer.formatTime(Configuration.INTERVAL_MS)
                    )
                ))

                AutomaticMemories.LOGGER.info("Saved automatic screenshot as $screenshotFile, next screenshot in ${Configuration.INTERVAL_MS} ms.")
            } catch(e: Exception) {
                AutomaticMemories.LOGGER.error("Couldn't save screenshot", e)

                messageReceiver.accept(AutomaticMemories.addChatPrefix(
                    Text.translatable("automaticmemories.screenshot.failure", e.message).formatted(Formatting.RED)
                ))
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