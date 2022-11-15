package io.doorapp.door

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class createUsernameFragment : Fragment() {
    private lateinit var auth: FirebaseAuth




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_username, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        auth = Firebase.auth

        checkIfEmailVerified()
    }


    private fun checkIfEmailVerified() {
        val user = auth.currentUser
        if (user!!.isEmailVerified) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            Toast.makeText(requireContext(), "email did get verified.", Toast.LENGTH_SHORT).show()
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(requireContext(), "email is not verified.", Toast.LENGTH_SHORT).show()
//            FirebaseAuth.getInstance().signOut()

            //restart this activity
        }
    }

}