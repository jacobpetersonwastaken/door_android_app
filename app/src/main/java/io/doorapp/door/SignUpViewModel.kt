package io.doorapp.door

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel: ViewModel() {



    private val userFirstName = MutableLiveData("")
    val liveUserFirstName: LiveData<String> = userFirstName

    private val usersEmail = MutableLiveData("")
    val liveUsersEmail: LiveData<String> = usersEmail

    private val usersPhone = MutableLiveData("")
    val liveUsersPhone: LiveData<String> = usersPhone

    private val usersPassword = MutableLiveData("")
    val liveUsersPassword: LiveData<String> = usersPassword

    private val userPasswordGood = MutableLiveData(false)
    val liveUsersPasswordGood = userPasswordGood

    private val formattedUsersPhone = MutableLiveData("")
    val liveFormattedUsersPhone: LiveData<String> = formattedUsersPhone

    private val usingEmail = MutableLiveData(false)
    val liveUsingEmail: LiveData<Boolean> = usingEmail

    private val signupReady = MutableLiveData(false)
    val liveSignupReady = signupReady


    private val verificationReady = MutableLiveData(false)
    val liveVerificationReady = verificationReady



    fun triggerLiveVerificationReady (bool: Boolean){
        verificationReady.value = bool
    }

    fun triggerLiveUsersPassword(password: String){
        usersPassword.value = password
    }

    fun triggerLiveUsersPasswordGood(bool: Boolean){
        userPasswordGood.value = bool
    }



    fun triggerLiveSignupReady(bool: Boolean){
        signupReady.value = bool
    }

    fun triggerLiveName(name: String){
        userFirstName.value = name
    }
    fun triggerLiveEmail(email: String){
        usersEmail.value = email
    }
    fun triggerLivePhone(phone: String){
        usersPhone.value = phone
    }
    fun triggerLiveFormattedPhone(phone: String){
        formattedUsersPhone.value = phone
    }

    fun triggerSignUpWithEmail(bool: Boolean){
        usingEmail.value = bool
    }




}