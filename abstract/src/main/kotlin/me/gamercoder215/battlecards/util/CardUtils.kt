package me.gamercoder215.battlecards.util

import org.bukkit.ChatColor
import org.bukkit.util.ChatPaginator
import java.util.*

object CardUtils {

    // Entity Utils

    // String Utils

    fun color(s: String): Array<String> {
       val array: Array<String> = s.trim().split("\\s+".toRegex()).toTypedArray()
       val prefix = if (array[0].startsWith("&")) array[0].substring(1) else ChatColor.GRAY

       for (i in array.indices) {
           val value = array[i]

           if (value.endsWith("%")) array[i] = "${ChatColor.GREEN}$value$prefix"
           if (value.replace(",", "").toDoubleOrNull() != null) array[i] = "${ChatColor.RED}$value$prefix"
       }

       return ChatPaginator.wordWrap(
           StringBuilder().append(prefix).append(array.joinToString(" ")).toString(),
           30
       )
    }

    fun color(s: Collection<String>): List<String> {
        val list = mutableListOf<String>()
        for (i in s) list.addAll(color(i))

        return list
    }

    // Other

    @JvmStatic
    private val ROMAN_NUMERALS = TreeMap<Long, String>().apply {
        putAll(mutableMapOf(
            1000L to "M",
            900L to "CM",
            500L to "D",
            400L to "CD",
            100L to "C",
            90L to "XC",
            50L to "L",
            40L to "XL",
            10L to "X",
            9L to "IX",
            5L to "V",
            4L to "IV",
            1L to "I"
        ))
    }

    fun toRoman(number: Long): String {
        val l: Long = ROMAN_NUMERALS.floorKey(number)
        return if (number == l) ROMAN_NUMERALS[number]!! else ROMAN_NUMERALS[l] + toRoman(number - l)
    }

}