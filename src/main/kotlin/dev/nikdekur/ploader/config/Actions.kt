/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.config

import dev.nikdekur.ploader.action.Action
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PLoaderActionConfig : Action {

    @Serializable
    @SerialName("upload")
    data class Upload(
        override val source: String,
        override val destination: String,
        override val overwrite: Boolean = true,
    ) : PLoaderActionConfig(), Action.Upload


    @Serializable
    @SerialName("remove")
    data class Remove(
        override val destination: String,
    ) : PLoaderActionConfig(), Action.Remove


    @Serializable
    @SerialName("command")
    data class Command(
        override val command: String
    ) : PLoaderActionConfig(), Action.Command
}

