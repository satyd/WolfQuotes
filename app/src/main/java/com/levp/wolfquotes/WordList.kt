package com.levp.wolfquotes

import com.levp.wolfquotes.activity.MainActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern


object Jmeh{

    var wordMap = HashMap<String, WordList>()
    var overallWeight=0;
    var quote = "Ауф."
    var template = -1
    const val totalTemplates = 43
    lateinit var log : ArrayList<Int>

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
    fun genTemplate() : String
    {
        template = weightedRandom()
        val logger = "[#$template]"

        quote = BuildStorage.getPattern(template, totalTemplates)

        //historySize++
        return quote
    }

    @Throws(IOException::class)
    fun readFile() {
        val res = MainActivity::class.java.getResource("/res/raw/words.txt")!!.readBytes()

        val br = BufferedReader(InputStreamReader(res.inputStream()))

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
    fun initData(){
        readFile()
        val patternStorageStream = MainActivity::class.java.getResource("/res/raw/patterns.txt")!!.readBytes()
        val presetStorageStream = MainActivity::class.java.getResource("/res/raw/presets.txt")!!.readBytes()
        val funcStorageStream = MainActivity::class.java.getResource("/res/raw/func.txt")!!.readBytes()

        BuildStorage.readData(patternStorageStream.inputStream())
        BuildStorage.presetInit(presetStorageStream.inputStream())
        Dec.funcInit(funcStorageStream.inputStream())

        log = ArrayList<Int>(totalTemplates)
        repeat(totalTemplates) { log.add(5) }
        calcOverallWeight()
    }

}
class Word(val value: String, var weight: Int) {

    fun changeWeight(chng: Int) {
        weight += chng
    }

    override fun toString(): String {
        return "$value $weight"
    }
}

class WordList {

    var weight = 0
    var list = ArrayList<Word>()

    constructor(init: ArrayList<Word>) {
        weight = calcWeight()
        list = init
    }

    constructor(weights: ArrayList<Int>, init: ArrayList<String>) {
        weight = 0
        if (weights.size == init.size) {
            for (i in weights.indices) {
                val tmp = Word(init[i], weights[i])
                weight += weights[i]
                list.add(tmp)
            }
            //this.calcWeight();
        } else System.err.println("Неправильные размеры массивов!!!")
    }

    fun addWord(a: Word) {
        list.add(a)
        weight += a.weight
    }

    fun addWord(str: String, weight: Int) {
        list.add(Word(str, weight))
        this.weight += weight
    }

    fun addStr(str: String, weight: Int) {
        val a = Word(str, weight)
        list.add(a)
        this.weight += weight
    }

    fun calcWeight(): Int {
        weight = 0
        for (i in list.indices) weight += list[i].weight
        return weight
    }

    val size: Int
        get() = list.size


    fun getWord() : String {

            val rand = Random()
            val randomize = rand.nextInt(weight + 1)
            var start = 0
            var result = "<это баг>"

            for (i in list.indices) {

                start += list[i].weight
                if (start >= randomize) {
                    val tmp = list[i].value
                    result = tmp ?: "<это баг>"

                    break
                }
            }
            return result
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val tmp = other as WordList
        return list == tmp.list
    }

    override fun hashCode(): Int {
        var res = weight
        res = res * 31 + list.hashCode()
        return res
    }

    override fun toString(): String {
        val res = StringBuilder()
        for (i in list.indices) res.append(
            """
                ${list[i]}
                
                """.trimIndent()
        )
        return res.toString()
    }


}