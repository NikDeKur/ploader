/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ploader.files.sftp

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import dev.nikdekur.ndkore.ext.recordTiming
import dev.nikdekur.ploader.files.FilesService
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.util.Properties

class SFTPFilesService(
    private val host: String,
    private val port: Int,
    private val username: String,
    private val password: String
) : FilesService {

    val logger = LoggerFactory.getLogger(javaClass)

    lateinit var channel: ChannelSftp

    override suspend fun start() {
        logger.info("Connecting to $host:$port as $username")

        val jsch = JSch()
        val jschSession = jsch.getSession(username, host, port)

        val config = Properties()
        config["StrictHostKeyChecking"] = "no"
        jschSession.setConfig(config)
        jschSession.setPassword(password)
        jschSession.connect()

        logger.info("Connected to host")

        channel = jschSession.openChannel("sftp") as ChannelSftp
        channel.connect()
        
        logger.info("Connected to SFTP server")

    }

    override suspend fun stop() {
        logger.info("Disconnecting from $host:$port")
        channel.disconnect()
    }

    override suspend fun put(local: File, remote: File, overwrite: Boolean) {
        logger.info("Uploading ${local.absolutePath} to ${remote.absolutePath}")
        logger.recordTiming(Level.INFO, "Uploading") {
            val mode = getOverwriteMode(overwrite)
            channel.put(local.absolutePath, remote.absolutePath, mode)
        }
    }

    override suspend fun get(local: File, remote: File, overwrite: Boolean) {
        logger.info("Downloading ${remote.absolutePath} to ${local.absolutePath}")
        logger.recordTiming(Level.INFO, "Downloading") {
            val mode = getOverwriteMode(overwrite)
            channel[remote.absolutePath, local.absolutePath, null, mode]
        }
    }

    inline fun getOverwriteMode(overwrite: Boolean): Int {
        return if (overwrite) ChannelSftp.OVERWRITE else ChannelSftp.RESUME
    }

    override suspend fun remove(remote: File) {
        logger.info("Removing ${remote.absolutePath}")
        channel.rm(remote.absolutePath)
    }


    override fun toString(): String {
        return "SFTPFilesService(host='$host', port=$port, username='$username')"
    }

}