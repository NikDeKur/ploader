/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.config

import kotlinx.serialization.Serializable

@Serializable
data class PLoaderSFTPConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String
)
