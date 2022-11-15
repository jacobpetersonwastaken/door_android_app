package io.doorapp.door

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginChoicesFragment : Fragment(), View.OnClickListener {
    private var doorDrawAnimationView: LottieAnimationView? = null
    private var loginButton: Button? = null
    private var createAccountButton: Button? = null
    private var theWallButton: LinearLayout? = null
    private var loginWithGoogleButton: LinearLayout? = null
    private var listPageViews = mutableListOf<Any>()
    private lateinit var codeHelpers: MyCodeHelpers
    private lateinit var auth: FirebaseAuth
    lateinit var navController: NavController



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_choices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        init(view)

    }

    private fun init(view: View){
        loginButton = view.findViewById(R.id.loginButton)
        createAccountButton = view.findViewById(R.id.createAccountButton)
        loginWithGoogleButton = view.findViewById(R.id.loginWithGoogleButton)
        theWallButton = view.findViewById(R.id.theWallButton)
        doorDrawAnimationView = view.findViewById(R.id.doorDraw)
        listPageViews.addAll(0, arrayListOf(loginButton!!, createAccountButton!!, loginWithGoogleButton!!, theWallButton!!))
        codeHelpers = MyCodeHelpers()
        setOnInteractionListeners()
        auth = Firebase.auth
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnInteractionListeners() {
        loginButton!!.setOnClickListener(this)
        createAccountButton!!.setOnClickListener(this)
        loginWithGoogleButton!!.setOnClickListener(this)
        theWallButton!!.setOnClickListener(this)
        codeHelpers.nonButtonTouchEffect(loginWithGoogleButton!!)
        codeHelpers.nonButtonTouchEffect(theWallButton!!)
    }

    private fun transitionEffect(v: View, nextPage: ()-> Unit){
        doorDrawAnimationView!!.playAnimation()
        listPageViews.forEach {
            it as View
            codeHelpers.fadeAnimationButton(1.0f,0.0f, 500, 0, true, it)
        }
        codeHelpers.animationProgress({ nextPage() }, doorDrawAnimationView!!)

    }


    override fun onClick(v: View) {
        val itemId = v.id
        when (itemId) {
            R.id.loginButton -> transitionEffect(loginButton!!, {navController.navigate(R.id.action_loginChoicesFragment_to_signInFragment) })
            R.id.createAccountButton ->  transitionEffect(createAccountButton!!, {navController.navigate(R.id.action_loginChoicesFragment_to_signUpFragment) })
            R.id.loginWithGoogleButton -> transitionEffect(loginWithGoogleButton!!, {navController.navigate(R.id.action_loginChoicesFragment_to_googleSignInFragment) })
            R.id.theWallButton -> transitionEffect(theWallButton!!, {navController.navigate(R.id.action_loginChoicesFragment_to_wallFragment) })
        }
    }


}