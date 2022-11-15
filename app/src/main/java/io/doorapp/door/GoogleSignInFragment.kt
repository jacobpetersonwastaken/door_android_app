package io.doorapp.door

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth


class GoogleSignInFragment : Fragment(), View.OnClickListener {

    private var navController: NavController? = null
    private lateinit var codeHelpers: MyCodeHelpers


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        init(view)

    }


    private fun init(view: View){
        codeHelpers = MyCodeHelpers()
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener(this)



    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.backButton -> navController!!.navigate(R.id.action_googleSignInFragment_to_loginChoicesFragment)

        }
    }
}
