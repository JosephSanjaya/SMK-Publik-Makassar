package com.smk.publik.makassar.account.domain

/*
 * Copyright (c) 2021 Designed and developed by Joseph Sanjaya, S.T., M.Kom., All Rights Reserved.
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

data class Password(
    var drawable: Int,
    var label: String,
    var status: Boolean
) {
    companion object {
        const val MSG_LOGIN_FIRST = "Silahkan login terlebih dahulu"
    }
}