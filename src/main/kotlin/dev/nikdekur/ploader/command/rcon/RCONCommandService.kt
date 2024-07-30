/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.command.rcon

import dev.nikdekur.ploader.command.CommandService
import org.slf4j.LoggerFactory
import java.io.DataInputStream
import java.net.ConnectException
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger

class RCONCommandService(
    val host: String,
    val port: Int,
    val password: String
) : CommandService {

    val logger = LoggerFactory.getLogger(javaClass)

    lateinit var socket: Socket

    val outputStream
        get() = socket.getOutputStream()
    val inputStream
        get() = socket.getInputStream()
    val dataInputStream
        get() = DataInputStream(inputStream)
    val requestIdCounter: AtomicInteger = AtomicInteger(1)

    override suspend fun start() {
        logger.info("Connecting to RCON server $host:$port")

        try {
            socket = Socket(host, port)
        } catch (e: ConnectException) {
            logger.error("Failed to connect to RCON server $host:$port")
            logger.error("Please make sure the server is running and the RCON port is correct")
            logger.error("Also make sure the Server is allowed to listen on the RCON port!")
            throw e
        }
        val response = send(RCONPacket.RequestType.LOGIN, password.toByteArray())
        check(response.requestId == response.responseId) { "RCON Login failed" }
        logger.info("Successfully connected to RCON server")
    }

    override suspend fun stop() {
        logger.info("Disconnecting from RCON server $host:$port")
        socket.close()
    }

    override suspend fun executeCommand(command: String) {
        logger.info("Executing command: $command")
        val response = send(RCONPacket.RequestType.COMMAND, command.toByteArray())
        logger.info("Response: ${String(response.payload)}")
    }

    fun send(type: RCONPacket.RequestType, payload: ByteArray): RCONPacket {
        val requestSize = 4 + 4 + payload.size + 2
        val requestId = requestIdCounter.getAndIncrement()
        val request = ByteBuffer.allocate(4 + requestSize).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(requestSize)
            putInt(requestId)
            putInt(type.id)
            put(payload)
            put(0)
            put(0)
        }
        return synchronized(socket) {
            outputStream.write(request.array())
            val header = ByteArray(3 * 4)
            dataInputStream.read(header)
            val usableHeader = ByteBuffer.wrap(header).apply {
                order(ByteOrder.LITTLE_ENDIAN)
            }
            val size = usableHeader.int
            val responseId = usableHeader.int
            val responseType = RCONPacket.RequestType.getById(usableHeader.int)

            val responsePayload = ByteArray(size - 4 - 4 - 2)

            dataInputStream.readFully(responsePayload)
            dataInputStream.readFully(ByteArray(2))
            RCONPacket(size, requestId, responseId, responseType, responsePayload)
        }
    }

    override fun toString(): String {
        return "RCONCommandService(host='$host', port=$port)"
    }
}