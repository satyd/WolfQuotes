package com.levp.wolfquotes.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.SharedPreferences.Editor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.levp.wolfquotes.*
import com.levp.wolfquotes.Jmeh.genTemplate
import com.levp.wolfquotes.Jmeh.log
import com.levp.wolfquotes.Jmeh.template
import com.levp.wolfquotes.Jmeh.totalTemplates
import com.levp.wolfquotes.database.AppDBHelper.db
import com.levp.wolfquotes.database.AppDBHelper.historyDao
import com.levp.wolfquotes.database.AppDBHelper.historyList
import com.levp.wolfquotes.database.AppDatabase
import com.levp.wolfquotes.database.Repository
import com.levp.wolfquotes.databinding.ActivityMainBinding
import com.levp.wolfquotes.models.FavoritesEntryEntity
import com.levp.wolfquotes.models.HistoryEntryEntity
import com.levp.wolfquotes.models.HistoryViewModel
import com.levp.wolfquotes.models.LogEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.*


class MainActivity : AppCompatActivity() {

    companion object {

        val STARTING_QUOTE = "Ауф."

        var quote: String = STARTING_QUOTE

        const val APP_PREFERENCES = "mysettings"
        const val APP_PREFERENCES_HISTORY = "history_counter"
        const val LATEST_QUOTE = "latest quote"
        const val WAS_VOTED = "was_voted"

        lateinit var settings: SharedPreferences
        var historySize = 0
        lateinit var historyModel: HistoryViewModel

        //notifications
        lateinit var notificationManager: NotificationManager
        const val NOTIFICATION_ID = 101
        var CHANNEL_ID = "channelID"

    }

    private lateinit var binding: ActivityMainBinding
    
    //Settings
    private var isSoundOn: Boolean? = false
    private var currentBackgroundImage: String? = "volk0.jpg"

    lateinit var repository: Repository

    //String ~== group id, WordList == группа слов
    private var menu: Menu? = null
    lateinit var text: String

    private var voted = true

    var clipboardManager: ClipboardManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Получаем настройки из преференсов

        repository = Repository(app = application)

//        val mActionBarToolbar = binding.toolbar.toolbarMain
//        setSupportActionBar(mActionBarToolbar)

        binding.mainBtnGen.setOnClickListener { makeQuote() }
        binding.gotoHistory.setOnClickListener { openHistory() }
        binding.addToFavorites.setOnClickListener { writeToFavorites() }

        binding.textMain.setOnLongClickListener {
            if (quote != STARTING_QUOTE) {
                val clipData = ClipData.newPlainText("text", quote)
                clipboardManager?.setPrimaryClip(clipData)
                Toast.makeText(baseContext, " Текст скопирован! ", Toast.LENGTH_SHORT).show()
            }
            false
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01"
            val name: CharSequence = "quotes_channel"
            val description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(mChannel)
        }

        onInit()
        binding.upvoteBtn.setOnClickListener { upVote() }
        binding.downvoteBtn.setOnClickListener { downVote() }

        //onCreateOptionsMenu(menu)

    }

    private fun makeQuote() {
        voted = false
        quote = genTemplate()
        binding.textMain.text = quote
        historySize++
        writeToHistory(Jmeh.template)
    }

    private fun upVote() {

        if (log == null)
            Jmeh.logInit(repository)

        if (!voted)
            if (log!![template] < 13) {
                log!![template] += 1
                historyDao?.updLog(log!![template], template)
                Toast.makeText(this, "+1 шаблону #$template", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "шаблон #$template уже достаточно хорош", Toast.LENGTH_SHORT)
                    .show()
            }
        voted = true
        if (isSoundOn!!) MediaPlayer.create(this, R.raw.auf1).start()
    }

    private fun downVote() {
        if (log == null)
            Jmeh.logInit(repository)
        if (!voted)
            if (log!![template] > 5) {
                log!![template] -= 1
                historyDao?.updLog(log!![template], template)
                Toast.makeText(this, "-1 шаблону #$template", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "шаблон #$template уже достаточно заминусён, хватит с него",
                    Toast.LENGTH_SHORT
                ).show()
            }
        voted = true

    }

    fun createNotification() {
        quote = genTemplate()
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

    private fun openHistory() {
        val i = Intent(this, HistoryActivity::class.java)
        startActivity(i)
    }


    override fun onPause() {
        super.onPause()
        val editor: Editor = settings.edit()
        editor.putInt(APP_PREFERENCES_HISTORY, historySize)

        editor.putString(LATEST_QUOTE, quote)

        editor.putBoolean(WAS_VOTED, voted)
        //editor.putInt(APP_PREFERENCES_HISTORY, historySize)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isSoundOn = prefs.getBoolean("isSoundEnabled", false)
        currentBackgroundImage = prefs.getString("backgroundImage", "volk0")

        val resourceId: Int = resources.getIdentifier(
            currentBackgroundImage,
            "drawable",
            packageName
        )

        Log.d("bg_image", currentBackgroundImage.toString())
        Log.d("soundOn", isSoundOn.toString())

        binding.mainBg.setBackgroundResource(resourceId)

        if (settings.contains(APP_PREFERENCES_HISTORY))
            historySize = settings.getInt(APP_PREFERENCES_HISTORY, 0)
    }

    private fun onInit() {
        Jmeh.initData(repository)
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        historySize = settings.getInt(APP_PREFERENCES_HISTORY, 0)

        CoroutineScope(Dispatchers.Default).launch {
            db = AppDatabase.getAppDataBase(this@MainActivity)
            historyDao = db!!.historyDao()
            historyModel = ViewModelProvider(this@MainActivity).get(HistoryViewModel::class.java)
            historyList = ArrayList(historyDao!!.pickAllHistory())
            if (historyDao?.getLogCount() == 0)
                initLogDB()
        }
        Jmeh.logInit(repository)
    }

    private fun writeToHistory(template: Int) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.UK)
        val currentDate = sdf.format(Date())
        val entry = HistoryEntryEntity(historySize, quote, currentDate, "t#$template")

        CoroutineScope(Dispatchers.Default).launch {
            historyDao?.insertEntry(entry)
        }
    }

    private fun writeToFavorites() {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.UK)
        val currentDate = sdf.format(Date())
        val entry = FavoritesEntryEntity(historySize, quote, currentDate, "t#${Jmeh.template}")

        CoroutineScope(Dispatchers.Default).launch {
            historyDao?.insertFavEntry(entry)
        }
    }

    private fun initLogDB() {
        for (i in 0..totalTemplates) {
            val entry = LogEntryEntity(logTemplate = i + 1, logEnabled = true, logPoints = 5)
            historyDao?.initLogs(entry)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_favorite -> {
                val i = Intent(this, FavoritesActivity::class.java)
                startActivity(i)
                true
            }
            R.id.action_info -> {
                val intentAbout = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intentAbout)
                true
            }
            R.id.action_points -> {
                val intentPoints = Intent(this@MainActivity, PointsActivity::class.java)
                intentPoints.putExtra("points", log)
                startActivity(intentPoints)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

