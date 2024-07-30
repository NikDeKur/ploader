/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.command.rcon;

data class RCONPacket(
    val size: Int,
    val requestId: Int,
    val responseId: Int,
    val type: RequestType,
    val payload: ByteArray,
) {
    enum class RequestType(val id: Int) {
        COMMAND_RESPONSE(0),
        COMMAND(2),
        LOGIN(3);



        companion object {
            val ids = arrayOfNulls<RequestType>(5)

            init {
                RequestType.entries.forEach { ids[it.id] = it }
            }

            fun getById(id: Int) = ids[id] ?: error("Unknown RequestType id: $id")
        }
    }
}