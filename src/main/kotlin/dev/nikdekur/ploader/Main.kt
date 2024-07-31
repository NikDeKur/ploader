/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.nikdekur.ploader.command.CommandService
import dev.nikdekur.ploader.command.rcon.RCONCommandService
import dev.nikdekur.ploader.config.PLoaderActionConfig
import dev.nikdekur.ploader.config.PLoaderConfig
import dev.nikdekur.ploader.files.local.LocalFilesService
import dev.nikdekur.ploader.files.sftp.SFTPFilesService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.slf4j.LoggerFactory
import java.io.File

suspend fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("dev.nikdekur.ploader.Main")

    val configFilePath = args.firstOrNull() ?: "config.yml"

    val configFile = File(configFilePath)
    if (!configFile.exists() || configFile.length() == 0L) {
        logger.error("Configuration file not found: $configFilePath or empty.")
        return
    }

    val yaml = Yaml(
        SerializersModule {
            polymorphic(PLoaderActionConfig::class) {
                subclass(PLoaderActionConfig.Upload::class, PLoaderActionConfig.Upload.serializer())
                subclass(PLoaderActionConfig.Remove::class, PLoaderActionConfig.Remove.serializer())
                subclass(PLoaderActionConfig.Command::class, PLoaderActionConfig.Command.serializer())
            }
        },
        YamlConfiguration(
            polymorphismPropertyName = "action",
            polymorphismStyle = PolymorphismStyle.Property
        )
    )
    val config = yaml.decodeFromString<PLoaderConfig>(configFile.readText())

    logger.info("Configuration loaded. Found ${config.actions.size} actions to execute.")

    val commandsService = config.rcon?.let {
        RCONCommandService(
            it.host,
            it.port,
            it.password
        )
    } ?: CommandService.NOOP

    logger.debug("Using command service: $commandsService")

    val filesService = config.sftp?.let {
        SFTPFilesService(
            it.host,
            it.port,
            it.username,
            it.password
        )
    } ?: LocalFilesService

    logger.debug("Using files service: $filesService")

    commandsService.start()
    filesService.start()

    logger.info("Services started.")

    val ploader = PLoaderImpl(commandsService, filesService)

    logger.info("Executing actions...")

    config.actions.forEach {
        ploader.executeAction(it)
    }

    logger.info("All actions executed.")

    commandsService.stop()
    filesService.stop()

    logger.info("All services stopped.")

}