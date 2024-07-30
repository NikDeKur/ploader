/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.command

interface CommandService {
    suspend fun start()
    suspend fun stop()

    suspend fun executeCommand(command: String)


    object NOOP : CommandService {
        override suspend fun start() {
            // NOOP
        }

        override suspend fun stop() {
            // NOOP
        }

        override suspend fun executeCommand(command: String) {
            // NOOP
        }
    }
}