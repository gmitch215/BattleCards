package me.gamercoder215.battlecards.util

import org.bukkit.ChatColor
import org.bukkit.util.ChatPaginator

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

}