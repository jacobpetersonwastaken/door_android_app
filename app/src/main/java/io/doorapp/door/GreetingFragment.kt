package io.doorapp.door

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView

class GreetingFragment : Fragment(), View.OnClickListener {

    private var navController: NavController? = null
    private lateinit var codeHelpers: MyCodeHelpers
    private var doorOpen: LottieAnimationView? = null
    private var doorJitterAnimation: LottieAnimationView? = null
    private var doorButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_greeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        init(view)
    }

    private fun init(view: View){
        codeHelpers = MyCodeHelpers()
        doorOpen = view.findViewById(R.id.doorOpen)
        doorJitterAnimation = view.findViewById(R.id.doorJitter)
        doorButton = view.findViewById(R.id.greetingButton)
        doorButton!!.setOnClickListener(this)
        doorJiggle()
    }

    private fun doorJiggle(){
        codeHelpers.doorJiggle(1, 6, doorJitterAnimation!!)
    }
    private fun doorOpen(nextPage: () -> Unit){
        doorJitterAnimation!!.setImageDrawable(null)
        doorJitterAnimation!!.pauseAnimation()
        doorJitterAnimation!!.removeAllUpdateListeners()
        doorOpen!!.playAnimation()
        codeHelpers.animationProgress({ nextPage()},doorOpen!!)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            doorButton!!.id -> {doorOpen { navController!!.navigate(R.id.action_greetingFragment_to_loginChoicesFragment) }}

        }
    }
}