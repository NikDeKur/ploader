/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ploader.action

interface Action {
    interface Upload {
        val source: String
        val destination: String
        val overwrite: Boolean
    }

    interface Remove {
        val destination: String
    }

    interface Command {
        val command: String
    }
}