package me.gamercoder215.battlecards.util

import com.sun.tools.jdi.IntegerValueImpl
import org.bukkit.ChatColor

object CardUtils {

   fun color(s: String): String {
       val array: Array<String> = s.trim().split("\\s+".toRegex()).toTypedArray()
       val prefix = if (array[0].startsWith("&")) array[0].substring(1) else ChatColor.GRAY

       for (i in array.indices) {
           val value = array[i]

           if (value.endsWith("%")) array[i] = "${ChatColor.GREEN}$value$prefix"
           if (value.replace(",", "").toDoubleOrNull() != null) array[i] = "${ChatColor.GREEN}$value$prefix"
       }

       return StringBuilder().append(prefix).append(array.joinToString(" ")).toString()
   }

}