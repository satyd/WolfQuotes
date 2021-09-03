package com.levp.wolfquotes

import com.levp.wolfquotes.activity.MainActivity
import java.io.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

object Dec {
    var func = HashMap<String, String>()
    var soft = "кшч"
    @Throws(IOException::class)
    fun funcInit(ins: InputStream?) {
        // = AppCompatActivity.getResources().openRawResource(R.raw.func);
        val br = BufferedReader(InputStreamReader(ins))
        //FileReader fr = new FileReader("func.txt");
        //BufferedReader br = new BufferedReader(new InputStreamReader(ins);
        var str = br.readLine()
        val iter = 0
        while (str != null) {
            if (!str.contains(";")) {
                val buf = StringBuilder(str)
                while (!buf.toString().contains(";")) {
                    buf.append(br.readLine())
                }
                str = buf.toString().replace("\t", "")
                str = buf.toString().replace("\n", "")
            }
            str = str.replace(";", "")
            val tmp = str.split(" : ".toRegex()).toTypedArray()
            func[tmp[0]] = tmp[1]
            str = br.readLine()
        }
    }

    fun first(noun: String, p: padezh?, t: type): String {
        val tmp = noun.substring(0, noun.length - 1)
        val preLast = tmp[tmp.length - 1]
        return when (p) {
            padezh.RODIT -> if (t == type.HARD && soft.indexOf(preLast) == -1) tmp + "ы" else tmp + "и"
            padezh.DAT, padezh.PREDLOZH -> tmp + "е"
            padezh.VINIT -> if (t == type.HARD) tmp + "у" else tmp + "ю"
            padezh.TVORIT -> if (t == type.HARD) tmp + "ой" else tmp + "ей"
            else -> noun
        }
    }

    fun second(noun: String, p: padezh?, t: type, g: gender): String {
        val tmp = if (g == gender.A || g == gender.M && t == type.SOFT) noun.substring(
                0,
                noun.length - 1
        ) else noun
        return when (p) {
            padezh.RODIT -> if (t == type.HARD) tmp + "а" else tmp + "я"
            padezh.DAT -> if (t == type.HARD) tmp + "у" else tmp + "ю"
            padezh.VINIT -> if (t == type.HARD) noun else if (g == gender.A) noun else tmp + "я"
            padezh.TVORIT -> if (t == type.HARD) tmp + "ом" else tmp + "ем"
            padezh.PREDLOZH -> tmp + "е"
            else -> noun
        }
    }

    fun third(noun: String, p: padezh?): String {
        val tmp = noun.substring(0, noun.length - 1)
        return when (p) {
            padezh.RODIT, padezh.DAT, padezh.PREDLOZH -> tmp + "и"
            padezh.TVORIT -> tmp + "ью"
            else -> noun
        }
    }

    fun adj(adjective: String, p: padezh?, t: type, g: gender): String {
        val tmp = adjective.substring(0, adjective.length - 2)
        val preLast = adjective[adjective.length - 2]
        return when (g) {
            gender.F -> when (p) {
                padezh.SHORT -> if (t == type.HARD) tmp + 'a' else tmp + "яя"
                padezh.IM -> if (t == type.HARD) tmp + "ая" else tmp + "яя"
                padezh.RODIT, padezh.DAT, padezh.PREDLOZH, padezh.TVORIT -> if (t == type.HARD) tmp + "ой" else tmp + "ей"
                padezh.VINIT -> if (t == type.HARD) tmp + "ую" else tmp + "юю"
                else -> adjective
            }
            gender.M, gender.A -> when (p) {
                padezh.SHORT -> if (g == gender.A) if (t == type.HARD) tmp + "о" else tmp + "е" else adjective
                padezh.IM -> if (g == gender.A) if (t == type.HARD) tmp + "ое" else tmp + "ее" else adjective
                padezh.RODIT -> if (t == type.HARD) tmp + "ого" else tmp + "его"
                padezh.DAT -> if (t == type.HARD) tmp + "ому" else tmp + "ему"
                padezh.VINIT -> if (g == gender.A) if (t == type.HARD) tmp + "ое" else tmp + "ее" else if (t == type.HARD) tmp + "ого" else tmp + "его"
                padezh.TVORIT -> if (preLast != 'и') if (t == type.HARD) tmp + "ым" else tmp + "им" else tmp + preLast + "м"
                padezh.PREDLOZH -> if (t == type.HARD) tmp + "ом" else tmp + "ем"
                else -> adjective
            }
            else -> adjective
        }
    }

    enum class padezh {
        IM, RODIT, DAT, VINIT, TVORIT, PREDLOZH, SHORT
    }

    enum class type {
        HARD, SOFT
    }

    enum class gender {
        M, F, A
    }
}

object buildStorage {
    var storage = LinkedHashMap<String, ArrayList<String>>()
    var Presets = ArrayList<String>()
    @Throws(IOException::class)
    fun presetInit(ins: InputStream?) {
        val br = BufferedReader(InputStreamReader(ins))
        //FileReader fr = new FileReader("func.txt");
        //BufferedReader br = new BufferedReader(new InputStreamReader(ins);
        var str = br.readLine()
        var iter = 0
        while (str != null) {
            if (!str.contains("#")) {
                val buf = StringBuilder(str)
                while (!buf.toString().contains("#")) buf.append(br.readLine())
            }
            str = str.replace("#", "")
            Presets.add(str)
            //println("preset " + ++iter)
            str = br.readLine()
        }
    }

    val preset: String
        get() = Presets[Random().nextInt(Presets.size)]

    fun getPattern(num: Int, lim: Int): String {
        var n = num
        if (num < 0) n = Random().nextInt(lim)
        //System.out.//println("Выпал шаблон #"+num);
        val str = StringBuilder("#")
        str.append(n)
        if (!storage.containsKey(str.toString())) str.append("_full")
        //System.out.//println(str.toString()+" in keys");
        return getPattern(str.toString())
    }

    fun getPattern(vararg input: String): String {
        val res = StringBuilder()
        var tmp: String? = null
        var work = ArrayList<String>()
        val picker = 0
        for (it in input) {
            it.replace(" ", "")
            if (it.contains("full")) {
                tmp = it.replace("full", "beg")
                work = storage[tmp]!!
                //System.out.//println(tmp);
                val pt1 = work

                val peck: String = weightedString(pt1)
                res.append(peck)
                tmp = it.replace("full", "end")
                work = storage[tmp]!!
            } else {
                work = storage[it]!!
                //System.out.//println(it);
            }
            val pt2 = work
            var j = 0
            //for (s in work) pt2[j++] = s
            val pecker: String = weightedString(pt2)
            res.append(pecker)
        }
        return transcript(res.toString())
    }

    @Throws(IOException::class)
    fun readData(ins: InputStream?) {
        //FileReader fr = new FileReader("patterns.txt");
        val br = BufferedReader(InputStreamReader(ins))
        var name: String? = null
        var values = ArrayList<String>()
        var str = br.readLine()
        while (str != null && str != "\n") {
            if (str.contains("#")) {
                if (name!=null)
                    storage[name] = values
                name = str
                values = ArrayList()
            } else values.add(str)
            str = br.readLine()
        }
        if (name!=null)
            storage[name] = values
        //System.out.//println(storage);
    }

    @Throws(IOException::class)
    fun giveExamples() {
        val fileOutputStream = FileOutputStream("giveExamples.txt")
        for ((key, output) in storage) {
            val name = """
                ${key.toString()}
                
                """.trimIndent()
            fileOutputStream.write(name.toByteArray())
            for (i in output) {
                //System.out.//println(i);
                val res = transcript(i) + "\n"
                fileOutputStream.write(res.toByteArray())
            }
        }
        fileOutputStream.close()
    }
}

fun transcript(`in`: String): String{

    val res = java.lang.StringBuilder(`in`)
    val rus = "[а-яА-Я&&[^a-z]]"
    val pattern = Pattern.compile(rus)
    var start = res.indexOf("[")
    var end = res.indexOf("]")
    var runs = 0
    while (start >= 0 && end >= 0 && runs < 50) {
        var replacement = java.lang.StringBuilder()
        runs++
        var sub = res.substring(start + 1, end)
        var buf: String
        var key: String?
        var words: Array<String>
        var instructions: String? = null
        if (sub.contains("$$")) {
            instructions = sub.substring(sub.lastIndexOf('$') + 1)
            sub = sub.substring(0, sub.lastIndexOf('$') - 1)
        }
        if (sub.contains("::")) {
            val weightRnd = ArrayList(sub.split("::".toRegex()))
            if (!sub.contains("(")) {
                buf = randString(weightRnd)
            } else {
                buf = weightedString(weightRnd)
            }
        } else buf = sub
        if (buf.contains("{")) words =
           buf.substring(buf.indexOf('{') + 1, buf.indexOf('}')).split(" && ".toRegex()).toTypedArray()
                 else {
            words = arrayOf(buf)

        }

        //Matcher matcher = pattern.matcher(buf);
        if (false) { //matcher.find()) {
            // replacement.append(words[new Random().nextInt(words.length)]);
            //System.out.//println(buf);
        } else {
            var iterations = 0
            for (works in words) {
                var work : String = works
                if (work.length == 0) break
                iterations++
                var params = arrayOfNulls<String>(0)
                if (work.contains(":")) {
                    val weightRnd = ArrayList(work.split(":".toRegex()))
                    if (!work.contains("(")) {
                        work = randString(weightRnd)
                    } else {
                        work = weightedString(weightRnd)
                    }
                }
                val matcher = pattern.matcher(work)
                if (matcher.find()) {
                    replacement.append(work)
                    if (iterations < words.size) replacement.append(" ")
                    continue
                }
                if (work.contains("$")) {
                    //System.out.//println(buf);
                    val index = work.indexOf('$')
                    params = work.substring(index + 1).split(", ".toRegex()).toTypedArray()
                    key = work.substring(0, index)
                    //System.out.//println(key);
                } else key = work
                //println(key)
                if (!work.contains("@")) work = roll(key) else work =
                    transcript(Dec.func[work.substring(1)]!!)
                if (params.size > 0) {
                    var p = '0'
                    var t = '0'
                    var g = '0'
                    for (i in params.indices) {
                        if (params[i]!![0] == 'U') work = work.capitalize(Locale.ROOT)
                        if (params[i]!!.length < 3 || i == 0) continue
                        if (params[i]!![0] == 'p') p = params[i]!![2]
                        if (params[i]!![0] == 't') t = params[i]!![2]
                        if (params[i]!![0] == 'g') g = params[i]!![2]
                    }
                    var padezh: Dec.padezh = when (p) {
                        'R' -> Dec.padezh.RODIT
                        'D' -> Dec.padezh.DAT
                        'V' -> Dec.padezh.VINIT
                        'T' -> Dec.padezh.TVORIT
                        'P' -> Dec.padezh.PREDLOZH
                        else -> Dec.padezh.IM
                    }
                    var type: Dec.type = when (t) {
                        'S' -> Dec.type.SOFT
                        else -> Dec.type.HARD
                    }
                    var gender: Dec.gender = when (g) {
                        'F' -> Dec.gender.F
                        'A' -> Dec.gender.A
                        else -> Dec.gender.M
                    }
                    when (params[0]) {
                        "adj" -> work = Dec.adj(work!!, padezh, type, gender)
                        "first" -> work = Dec.first(work!!, padezh, type)
                        "second" -> work = Dec.second(work!!, padezh, type, gender)
                        "third" -> work = Dec.third(work!!, padezh)
                        else -> {
                        }
                    }
                }
                replacement.append(work)
                if (iterations < words.size) replacement.append(" ")
            }
        }
        if (instructions != null) {
            var res2: String? = replacement.toString()
            val toDo = instructions.split(", ".toRegex()).toTypedArray()
            var isUpp = false
            val actor2: String = roll("nounsSubjM2H")
            for (iter in toDo) {
                when (iter) {
                    "U" -> isUpp = true
                    "rNot" -> res2 = randNot()
                    else -> res2 = Dec.func[iter]?.let { transcript(it) }
                }
            }
            if (isUpp) res2 = res2!!.capitalize(Locale.ROOT)
            replacement = java.lang.StringBuilder(res2)
        }
        res.replace(start, end + 1, replacement.toString())
        start = res.indexOf("[")
        end = res.indexOf("]")
    }
    return res.toString()
}
fun randNot(): String {
    val res = Random().nextInt(2)
    return when (res) {
        0 -> " не "
        else -> " "
    }
}

fun randString(args: ArrayList<String>): String {
    val index = Random().nextInt(args.size)
    return args[index]
}

fun weightedString(weightRnd: ArrayList<String>): String {
    var res = "<баг в weighted String>"
    val weights = IntArray(weightRnd.size)
    var it = 0
    var sum = 0
    for (c in weightRnd) {
        val pTop = c.lastIndexOf(")")
        if (pTop + 1 == c.length) {
            var pLow = pTop
            while (pLow > 0 && c[pLow] != '(') pLow--
            val tmp = c.substring(pLow + 1, pTop)
            val parse = tmp.toInt()
            weightRnd[it] = c.substring(0, pLow)
            weights[it++] = parse
            sum += parse
        } else {
            weights[it++] = 1
            sum += 1
        }
    }
    val rnd = Random().nextInt(sum) + 1
    var pick = 0
    for (i in weights.indices) {
        pick += weights[i]
        if (pick >= rnd) {
            res = weightRnd[i]
            break
        }
    }
    return res
}

fun roll(s: String): String {
    return MainActivity.wordMap[s]!!.getWord()
}
