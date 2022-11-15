package io.doorapp.door

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

class SignUpFragment : Fragment(), View.OnClickListener {
    private var userDob: Triple<String, String, String>? = null
    //    buttons
    private var backButton: ImageButton? = null
    private var nextButton: ImageButton? = null
    private var phoneToEmailButton: Button? = null
    private var emailToPhoneButton: Button? = null
    //    entries
    private var phoneEmailEntry: EditText? = null
    private var dobEntry: EditText? = null
    private var nameEntry: EditText? = null
    private var nameFilledOut = false
    private var phoneFilledOut = false
    //  entry error
    private var nameError: TextView? = null
    private var phoneEmailError: TextView? = null
    private var dobError: TextView? = null
    private var verificationReady = false
    //    cal fragment
    private var selectedMonth: String? = null
    private var selectedDay: String? = null
    private var selectedYear: String? = null
    private var calFrag: FragmentContainerView? = null
    //    viewmodel for data
//    private lateinit var userData: SignUpViewModel
//    private lateinit var calendar: CalendarViewModel

    private val calendar: CalendarViewModel by activityViewModels()
    private val userData: SignUpViewModel by activityViewModels()




    private lateinit var countryPrefix: CountryCodePrefix
    @RequiresApi(Build.VERSION_CODES.N)
    private var countryCode: String? = null
    private var phoneUtil = PhoneNumberUtil.getInstance()
    //    general
    private lateinit var codeHelpers: MyCodeHelpers

    var navController: NavController? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        init(view)
        configStartPos()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
//    @RequiresApi(Build.VERSION_CODES.N)
    private fun init(view: View){
        codeHelpers = MyCodeHelpers()
//button
        backButton = view.findViewById(R.id.backButton)
        nextButton = view.findViewById(R.id.nextButton)
        phoneToEmailButton = view.findViewById(R.id.useEmailButton)
        emailToPhoneButton = view.findViewById(R.id.usePhoneButton)
//button listener
        backButton!!.setOnClickListener(this)
        nextButton!!.setOnClickListener(this)
        phoneToEmailButton!!.setOnClickListener(this)
        emailToPhoneButton!!.setOnClickListener(this)
// entries
        phoneEmailEntry = view.findViewById(R.id.phoneEmailEntry)
        dobEntry = view.findViewById(R.id.dobEntry)
        nameEntry = view.findViewById(R.id.nameEntry)
//error textViews
        nameError = view.findViewById(R.id.nameError)
        phoneEmailError = view.findViewById(R.id.phoneEmailError)
        dobError = view.findViewById(R.id.dobError)
//calendar frag
        calFrag = view.findViewById(R.id.calendarFragmentContainer)
        getCalendarData()
//update view model live data

//lose focus listner
        nameEntry!!.setLoseFocusListener { name(true) }
        phoneEmailEntry!!.setLoseFocusListener { phoneEmail(true) }
//text change listener

        nameEntry!!.setEditTextChangeListener({},{},{name(false)})
        phoneEmailEntry!!.setEditTextChangeListener({ phoneEmail(true)}, { println()}, {
            val startSelection = phoneEmailEntry!!.selectionStart
            val endSelection = phoneEmailEntry!!.selectionEnd
            if (startSelection == endSelection && startSelection == phoneEmailEntry!!.text.length){
                phoneEmailEntry!!.formatPhone(it, true, startSelection, endSelection)
            }else{
                phoneEmailEntry!!.formatPhone(it, false,startSelection , endSelection)
            }
        })
//edit keyboard listener
        phoneEmailEntry!!.setKeyboardButtonListener( { useDobState() }, {EditorInfo.IME_ACTION_DONE})
//touch listeners
        nameEntry!!.addTouchListener({ name(false)}, {useNameEntryState()})
        phoneEmailEntry!!.addTouchListener({phoneEmail(false)},
            {if (userData.liveUsingEmail.value!!) useEmailState() else usePhoneState()}, {showCalendarFrag(false)})
        dobEntry!!.addTouchListener({ dob(false)}, {useDobState()})
//phone
        countryPrefix = CountryCodePrefix()
        countryCode = this.resources.configuration.locales[0].country
    }


    private fun configStartPos(){
        nameEntry!!.doOnPreDraw {
            clearDobError()
            clearNameError()
            nameEntry!!.setText(userData.liveUserFirstName.value!!.toString())
            if (userData.liveUsingEmail.value!!){
                phoneEmailEntry!!.setText(userData.liveUsersEmail.value!!.toString())
            }else{
                phoneEmailEntry!!.setText(userData.liveFormattedUsersPhone.value!!.toString())
                clearEmailPhoneError()
            }
        }
    }


    /**gets data from the calendar fragment*/
    private fun getCalendarData(){
        calendar.liveDobFormatted.observe(viewLifecycleOwner){
            dobEntry!!.setText(it)
        }
        calendar.liveTimeTravel.observe(viewLifecycleOwner){
            dob(true)
        }
    }


    /**if all fields are corrent can move to next page*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun readyForVerification(){
        if (userData.liveSignupReady.value!!){
            navController!!.navigate(R.id.action_signUpFragment_to_verifySignUpFragment)
        }
    }
    /**checks name is valid*/
    private fun name(provideErrorMessage: Boolean): Boolean{
        val nameEmpty = TextUtils.isEmpty(nameEntry!!.text.toString().trim())
        if (nameEmpty){
            nameFilledOut = false
            if(provideErrorMessage) {
                val errorText = "Name cannot be empty"
                nameError!!.text = errorText
                return false
            }
            allInputValid()
            return false
        }else{
            clearNameError()
            userData.triggerLiveName(nameEntry!!.text.toString().trim())
            nameFilledOut = true
            allInputValid()
            return true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun phoneEmail(raiseError: Boolean) {
        if (userData.liveUsingEmail.value!!){
            phoneFilledOut = phoneEmailValid(raiseError, {emailEmptyError()}, {validEmail()}, {invalidEmailError()})
            allInputValid()
        }else{
            phoneFilledOut = phoneEmailValid(raiseError, {phoneEmptyError()}, {validPhone()}, {invalidPhoneError()})
            allInputValid()
        }
    }
    /**checks if email/phone is valid*/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun phoneEmailValid(raiseError: Boolean, emptyError:()-> Unit ,
                                validInput:() -> Boolean, invalidInputError:() -> Unit): Boolean{
        when (phoneEmailEmpty()) {
            true -> {
                if (raiseError) {
                    emptyError()
                    return false
                } else {
                    clearEmailPhoneError()
                    return false
                }
            }
            false -> {
                clearEmailPhoneError()
                when (!validInput()) {
                    true -> {
                        if (raiseError) {
                            invalidInputError()
                            return false
                        } else {
                            clearEmailPhoneError()
                            return false
                        }
                    }
                    false -> {
                        clearEmailPhoneError()
                        return true
                    }
                }
            }
        }
    }

    private fun dob(provideErrorMessage: Boolean): Boolean{
        if (calendar.liveTimeTravel.value == true){
            if (provideErrorMessage){
                val errorText = "You cannot time travel"
                allInputValid()
                dobError!!.text = errorText
                return false
            }
            return false
        }else{
            clearDobError()
            allInputValid()
            return true
        }
    }
    /**lights up button if valid*/
    private fun allInputValid() {
        if (nameFilledOut && phoneFilledOut && calendar.liveTimeTravel.value == false){
            nextButton!!.alpha = 1f
            userData.triggerLiveSignupReady(true)
        }else{
            nextButton!!.alpha = 0.2f
            userData.triggerLiveSignupReady(false)
        }
    }
    /**sets touch listeners for all the things where you can input the states you perfer*/
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun EditText.addTouchListener(vararg fun1:() -> Any){
        this.setOnTouchListener { _: View, event: MotionEvent ->
            if (event.action == event.action) {
                fun1.forEach {
                    it()
                }
            }
            false
        }
    }
    /**deals with the buttons and their states*/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        val itemId = v.id
        when (itemId) {
            R.id.backButton -> navController!!.navigate(R.id.action_signUpFragment_to_loginChoicesFragment)
            R.id.nextButton -> {
                readyForVerification()
            }
            R.id.useEmailButton -> {
                useEmailState()
                phoneEmailEntry!!.text.clear()
            }
            R.id.usePhoneButton -> {
                usePhoneState()
                phoneEmailEntry!!.text.clear()
            }
        }
    }
    /**handels when text is edited in real time*/
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
    /**deals with editText when it loses focus*/
    private fun EditText.setLoseFocusListener(fun1: () -> Unit){
        this.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                fun1()
            }
        }
    }

    /**listens for edit text keyboard ok push then changes the state*/
    private fun EditText.setKeyboardButtonListener(fun1: () -> Unit, fun2: () -> Int) {
        this.setOnEditorActionListener { v, actionId, event ->
            if(actionId == fun2()){
                fun1()
                true
            } else {
                false
            }
        }
    }
    private fun useNameEntryState(){
        phoneToEmailButton!!.visibility = View.GONE
        emailToPhoneButton!!.visibility = View.GONE
        showCalendarFrag(false)
        nameEntry!!.isCursorVisible = true
        phoneEmailEntry!!.isCursorVisible = true
    }
    private fun useDobState(){
        phoneToEmailButton!!.visibility = View.GONE
        emailToPhoneButton!!.visibility = View.GONE
        showCalendarFrag(true)

        nameEntry!!.isCursorVisible = false
        phoneEmailEntry!!.isCursorVisible = false
        phoneEmailEntry!!.clearFocus()
        nameEntry!!.clearFocus()
        hideKeyboardFrom(requireContext(), dobEntry!!)
    }
    private fun useEmailState(){
        phoneEmailEntry!!.hint = "Email"
        userData.triggerSignUpWithEmail(true)
        phoneEmailEntry!!.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailToPhoneButton!!.visibility = View.VISIBLE
        phoneToEmailButton!!.visibility = View.GONE
        nameEntry!!.isCursorVisible = true
        phoneEmailEntry!!.isCursorVisible = true

        val errorText = ""
        phoneEmailError!!.text = errorText
        showCalendarFrag(false)
    }
    private fun usePhoneState(){

        phoneEmailEntry!!.hint = "Phone"
        userData.triggerSignUpWithEmail(false)
        phoneEmailEntry!!.inputType = InputType.TYPE_CLASS_PHONE
        emailToPhoneButton!!.visibility = View.GONE
        phoneToEmailButton!!.visibility = View.VISIBLE
        nameEntry!!.isCursorVisible = true
        phoneEmailEntry!!.isCursorVisible = true
        val errorText = ""
        phoneEmailError!!.text = errorText
        showCalendarFrag(false)
    }
    /**handles showing or hiding calendar*/
    private fun showCalendarFrag(b: Boolean){
        if (b) calFrag!!.visibility = View.VISIBLE else calFrag!!.visibility = View.GONE
    }
    /**shows or hides keyboard*/
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun showKeyboard(context: Context, view: View) {
        val methodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**handles phone and email errors*/
    private fun phoneEmailEmpty(): Boolean{
        return phoneEmailEntry!!.text.toString().isEmpty()
    }
    private fun phoneEmptyError(){
        val errorText = "Phone cannot be empty"
        phoneEmailError!!.text = errorText
    }
    private fun clearEmailPhoneError(){
        val errorText = ""
        phoneEmailError!!.text = errorText
    }
    private fun clearDobError(){
        val errorText = ""
        dobError!!.text = errorText
    }
    private fun clearNameError(){
        val errorText = ""
        nameError!!.text = errorText
    }
    private fun emailEmptyError(){
        val errorText = "Email cannot be empty"
        phoneEmailError!!.text = errorText
    }
    private fun invalidEmailError(){
        val errorText = "Must enter in a valid email"
        phoneEmailError!!.text = errorText
    }
    private fun invalidPhoneError(){
        val errorText = "Must enter in a valid phone"
        phoneEmailError!!.text = errorText
    }
    /**checks if valid email*/
    private fun validEmail(): Boolean{
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(phoneEmailEntry!!.text.toString().trim()).matches()) {
            userData.triggerLiveEmail(phoneEmailEntry!!.text.toString().trim())
            return true
        }
        return false
    }
    /**checks if valid phone*/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun validPhone(): Boolean {
        val phoneObj = createPhoneObj()
        if (phoneObj != null){
            phoneObj as Phonenumber.PhoneNumber

            if (phoneUtil.isValidNumber(phoneObj)){
                return true
            }

        }
        return false
    }
    /**formats the phone number object and updates the edit text. Posts cleaned phone to viewModel*/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun EditText.formatPhone(textWatcher: TextWatcher, moveCursorEnd: Boolean, start: Int, end: Int){
        val phoneObj = createPhoneObj()
        if (phoneObj != null){
            phoneObj as Phonenumber.PhoneNumber
            val phoneLength = {num: String -> num != "" && num.length >= 10 }
            val formattedPhoneNumber = PhoneNumberUtils.formatNumber(setPhonePrefix(), getPhoneIso().toString()).toString()
            userData.triggerLiveFormattedPhone(formattedPhoneNumber)
            val onlyPhoneNumber = phoneObj.nationalNumber.toString()
            if(phoneLength(onlyPhoneNumber)) userData.triggerLivePhone(onlyPhoneNumber)
            phoneEmailEntry!!.removeTextChangedListener(textWatcher)
            this.setText(PhoneNumberUtils.formatNumber(setPhonePrefix(), getPhoneIso().toString()))
            if (moveCursorEnd){
                phoneEmailEntry!!.setSelection(phoneEmailEntry!!.text.length)
            }else{
                phoneEmailEntry!!.setSelection(start, end)
            }
            phoneEmailEntry!!.addTextChangedListener(textWatcher)
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createPhoneObj(): Any?{
        try {
            val phoneObj = phoneUtil.parse(setPhonePrefix(), countryCode!!)
            return phoneObj
        }
        catch (e: NumberParseException){}
        return null
    }
    /**gets the phones country code then adds it to the number*/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPhonePrefix(): String{
        val countryCodePrefix = countryPrefix.getPhone(countryCode!!)
        val phone = phoneEmailEntry!!.text.toString().trim()
        return if (phone.startsWith("+")) phone else "$countryCodePrefix$phone"
    }
    /**gets the phones iso*/
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getPhoneIso(): Int{
        val phoneUtil = PhoneNumberUtil.getInstance()
        return  phoneUtil.getCountryCodeForRegion(countryCode)
    }

}