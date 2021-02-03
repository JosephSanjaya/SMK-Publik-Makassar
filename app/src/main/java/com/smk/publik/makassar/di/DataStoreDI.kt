package com.smk.publik.makassar.di

import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.smk.publik.makassar.data.datastore.DataStoreContainer
import com.smk.publik.makassar.presentation.viewmodel.DataStoreViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


/**
 * @Author Joseph Sanjaya, S.T., M.Kom. on 31,January,2021
 * @Github (https://github.com/JosephSanjaya),
 * @LinkedIn (https://www.linkedin.com/in/josephsanjaya/))
 */

object DataStoreDI  {

    private const val USER_KEYSET_NAME = "master_keyset"
    private const val USER_PREFERENCE_FILE = "master_key_preference"
    private const val USER_MASTER_KEY_URI = "android-keystore://master_key"
    private const val DATASTORE_FILE = "user.pb"

    val modules = module {
        single<Aead> {
            AeadConfig.register()
            AndroidKeysetManager.Builder()
                .withSharedPref(androidApplication(), USER_KEYSET_NAME, USER_PREFERENCE_FILE)
                .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
                .withMasterKeyUri(USER_MASTER_KEY_URI)
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)
        }
        single {
            DataStoreContainer(androidApplication(), get())
        }
        single {
            DataStoreViewModel(get())
        }
    }
}