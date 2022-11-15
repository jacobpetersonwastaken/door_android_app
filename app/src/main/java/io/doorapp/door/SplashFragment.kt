package io.doorapp.door

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var codeHelpers: MyCodeHelpers
    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeHelpers = MyCodeHelpers()
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_splash, container, false)




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }



    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null){
            currentUser.delete()
            Handler(Looper.myLooper()!!).postDelayed({
                navController!!.navigate(R.id.action_splashFragment_to_wallFragment)
            }, 1000)
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                navController!!.navigate(R.id.action_splashFragment_to_greetingFragment)
            }, 1000)
        }
    }




}