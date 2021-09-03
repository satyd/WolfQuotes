package com.levp.wolfquotes

import java.util.*


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