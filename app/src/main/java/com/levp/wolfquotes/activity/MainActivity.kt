package com.levp.wolfquotes.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.levp.wolfquotes.Dec
import com.levp.wolfquotes.R
import com.levp.wolfquotes.WordList
import com.levp.wolfquotes.buildStorage
import com.levp.wolfquotes.database.AppDBhelper.db
import com.levp.wolfquotes.database.AppDBhelper.historyDao
import com.levp.wolfquotes.database.AppDBhelper.historyList
import com.levp.wolfquotes.database.AppDatabase
import com.levp.wolfquotes.models.HistoryEntryEntity
import com.levp.wolfquotes.models.HistoryViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.*


class MainActivity : AppCompatActivity() {

    companion object{
        var wordMap = HashMap<String, WordList>()
        var overallWeight=0;
        var quote = "Ауф."
        const val totalTemplates = 43
        lateinit var log : ArrayList<Int>

        const val APP_PREFERENCES = "mysettings"
        const val APP_PREFERENCES_HISTORY = "history_counter"
        lateinit var settings : SharedPreferences
        var historySize = 0
        lateinit var historyModel : HistoryViewModel


        //notifications
        lateinit var notificationManager: NotificationManager

        const val NOTIFICATION_ID = 101
        var CHANNEL_ID = "channelID"
    }

    lateinit var text : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()

        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        historySize = settings.getInt(APP_PREFERENCES_HISTORY, 0)



        main_btn_gen.setOnClickListener{
            genTemplate()
        }
        goto_history.setOnClickListener {
            openHistory()
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01"
            val name: CharSequence = "my_channel"
            val Description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)
            notificationManager?.createNotificationChannel(mChannel)
        }
        downvote_btn.setOnClickListener{
            genTemplate()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon1_small_hdpi)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon1_small))
                .setContentTitle("Цитата дня:")
                .setContentText(quote)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                notificationManager.notify(NOTIFICATION_ID, builder.build()) // посылаем уведомление
            }
        }
        initDB()

    }

    private fun downvote() {
        //Log.e("current list size", HistoryViewModel.historyListLiveData.value?.size.toString())
    }

    private fun openHistory() {
        val i = Intent(this, HistoryActivity::class.java)

        startActivity(i)
    }

    private fun initData(){
        readFile()
        val patternStorageStream = MainActivity::class.java.getResource("/res/raw/patterns.txt")!!.readBytes()
        val presetStorageStream = MainActivity::class.java.getResource("/res/raw/presets.txt")!!.readBytes()
        val funcStorageStream = MainActivity::class.java.getResource("/res/raw/func.txt")!!.readBytes()

        buildStorage.readData(patternStorageStream.inputStream())
        buildStorage.presetInit(presetStorageStream.inputStream())
        Dec.funcInit(funcStorageStream.inputStream())

        log = ArrayList<Int>(totalTemplates)
        repeat(totalTemplates) { log.add(5) }
        calcOverallWeight()
    }

    override fun onPause() {
        super.onPause()
        val editor: Editor = settings.edit()
        editor.putInt(APP_PREFERENCES_HISTORY, historySize)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        if(settings.contains(APP_PREFERENCES_HISTORY))
            historySize =settings.getInt(APP_PREFERENCES_HISTORY, 0)
    }
    private fun initDB(){
        CoroutineScope(Dispatchers.Default).launch {

            db = AppDatabase.getAppDataBase(this@MainActivity)
            historyDao = db!!.historyDao()

            historyModel = ViewModelProvider(this@MainActivity).get(HistoryViewModel::class.java)
            //Log.e("Model size", historyModel.historyListLiveData.value?.size.toString())

            historyList =  ArrayList(historyDao!!.pickAllHistory())

        }
    }
    private fun genTemplate()
    {
        val template = weightedRandom()
        val logger = "[#$template]"
        //println(logger)
        quote = buildStorage.getPattern(template, totalTemplates)
        text_main.text = quote
        historySize++
        writeToHistory(template)
    }

    private fun writeToHistory(template: Int) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.UK)
        val currentDate = sdf.format(Date())
        val entry = HistoryEntryEntity(historySize, quote, currentDate, "t#$template")

        CoroutineScope(Dispatchers.Default).launch {
            historyDao?.insertEntry(entry)
        }
    }

    fun calcOverallWeight(): Int {
        var res = 0
        for (i in log.indices) res += log.get(i)
        overallWeight = res
        return res
    }

    fun weightedRandom(): Int {
        val random = Random().nextInt(overallWeight + 1)
        var counter = 0
        var res = 0
        for (i in log.indices) {
            counter += log.get(i)
            if (counter >= random) {
                res = i
                break
            }
        }
        return res
    }

    @Throws(IOException::class)
    fun readFile() {
        var res = MainActivity::class.java.getResource("/res/raw/words.txt")!!.readBytes()
        //val ins = R.raw.words.toString()
        val br = BufferedReader(InputStreamReader(res.inputStream()))
        text = R.raw.words.toString()
        val eng = "[a-zA-Z]"
        val noteng = "[^a-zA-Z0-9]"
        val rus = "[^a-яА-Я ]"
        val num = "[^0-9]"
        val pattern = Pattern.compile(eng)
        val pattern1 = Pattern.compile(num)
        val pattern2 = Pattern.compile(rus)

        var str = br.readLine()
        var groupName: String
        var iterations = 0
        while (str != null) {

            iterations++
            var matcher = pattern.matcher(str)
            if (matcher.find()) {
                groupName = str
                groupName = groupName.replace(noteng.toRegex(), "")
                val words = ArrayList<String>()
                val weights = ArrayList<Int>()

                val safe = ArrayList<Int>()

                str = br.readLine()
                if (str == null) break
                matcher = pattern.matcher(str)
                while (!matcher.find()) {
                    if (str!!.length <= 1) break
                    val w = str.replace(num.toRegex(), "").toInt()
                    val word = str.replace(rus.toRegex(), "")

                    //str = str.sub
                    words.add(word)
                    weights.add(w)
                    str = br.readLine()
                    if (str == null) break
                    matcher = pattern.matcher(str)
                }
                if(words.size == weights.size)
                    wordMap[groupName] = WordList(weights, words)
                //insertWordListOfType(words, groupName, weights, safe)
            }
            if (str == null || str.length <= 1) break
        }
    }
}

