package io.doorapp.door

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.airbnb.lottie.LottieAnimationView


class MyCodeHelpers() {


    /**Animation helpers
     * */
    fun waiting(min: Int, max: Int, animationObj: LottieAnimationView) {
        val waitTime = ((min..max).random() * 1000).toLong()
        Handler(Looper.getMainLooper()).postDelayed({
            animationObj.playAnimation()
        }, waitTime)
    }

    fun doorJiggle(min: Int, max: Int,animationObj: LottieAnimationView){
        animationObj.addAnimatorUpdateListener {valueAnimator ->
            val progress = (valueAnimator.animatedValue as Float * 100).toInt()
            if (progress >= 98) {
                animationObj.pauseAnimation()
                waiting(min, max, animationObj)

            }
        }
    }
//checks if the progress is done if yes then it will go to the next page
    fun animationProgress(nextPage: ()-> Unit, animationObj: LottieAnimationView){
        animationObj.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
//                secondAnimation.playAnimation()
                nextPage()
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })

    }

    fun fadeAnimationButton(start: Float,end: Float,duration: Long,startAfter: Long,
                            resetAfter: Boolean, v: View
    ) {
        val fadeAnimation = AlphaAnimation(start, end)
        if (duration > 0){
            fadeAnimation.duration = duration
        }

        fadeAnimation.startOffset = startAfter
        fadeAnimation.fillAfter = resetAfter
        v.startAnimation(fadeAnimation)

        var buttonDrawable = v.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable!!)
        DrawableCompat.setTint(buttonDrawable, Color.WHITE)
        v.setBackground(buttonDrawable)

    }
    /**for non buttons lights up background when touched*/
    @SuppressLint("ClickableViewAccessibility")
    fun nonButtonTouchEffect(v: View){
        v.setOnTouchListener { view, motionEvent ->
            fadeAnimationButton(1.0f,1.0f, 0, 0, true, v)
            false
        }
    }

/**general use*/
//    nextPage
fun nextPage(page: AppCompatActivity, context: Context) {
    val intent = Intent(context, page::class.java)
    context.startActivity(intent)
    page.finish()


}




}
