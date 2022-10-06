package com.ar.gdscworkshop

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    lateinit var preferences: SharedPreferences
    // Defining Variables
    //TODO("Teach initialisation and declaration of variables, use of classes")
    var day = 0
    var month : Int = 0
    var year : Int = 0
    var currentDay = 0
    var currentHour = 0
    var currentMinute = 0
    var currentMonth = 0
    var currentYear = 0
    lateinit var adapter: ReminderAdapter
    val CHANNEL_ID = "gdscNotif"
    val notificationID = 314

    //TODO("Teach declaration of components")
    private lateinit var reminderName : TextInputEditText
    private lateinit var reminderNameField : TextInputLayout
    private lateinit var description : EditText
    private lateinit var dateAndTime : TextView
    private lateinit var addReminder : Button
    lateinit var recyclerView : RecyclerView

    // Components for Recycler View
    // TODO("Creation of Arrays")
    var reminderList = ArrayList<String>()
    var descriptionList = ArrayList<String>()
    var dateAndTimeList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        // Initialising Components
        //TODO("Teach initialisation of different components")
        reminderName = findViewById(R.id.reminderName)
        reminderNameField = findViewById(R.id.reminderNameField)
        description = findViewById(R.id.description)
        dateAndTime = findViewById(R.id.dateAndTime)
        addReminder = findViewById(R.id.addReminder)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        // Accessing Date and Time
        //TODO("Only implement the onClickListener during the workshop")
        dateAndTime.setOnClickListener {
            chooseDeadline()
        }

        // Don't change this
        preferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)

        if(preferences.getInt("reminderListSize", 0) != 0){
            reminderList = loadReminders("reminderList")
            descriptionList = loadReminders("descriptionList")
            dateAndTimeList = loadReminders("dateAndTimeList")
        }
        adapter = ReminderAdapter(reminderList, descriptionList, dateAndTimeList, this@MainActivity)
        recyclerView.adapter = adapter



        // TODO("When Add Reminder is Clicked")
        addReminder.setOnClickListener {
            //TODO("Teach function call here")
            if(checkFields()){
                sendNotification("${reminderName.text.toString()}", "${description.text}")
                //TODO("Add elements to the array")
                reminderList.add(reminderName.text.toString())
                descriptionList.add(description.text.toString())
                dateAndTimeList.add(dateAndTime.text.toString())
                saveReminders(reminderList, "reminderList")
                saveReminders(descriptionList, "descriptionList")
                saveReminders(dateAndTimeList, "dateAndTimeList")
            }
            dateAndTime.text = ""
            adapter.notifyDataSetChanged()
        }
    }

    //TODO("Teach function declaration here. Remember to create after onCreate function")
    private fun checkFields() : Boolean{
        if(reminderName.length() == 0){
            Toast.makeText(this, "Mandatory Field", Toast.LENGTH_SHORT).show()
            reminderNameField.boxStrokeColor = Color.parseColor("#b00020")
            reminderName.requestFocus()
            return false
        }
        return true
    }


    // TODO("Do not change the following functions")
    private fun chooseDeadline() {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(this@MainActivity, this@MainActivity, year, month, day)
        datePickerDialog.show()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        currentDay = day
        currentYear = year
        currentMonth = month
        val calendar: Calendar = Calendar.getInstance()
        currentHour = calendar.get(Calendar.HOUR)
        currentMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this@MainActivity, this@MainActivity, currentHour, currentMinute, is24HourFormat(this))
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        currentHour = hourOfDay
        currentMinute = minute
        dateAndTime.text = "${currentDay}/${currentMonth+1}/${currentYear}  ${currentHour}:${currentMinute}"
    }
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
                .apply { descriptionText }
            val notificationManager : NotificationManager = getSystemService(Context. NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendNotification(title : String, desc : String){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationID, builder.build())
        }
    }
    fun saveReminders(array: ArrayList<String>, arrayName: String): Boolean {
        preferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        var editor = preferences.edit()
        editor.putInt(arrayName + "Size", array.size)
        for (i in array.indices)
            editor.putString(arrayName + "_" + i, array[i])
        editor.commit()
        return editor.commit()
    }

    fun loadReminders(arrayName: String): ArrayList<String> {
        val size = preferences.getInt(arrayName + "Size", 0)
        val array = ArrayList<String>(size)
        for (i in 0 until size)
            array.add(preferences.getString(arrayName + "_" + i, null).toString())
        return array
    }
}