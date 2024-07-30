/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.files.local

import dev.nikdekur.ploader.files.FilesService
import org.slf4j.LoggerFactory
import java.io.File

object LocalFilesService : FilesService {

    val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun start() {
        // Do nothing
    }

    override suspend fun stop() {
        // Do nothing
    }

    override suspend fun put(local: File, remote: File, overwrite: Boolean) {
        logger.info("Copying $local to $remote")
        local.copyTo(remote, overwrite = overwrite)
    }

    override suspend fun get(local: File, remote: File, overwrite: Boolean) {
        logger.info("Copying $remote to $local")
        remote.copyTo(local, overwrite = overwrite)
    }

    override suspend fun remove(remote: File) {
        logger.info("Deleting $remote")
        val result = remote.delete()
        if (!result) {
            logger.warn("Failed to delete $remote")
        }
    }

    override fun toString(): String {
        return "LocalFilesService()"
    }
}