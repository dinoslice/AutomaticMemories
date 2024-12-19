package org.blackninja745studios.automaticmemories.client.config

import net.fabricmc.loader.api.FabricLoader
import org.blackninja745studios.automaticmemories.client.AutomaticMemories
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object Configuration {
    var INTERVAL_MS: Long = 3600 * 1000 * 3
    var LEFTOVER_INTERVAL_MS: Long = 0
    var RESTART_TIMER_EACH_SESSION: Boolean = false
    var REQUIRE_IN_WORLD: Boolean = true
    var REQUIRE_UNPAUSED: Boolean = false

    var SAVE_DIRECTORY: String = "screenshots"
    var SCREENSHOT_PREFIX: String = "auto_"

    var NOTIFY_PLAYER: Boolean = false

    val CONFIG_PATH: Path = FabricLoader.getInstance().configDir.resolve("automaticmemories.properties")

    fun loadFromFile(path: Path) {
        try {
            Files.newBufferedReader(path).use {
                val properties = Properties(1)
                properties.load(it)

                INTERVAL_MS = 0L.coerceAtLeast(properties.getProperty("interval_ms", INTERVAL_MS.toString()).toLong())

                val leftoverIntervalMs =
                    properties.getProperty("leftover_interval_ms", LEFTOVER_INTERVAL_MS.toString()).toLong()
                LEFTOVER_INTERVAL_MS = (0L.coerceAtLeast(leftoverIntervalMs)).coerceAtLeast(INTERVAL_MS)

                RESTART_TIMER_EACH_SESSION =
                    properties.getProperty("restart_timer_each_session", RESTART_TIMER_EACH_SESSION.toString())
                        .toBoolean()
                REQUIRE_IN_WORLD = properties.getProperty("require_in_world", REQUIRE_IN_WORLD.toString()).toBoolean()
                REQUIRE_UNPAUSED = properties.getProperty("require_unpaused", REQUIRE_UNPAUSED.toString()).toBoolean()

                SAVE_DIRECTORY = properties.getProperty("save_directory", SAVE_DIRECTORY)
                SCREENSHOT_PREFIX = properties.getProperty("screenshot_prefix", SCREENSHOT_PREFIX)

                NOTIFY_PLAYER = properties.getProperty("notify_player", NOTIFY_PLAYER.toString()).toBoolean()
            }
        } catch(ignored: Exception) {
            saveToFile(path)
        }
    }

    fun saveToFile(path: Path) {
        val properties = Properties(1)

        properties["interval_ms"] = INTERVAL_MS.toString()
        properties["leftover_interval_ms"] = LEFTOVER_INTERVAL_MS.toString()
        properties["restart_timer_each_session"] = RESTART_TIMER_EACH_SESSION.toString()
        properties["require_in_world"] = REQUIRE_IN_WORLD.toString()
        properties["require_unpaused"] = REQUIRE_UNPAUSED.toString()

        properties["save_directory"] = SAVE_DIRECTORY
        properties["screenshot_prefix"] = SCREENSHOT_PREFIX

        properties["notify_player"] = NOTIFY_PLAYER.toString()

        try {
            Files.newBufferedWriter(path).use {
                properties.store(it, "AutomaticMemories config")
            }
        } catch(exception: IOException) {
            AutomaticMemories.LOGGER.error(exception.message, exception)
        }
    }

    fun getFullDirectory(runDirectory: File, saveDirectory: String): File {
        var saveDir = File(saveDirectory)

        if (!saveDir.isAbsolute) {
            saveDir = File(runDirectory, saveDirectory)
        }

        return saveDir
    }
}