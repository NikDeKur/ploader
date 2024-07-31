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
import com.jcraft.jsch.SftpException
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

    lateinit var session: Session
    lateinit var channel: ChannelSftp

    override suspend fun start() {
        logger.info("Connecting to $host:$port as $username")
        val jsch = JSch()

        val jschSession = jsch.getSession(username, host, port)
        jschSession.setPassword(password)
        val config = Properties()
        config["StrictHostKeyChecking"] = "no"
        jschSession.setConfig(config)

        try {
            jschSession.connect()
            logger.info("Connected to host")
            channel = jschSession.openChannel("sftp") as ChannelSftp
            channel.connect()
            logger.info("Connected to SFTP server")
        } catch (e: Exception) {
            logger.error("Failed to connect to SFTP server", e)
            throw e
        }
    }

    override suspend fun stop() {
        logger.info("Disconnecting from $host:$port")
        if (::channel.isInitialized && channel.isConnected) {
            channel.disconnect()
        }
    }

    override suspend fun put(local: File, remote: File, overwrite: Boolean) {
        val remotePath = formatRemotePath(remote.path)
        logger.info("Uploading ${local.absolutePath} to $remotePath")
        logger.recordTiming(Level.INFO, "Uploading") {
            val mode = getOverwriteMode(overwrite)
            try {
                channel.put(local.absolutePath, remotePath, mode)
                logger.info("File uploaded successfully")
            } catch (e: SftpException) {
                logger.error("Failed to upload file", e)
                throw e
            }
        }
    }

    override suspend fun get(local: File, remote: File, overwrite: Boolean) {
        val remotePath = formatRemotePath(remote.path)
        logger.info("Downloading $remotePath to ${local.absolutePath}")
        logger.recordTiming(Level.INFO, "Downloading") {
            val mode = getOverwriteMode(overwrite)
            try {
                channel[remotePath, local.absolutePath, null, mode]
                logger.info("File downloaded successfully")
            } catch (e: SftpException) {
                logger.error("Failed to download file", e)
                throw e
            }
        }
    }

    inline fun getOverwriteMode(overwrite: Boolean): Int {
        return if (overwrite) ChannelSftp.OVERWRITE else ChannelSftp.RESUME
    }

    override suspend fun remove(remote: File) {
        val remotePath = formatRemotePath(remote.path)
        logger.info("Removing $remotePath")
        try {
            channel.rm(remotePath)
            logger.info("File removed successfully")
        } catch (e: SftpException) {
            logger.error("Failed to remove file", e)
            throw e
        }
    }

    private fun formatRemotePath(path: String): String {
        return path.replace("\\", "/").removePrefix("C:").trimStart('/')
    }

    override fun toString(): String {
        return "SFTPFilesService(host='$host', port=$port, username='$username')"
    }

}
