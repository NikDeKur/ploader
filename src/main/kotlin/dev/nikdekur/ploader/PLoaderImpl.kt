/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader

import dev.nikdekur.ploader.action.Action
import dev.nikdekur.ploader.command.CommandService
import dev.nikdekur.ploader.files.FilesService
import org.slf4j.LoggerFactory

data class PLoaderImpl(
    override val commandService: CommandService,
    override val filesService: FilesService
) : PLoader {

    val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun executeAction(action: Action) {
        logger.debug("Executing action: $action")
        when (action) {
            is Action.Upload -> filesService.put(action.source, action.destination, action.overwrite)
            is Action.Remove -> filesService.remove(action.destination)
            is Action.Command -> commandService.executeCommand(action.command)
            else -> throw IllegalArgumentException("Unknown action type: $action")
        }
    }
}