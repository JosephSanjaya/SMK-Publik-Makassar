package com.smk.publik.makassar.presentation.activities.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class AccountSharedViewModel: ViewModel() {

    val mUsers = MutableLiveData<FirebaseUser>()

}