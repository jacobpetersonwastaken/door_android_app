package io.doorapp.door

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import java.lang.NumberFormatException
import java.time.LocalDate
import java.time.YearMonth


class CalendarFragment : Fragment(){

    private var calendarsCreated:Int = 1
    //    month
    private var monthScrollWheel:HorizontalScrollView? = null
    private var monthLinearLayout:LinearLayout? = null
    private var monthTextViews = mutableListOf<TextView>()
    //    day
    private var dayScrollWheel: HorizontalScrollView? = null
    private var dayLinearLayout: LinearLayout? = null
    private var dayTextViews = mutableListOf<TextView>()
    //    year
    private var yearScrollWheel: HorizontalScrollView? = null
    private var yearLinearLayout: LinearLayout? = null
    private var yearTextViews = mutableListOf<TextView>()

    private val calendar: CalendarViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater,parent: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_calendar, parent, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        initViews(view)
        initAllStartTextView()
        configScrollStartPos()
        horizScrollChange()

    }

    /**Init all of the views and creates the starting textviews*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews(view: View){
//        month
        monthScrollWheel = view.findViewById(R.id.monthScroll)
        monthLinearLayout = view.findViewById(R.id.monthLinearLayout)
//      day
        dayScrollWheel = view.findViewById(R.id.dayScroll)
        dayLinearLayout = view.findViewById(R.id.dayLinearLayout)
//      year
        yearScrollWheel = view.findViewById(R.id.yearScroll)
        yearLinearLayout = view.findViewById(R.id.yearLinearLayout)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAllStartTextView() {
        createMonths(calendarsCreated, calendar.months, calendar.currentMonth)
        createDays(calendarsCreated, calendar.months, calendar.currentMonth)
        createYears()
    }
    /**before view is drawn we set the position of the scroll to current month
     * function called in initViews function after all the views are found*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun configScrollStartPos(){
        Log.e("cal frag", "cal frag was loaded")
//        year stays because it matches the set
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val middleScreen = screenWidth / 2

        monthLinearLayout!!.doOnPreDraw {
            monthTextViews.forEach {
                if (calendar.liveMonthTag.value!!.toInt() == it.tag.toString().toInt()){
                    val monthView = it
                    val monthViewTag = monthView.tag.toString()
                    val theMonthView = monthView.findViewWithTag<TextView>(monthViewTag)
                    val absoluteMonthX = monthView.absX()
                    monthScrollWheel!!.scrollTo(absoluteMonthX - middleScreen + (theMonthView.width / 2), 0)

                }
            }
        }
        dayLinearLayout!!.doOnPreDraw {
            dayTextViews.forEach {
                if (calendar.liveJustDayOfYear.value!!.toInt() == it.tag.toString().split(" ").first().toInt()){
                    val dayView = it
                    val dayViewTag = dayView.tag.toString()
                    val theDayView = dayView.findViewWithTag<TextView>(dayViewTag)
                    val absoluteDayX = dayView.absX()
                    dayScrollWheel!!.scrollTo(absoluteDayX - middleScreen + (theDayView.width / 2), 0)
                }
            }
        }
        yearLinearLayout!!.doOnPreDraw {
            yearTextViews.forEach {
                if (calendar.liveYearTag.value!!.toInt() == it.tag.toString().toInt()){
                    val yearView = it
                    val yearViewTag = yearView.tag.toString()
                    val theYearView = yearView.findViewWithTag<TextView>(yearViewTag)
                    val absoluteYearX = yearView.absX()
                    yearScrollWheel!!.scrollTo(absoluteYearX - middleScreen + (theYearView.width / 2), 0)
                }
            }
        }

    }
    /**checks when scroll has been changed*/
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun horizScrollChange(){
        val scrollBounds = Rect()
        val middleScreen = Resources.getSystem().displayMetrics.widthPixels / 2

        monthScrollWheel!!.setOnTouchListener { view, motionEvent ->
            monthScrollWheel!!.addViewObserverMonth {
                month(monthScrollWheel!!, middleScreen, scrollBounds)
            }
            false
        }
        dayScrollWheel!!.setOnTouchListener { view, motionEvent ->
            dayScrollWheel!!.addViewObserver {
                day(dayScrollWheel!!,middleScreen, scrollBounds)
            }
            false
        }
        yearScrollWheel!!.setOnTouchListener { view, motionEvent ->
            yearScrollWheel!!.addViewObserver {
                year(yearScrollWheel!!,middleScreen, scrollBounds)
            }
            false
        }
    }
    private fun View.addViewObserver(function: () -> Unit) {
        val view = this
        view.viewTreeObserver.addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
            override fun onScrollChanged() {
                function.invoke()
                val handler = Handler()
                handler.postDelayed({
                    view.viewTreeObserver.removeOnScrollChangedListener(this)
                }, 1000)
            }
        })
    }
    /**this has less of a delay because it fixes the bug.*/
    private fun View.addViewObserverMonth(function: () -> Unit) {
        val view = this
        view.viewTreeObserver.addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
            override fun onScrollChanged() {
                function.invoke()
                val handler = Handler()
                handler.postDelayed({
                    view.viewTreeObserver.removeOnScrollChangedListener(this)
                }, 300)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun month(horizontalScrollView: HorizontalScrollView, middleScreen: Int, scrollBounds: Rect){
        horizontalScrollView.getDrawingRect(scrollBounds)
        for (i in monthTextViews) {
            val monthViewTag = i.tag.toString()
            val theMonthView = i.findViewWithTag<TextView>(monthViewTag)
            val monthAbsoluteX = theMonthView.absX()
            if (monthAbsoluteX in middleScreen - i.width..middleScreen + i.width) {
                scrollToDay(middleScreen, monthViewTag.toInt())
                calendar.triggerLiveMonthTag(monthViewTag.toInt())
                calendar.checkTimeTravel()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollToDay(middleScreen: Int, viewTag: Int) {
        var dayNum = calendar.liveJustDayOfMonth.value!!.toInt()
        val yearNum = calendar.liveYearTag.value!!
        val monthNum = viewTag + 1
        val maxDaysInMonth = numOfDaysInAMonth(yearNum, monthNum)
        val numCheck: () -> Int = {
            if (dayNum > maxDaysInMonth) {
                maxDaysInMonth
            } else {
                dayNum
            }
        }
        dayNum = numCheck()
        val date = LocalDate.of(yearNum, monthNum, dayNum)
        val newDayOfYear = date.dayOfYear
        try{
            dayTextViews.forEach {
                if (it.tag.toString().split(" ").first().toInt() == newDayOfYear) {
                    val dayView = it
                    val dayViewTag = dayView.tag.toString()
                    val theDayView = dayView.findViewWithTag<TextView>(dayViewTag)
                    val dayViewXLocation = dayView.x.toInt() - middleScreen / 2
                    dayScrollWheel!!.scrollTo(dayViewXLocation, 0)
                    calendar.triggerLiveDayTag(dayViewTag)
                }
            }
        }catch (e: NumberFormatException){
            val dayView = dayTextViews[59]
            val dayViewTag = dayView.tag.toString()
            val theDayView = dayView.findViewWithTag<TextView>(dayViewTag)
            val dayViewXLocation = dayView.x.toInt() - middleScreen / 2
            dayScrollWheel!!.scrollTo(dayViewXLocation, 0)
            calendar.triggerLiveDayTag(dayViewTag)
        }
    }
    private fun day(horizontalScrollView: HorizontalScrollView, middleScreen: Int, scrollBounds: Rect){
        horizontalScrollView.getDrawingRect(scrollBounds)
        for (i in dayTextViews) {
            val dayViewTag = i.tag.toString()
            val theDayView = i.findViewWithTag<TextView>(dayViewTag)
            val dayAbsoluteX = theDayView.absX()

            val monthViewTag = dayViewTag.split(" ")[1].toInt()
            if (dayAbsoluteX in middleScreen - i.width..middleScreen + i.width) {

                monthScrollWheel!!.scrollTo(monthTextViews[monthViewTag].x.toInt() - middleScreen / 2, 0)
                calendar.triggerLiveDayTag(dayViewTag)
                calendar.triggerLiveMonthTag(monthViewTag)
                calendar.checkTimeTravel()
            }
        }
    }
    private fun year(horizontalScrollView: HorizontalScrollView, middleScreen: Int, scrollBounds: Rect) {
        horizontalScrollView.getDrawingRect(scrollBounds)

        for (i in yearTextViews) {
            val yearViewTag = i.tag.toString()
            val theYearView = i.findViewWithTag<TextView>(yearViewTag)
            val yearAbsoluteX = theYearView.absX()
            if (yearAbsoluteX in middleScreen - i.width..middleScreen + i.width) {
                calendar.triggerLiveYearTag(yearViewTag.toInt())
                calendar.checkTimeTravel()
                if (calendar.isLeapYear(yearViewTag.toInt())) {
                    leapYearDay(true)
                } else {
                    leapYearDay(false)
                }
            }
        }
    }
    private fun leapYearDay(bool: Boolean){
        if (bool){
            if (dayTextViews[59].text != "29"){
                val daysTextView = textViewPattern("29", "59.5 1 29")
                dayLinearLayout!!.addView(daysTextView, 59)
                dayTextViews.add(59, daysTextView)
            }
        }
        else{
            if (dayTextViews[59].text == "29"){
                dayTextViews.removeAt(59)
                dayLinearLayout!!.removeViewAt(59)
            }
        }
    }

    private fun View.absX(): Int {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return location[0]
    }
    private fun createYears(){
        for (i in 1900..calendar.currentYear){
            val yearTextView = textViewPattern(i.toString(), "$i")
            yearTextViews.add(yearTextView)
            yearLinearLayout!!.addView(yearTextView)

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun numOfDaysInAMonth(year:Int, month:Int): Int{
        return YearMonth.of(year, month).lengthOfMonth()
    }
    /**creates 12 months of text views if repeated once*/
    private fun createMonths(repeatTimes: Int, months: List<String>, year: Int){
        repeat(repeatTimes) {
            months.forEachIndexed { monNum, monthName ->
                val monthTextView = textViewPattern(monthName, "$monNum")
                monthTextViews.add(monthTextView)
                monthLinearLayout!!.addView(monthTextView)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDays(repeatTimes: Int, months: List<String>, year: Int){
        var dayNumOfYear = 1
        repeat(repeatTimes) {
            months.forEachIndexed { monNum, monthName ->
                val num: Int = numOfDaysInAMonth(year, monNum  + 1)
                for (dayNum in 1..num) {
                    val daysTextView = textViewPattern(dayNum.toString(), "$dayNumOfYear $monNum $dayNum")
                    dayLinearLayout!!.addView(daysTextView)
                    dayTextViews.add(daysTextView)
                    dayNumOfYear += 1
                }
            }
        }
    }
    /**textView Pattern*/
    private fun textViewPattern(textViewText: String, textViewTag: String): TextView{
        val name = TextView(context)
            .apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(50, 0, 50, 0)
                    setPadding(0, 0, 0, 0)
                }
                typeface = ResourcesCompat.getFont(context, R.font.bebas_neue)
            }
        name.tag = textViewTag
        name.typeface = ResourcesCompat.getFont(requireContext(), R.font.bebas_neue)
        name.text = textViewText
        name.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        name.setBackgroundResource(R.color.clear)
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35F)
        return name
    }

}