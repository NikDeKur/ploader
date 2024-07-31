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
import com.jcraft.jsch.Session
import dev.nikdekur.ndkore.ext.recordTiming
import dev.nikdekur.ploader.files.FilesService
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.Properties

class SFTPFilesService(
    private val host: String,
    private val port: Int,
    private val username: String,
    private val password: String
) : FilesService {

    val logger = LoggerFactory.getLogger(javaClass)

    lateinit var session: Session
    lateinit var channel: ChannelSftp

    override suspend fun start() {
        logger.info("Connecting to $host:$port as $username")

        val jsch = JSch()
        session = jsch.getSession(username, host, port)

        val config = Properties()
        config["StrictHostKeyChecking"] = "no"
        session.setConfig(config)
        session.setPassword(password)
        session.connect()

        logger.info("Connected to host")

        channel = session.openChannel("sftp") as ChannelSftp
        channel.connect()

        logger.info("Connected to SFTP server")

    }

    override suspend fun stop() {
        logger.info("Disconnecting from $host:$port")
        // Don't close the channel, jsch will do it for us
        session.disconnect()
    }

    override suspend fun put(local: String, remote: String, overwrite: Boolean) {
        logger.info("Uploading $local to $remote")
        logger.recordTiming(Level.INFO, "Uploading") {
            val mode = getOverwriteMode(overwrite)
            channel.put(local, remote, mode)
        }
    }

    override suspend fun get(local: String, remote: String, overwrite: Boolean) {
        logger.info("Downloading $remote to $local")
        logger.recordTiming(Level.INFO, "Downloading") {
            val mode = getOverwriteMode(overwrite)
            channel[remote, local, null, mode]
        }
    }

    inline fun getOverwriteMode(overwrite: Boolean): Int {
        return if (overwrite) ChannelSftp.OVERWRITE else ChannelSftp.RESUME
    }

    override suspend fun remove(remote: String) {
        logger.info("Removing $remote")
        channel.rm(remote)
    }


    override fun toString(): String {
        return "SFTPFilesService(host='$host', port=$port, username='$username')"
    }

}