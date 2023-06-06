package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.impl.cards.*
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import org.bukkit.inventory.ItemStack
import java.security.SecureRandom

interface Wrapper {

    fun sendActionbar(player: Player, component: BaseComponent)

    fun sendActionbar(player: Player, message: String)

    fun setBossBarVisibility(boss: Wither, visible: Boolean)

    fun loadProperties(en: Creature, card: IBattleCard<*>)

    fun getNBTWrapper(item: ItemStack): NBTWrapper

    fun isCard(en: Creature): Boolean

    fun createInventory(id: String, name: String, size: Int): BattleInventory

    companion object {
        @JvmStatic
        val w = getWrapper()

        @JvmStatic
        val r = SecureRandom()

        @JvmStatic
        val versions = listOf(
            "1_8_R1",
            "1_8_R2",
            "1_8_R3",
            "1_9_R1",
            "1_9_R2",
            "1_10_R1",
            "1_11_R1",
            "1_12_R1",
            "1_13_R1",
            "1_13_R2",
            "1_14_R1",
            "1_15_R1",
            "1_16_R1",
            "1_16_R2",
            "1_16_R3",
            "1_17_R1",
            "1_18_R1",
            "1_18_R2",
            "1_19_R1",
            "1_19_R2",
            "1_19_R3"
        )

        @JvmStatic
        fun getWrapper(): Wrapper {
            val constr = Class.forName("${Wrapper::class.java.`package`.name}.v${getServerVersion()}.Wrapper${getServerVersion()}")
                .asSubclass(Wrapper::class.java)
                .getDeclaredConstructor()

            constr.isAccessible = true
            return constr.newInstance()
        }

        @JvmStatic
        fun loadCards() {
            val current = getServerVersion()
            val loaded: MutableList<Class<out IBattleCard<*>>> = mutableListOf()

            loaded.addAll(listOf(
                IBasicCard::class.java,
                IDiamondGolem::class.java,
                IKingWither::class.java,
                IWitherman::class.java,
                ISniper::class.java
            ))

            versions.subList(0, versions.indexOf(current) + 1).forEach {
                try {
                    loaded.addAll(Class.forName("${IBattleCard::class.java.`package`.name}.CardLoader$it")
                        .asSubclass(CardLoader::class.java)
                        .getDeclaredConstructor()
                        .newInstance()
                        .loadedCards())
                } catch (ignored: ClassNotFoundException) {}
            }

            loaded.forEach(BattleConfig.getConfig()::registerCard)
        }

        @JvmStatic
        fun getServerVersion(): String {
            return Bukkit.getServer().javaClass.`package`.name.split("\\.")[3].substring(1)
        }

        @JvmStatic
        fun get(key: String): String {
            return BattleConfig.getConfig().get(key)
        }

        @JvmStatic
        fun getMessage(key: String): String {
            return BattleConfig.getConfig().getMessage(key)
        }

    }

}