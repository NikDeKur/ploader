/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.config

import dev.nikdekur.ploader.action.Action
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

@Serializable
sealed class PLoaderActionConfig : Action {

    @Serializable
    @SerialName("upload")
    data class Upload(
        @Serializable(with = FileSerializer::class)
        override val source: File,

        @Serializable(with = FileSerializer::class)
        override val destination: File,

        override val overwrite: Boolean = true
    ) : PLoaderActionConfig(), Action.Upload


    @Serializable
    @SerialName("remove")
    data class Remove(
        @Serializable(with = FileSerializer::class)
        override val destination: File
    ) : PLoaderActionConfig(), Action.Remove


    @Serializable
    @SerialName("command")
    data class Command(
        override val command: String
    ) : PLoaderActionConfig(), Action.Command
}







object FileSerializer : KSerializer<File> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("File", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: File) {
        encoder.encodeString(value.absolutePath)
    }

    override fun deserialize(decoder: Decoder): File {
        return File(decoder.decodeString())
    }
}