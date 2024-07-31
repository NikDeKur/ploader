/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.files

interface FilesService {

    suspend fun start()
    suspend fun stop()

    suspend fun put(local: String, remote: String, overwrite: Boolean)
    suspend fun get(local: String, remote: String, overwrite: Boolean)
    suspend fun remove(remote: String)
}