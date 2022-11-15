package io.doorapp.door

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.time.Year
import java.util.*

class CalendarViewModel : ViewModel(){
    private val calendar = Calendar.getInstance()
    var currentMonth = calendar.get(Calendar.MONTH)
    var currentDayYear = calendar.get(Calendar.DAY_OF_YEAR)
    private var currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    var currentYear = calendar.get(Calendar.YEAR)
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    private val monthTag = MutableLiveData(currentMonth)
    val liveMonthTag: LiveData<Int> = monthTag

    private val dayTag = MutableLiveData("$currentDayYear $currentMonth $currentDay")
    val liveDayTag: LiveData<String> = dayTag

    private val yearTag =  MutableLiveData(currentYear)
    val liveYearTag: LiveData<Int> = yearTag

    private val timeTravel = MutableLiveData(true)
    val liveTimeTravel: LiveData<Boolean> = timeTravel

    private val justDayOfYear = MutableLiveData(currentDayYear.toString())
    val liveJustDayOfYear: LiveData<String> = justDayOfYear

    private val justDayOfMonth = MutableLiveData(currentDay.toString())
    val liveJustDayOfMonth: LiveData<String> = justDayOfMonth

    private val dobFormatted = MutableLiveData("${months[currentMonth]} $currentDay, " +
            "$currentYear ")
    val liveDobFormatted: LiveData<String> = dobFormatted

    fun updateInfo(){
        justDayOfYear.value = liveDayTag.value.toString().split(" ").first()
        justDayOfMonth.value = liveDayTag.value.toString().split(" ").last()
        dobFormatted.value = "${months[monthTag.value!!]} ${liveJustDayOfMonth.value}, " +
                "${yearTag.value} "
    }

    fun triggerLiveMonthTag(month: Int){
//        "$monNum"
        monthTag.value = month
        updateInfo()
    }
    fun triggerLiveDayTag(day: String){
//        "$dayNumOfYear $monNum $dayNum"
        dayTag.value = day
        updateInfo()
    }
    fun triggerLiveYearTag(year: Int){
//        "year
        yearTag.value = year
        updateInfo()
    }

    fun checkTimeTravel(){
        timeTravel.value = liveJustDayOfYear.value!!.toDouble() > currentDayYear.toDouble() && liveYearTag.value!!.toInt() == currentYear
    }

    fun isLeapYear(year: Int): Boolean{
        var leap = false
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                leap = year % 400 == 0
            } else
                leap = true
        } else
            leap = false
        return leap
    }







//
//    fun combindDobData (a: LiveData<String>, b: LiveData<String>, c: LiveData<String>):  LiveData<Triple<String, String, String>>{
//        fun <A, B, C> LiveData<A>.combine(bStream: LiveData<B>, cStream: LiveData<C>): LiveData<Triple<A, B, C>> {
//            val result = MediatorLiveData<Triple<A, B, C>>()
//            result.addSource(this) { a ->
//                if (a != null && bStream.value != null && cStream.value != null) {
//                    result.value = Triple(a, bStream.value!!, cStream.value!!)
//                }
//            }
//            result.addSource(bStream) { b ->
//                if (b != null && this.value != null && cStream.value != null) {
//                    result.value = Triple(this.value!!, b, cStream.value!!)
//                }
//            }
//            result.addSource(cStream){ c ->
//                if (c != null && b.value != null && this.value != null) {
//                    result.value = Triple(this.value!!, bStream.value!!, c)
//                }
//            }
//            return result
//        }
//        return a.combine(b, c)
//    }






//    fun updateSelectedMonth(newMonthText:String, newMonthTag: String){
//        selectedMonthText.postValue(newMonthText)
//        monthTag.postValue(newMonthTag)
//    }
//    fun updateselectedDay(newDayText:String, newDayTag: String){
//        selectedDayText.postValue(newDayText)
//        dayTag.postValue(newDayTag)
//    }
//    fun updateselectedYear(newYearText:String, newYearTag: String){
//        selectedYearText.postValue(newYearText)
//        yearTag.postValue(newYearTag)
//    }


}