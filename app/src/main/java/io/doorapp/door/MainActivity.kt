package io.doorapp.door

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import java.net.URI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//
//        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
////            add in link handeling code here. it was successful
//            Log.i("main activity", "we havew a dynamic link")
//            var deepLink: Uri? = null
//            if (it != null){
//                deepLink = it.link
//            }
//            if (deepLink != null){
//                Log.i("mainActivity", "here is the deep link url\n" +
//                        "$deepLink")
//            }
//        }


    }
}