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

interface PLoader {
    val commandService: CommandService
    val filesService: FilesService

    suspend fun executeAction(action: Action)
}