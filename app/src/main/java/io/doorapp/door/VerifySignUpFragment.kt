package io.doorapp.door

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class VerifySignUpFragment : Fragment(), View.OnClickListener {

    private var navController: NavController? = null
    private lateinit var codeHelpers: MyCodeHelpers
//    private lateinit var userData: SignUpViewModel
    private var phoneEmailEntry: EditText? = null
    private lateinit var auth: FirebaseAuth
    private var submitButton: Button? = null
    private var backButton: ImageButton? = null
    private var phoneEmailEditTextButton:Button? = null
    private var passwordError: TextView? = null
    private var editTextPassword: EditText? = null
    private val userData: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(view: View){
        codeHelpers = MyCodeHelpers()
        phoneEmailEntry = view.findViewById(R.id.phoneEmailEntry)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        passwordError = view.findViewById(R.id.passwordError)

        backButton = view.findViewById(R.id.backButton)
        submitButton = view.findViewById(R.id.submit)
        phoneEmailEditTextButton = view.findViewById(R.id.phoneEmailEditTextButton)

        backButton!!.setOnClickListener(this)
        submitButton!!.setOnClickListener(this)
        phoneEmailEditTextButton!!.setOnClickListener(this)

        editTextPassword!!.setLoseFocusListener { verifyPassword(true) }
        editTextPassword!!.setEditTextChangeListener({},{},{verifyPassword(false)})

        subscribeToSignUpViewModel()
        auth = Firebase.auth

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backButton -> navController!!.navigate(R.id.action_verifySignUpFragment_to_signUpFragment)
            R.id.phoneEmailEditTextButton -> {
                if (editTextPassword!!.isFocused){
                    editTextPassword!!.clearFocus()
                    hideKeyboardFrom(requireContext(), editTextPassword!!)

                }else{
                    navController!!.navigate(io.doorapp.door.R.id.action_verifySignUpFragment_to_signUpFragment)
                }

            }
            R.id.submit -> {
                verifyIsReady()
                hideKeyboardFrom(requireContext(), editTextPassword!!)
                submitButtonStateReady(false)
            }
        }
    }
    private fun subscribeToSignUpViewModel(){
        if (userData.liveUsingEmail.value!!){
            userData.liveUsersEmail.observe(viewLifecycleOwner){
                phoneEmailEntry!!.setText(it)
            }
        }else{
            userData.liveFormattedUsersPhone.observe(viewLifecycleOwner){
                phoneEmailEntry!!.setText(it)
            }
        }
    }

    private fun verifyIsReady(){
        if (userData.liveVerificationReady.value!!){
            submitVerification()
        }
    }
    private fun submitVerification(){
        if (userData.liveUsingEmail.value!!){
            createAccountWithEmail()
        }else{
            verifyPhone()
        }
    }
    private fun verifyPassword(provideError: Boolean){
        val password = editTextPassword!!.text.toString().trim()
        clearPasswordError()
        if (password.isEmpty()){
            if (provideError) setPasswordEmpyError()
            submitButtonStateReady(false)
            userData.triggerLiveVerificationReady(false)
            userData.triggerLiveUsersPasswordGood(false)
        }else{
            if (password.length < 6){
                submitButtonStateReady(false)
                userData.triggerLiveVerificationReady(false)
                userData.triggerLiveUsersPasswordGood(false)
                if (provideError) setPasswordNotValidError()
            }else{
                clearPasswordError()
                submitButtonStateReady(true)
                userData.triggerLiveVerificationReady(true)
                userData.triggerLiveUsersPassword(password)
                userData.triggerLiveUsersPasswordGood(true)
            }
        }
    }

    private fun submitButtonStateReady(boolean: Boolean){
        if (boolean){
            submitButton!!.alpha = 1f
            submitButton!!.isClickable = true

        }else{
            submitButton!!.alpha = 0.2f
            submitButton!!.isClickable = false
        }
    }

    private fun setPasswordNotValidError(){
        val error = "Password must be at least 6 characters"
        passwordError!!.text = error
    }

    private fun setPasswordEmpyError(){
        val error = "Password cannot be empty"
        passwordError!!.text = error
    }
    private fun clearPasswordError() {
        val error = ""
        passwordError!!.text = error

    }

    private fun createAccountWithEmail() {
        val email = userData.liveUsersEmail.value!!
        auth.createUserWithEmailAndPassword(email, userData.liveUsersPassword.value!!)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendVerificationEmail()

            }
        }
    }

    private fun sendVerificationEmail(){

        val user = FirebaseAuth.getInstance().currentUser


        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://dynamiclinks.doorapp.io/verify")
            .setIOSBundleId("com.example.ios")
            // The default for this is populated with the current android package name.
            .setAndroidPackageName("io.doorapp.door", false, null)
            .build()

        user!!.sendEmailVerification(actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "email sent", Toast.LENGTH_SHORT).show()
                } else {

                }
            }
    }

    private fun verifyPhone(){

    }

    private fun EditText.setLoseFocusListener(fun1: () -> Unit){
        this.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                fun1()
            }
        }
    }

    private fun EditText.setEditTextChangeListener(afterText: (TextWatcher) -> Unit, beforeText: (TextWatcher) -> Unit, onText: (TextWatcher) -> Unit){
        this.addTextChangedListener(object: TextWatcher {
            val t = this
            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(s: Editable?) {
                afterText(t)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { beforeText(t)}
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onText(t)
            }
        })
    }
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
