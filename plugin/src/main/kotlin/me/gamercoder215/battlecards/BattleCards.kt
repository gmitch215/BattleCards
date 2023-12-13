package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableSet
import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.BattleConfig.Companion.print
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.impl.Type
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.impl.cards.IBattleCardListener
import me.gamercoder215.battlecards.placeholderapi.BattlePlaceholders
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.cardShard
import me.gamercoder215.battlecards.util.inventory.Items.createShapedRecipe
import me.gamercoder215.battlecards.util.inventory.Items.exactChoice
import me.gamercoder215.battlecards.vault.VaultChat
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bstats.charts.SingleLineChart
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private const val bstats = 18166
private const val github = "GamerCoder215/BattleCards"
private const val hourTicks: Long = 20 * 60 * 60

@Suppress("unchecked_cast")
class BattleCards : JavaPlugin(), BattleConfig {

    fun loadListeners() {
        BattleCardListener(this)
        BattleGUIManager(this)
        BattleSpawner(this)

        IBattleCardListener(this)
    }

    val tasks: MutableSet<BukkitTask> = mutableSetOf()

    fun loadTasks() {
        tasks.addAll(listOf(
                object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.getOnlinePlayers().forEach {
                            it.inventory.cards.forEach { (slot, card) ->
                                card.statistics.cardExperience += this@BattleCards.growthPassiveAmount
                                it.inventory[slot] = card.itemStack
                            }
                        }
                    }
                }.runTaskTimer(this, hourTicks, hourTicks),
                object : BukkitRunnable() {
                    override fun run() {
                        saveMetadata()
                        sync { loadBlockMetadata() }
                    }
                }.runTaskTimerAsynchronously(this, 600, 600)
            )
        )
    }

    fun checkIntegrations() {
        if (hasVault()) {
            logger.info("Vault Integration Found!")
            VaultChat.loadChat()
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logger.info("Placeholder API Found! Hooking...")
            BattlePlaceholders(this)
            logger.info("Hooked into Placeholder API!")
        }
    }

    internal fun hasVault(): Boolean {
        return Bukkit.getPluginManager().getPlugin("Vault") != null
    }

    override fun onEnable() {
        saveDefaultConfig()

        BattleConfig.loadConfig()
        loadListeners()
        Wrapper.getCommandWrapper()
        logger.info("Loaded Files...")

        loadTasks()
        Bukkit.getOnlinePlayers().forEach { w.addPacketInjector(it) }
        logger.info("Loaded Tasks...")

        Wrapper.loadCards()
        logger.info("Registered ${registeredCards.size} Cards")
        logger.info("Registered ${registeredEquipment.size} Card Equipments")

        loadMetadata()
        Items.RECIPES.forEach { Bukkit.addRecipe(it) }
        logger.info("Loaded Metadata...")

        // UpdateChecker
        UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, github).apply {
            supportLink = "https://discord.gg/WVFNWEvuqX"
            isNotifyOpsOnJoin = true
            changelogLink = "https://github.com/$github/releases/latest/"
            isColoredConsoleOutput = true
            donationLink = "https://www.patreon.com/teaminceptus"
            isNotifyRequesters = true

            setUserAgent("$github v${BattleCards::class.java.`package`.implementationVersion}")
            checkEveryXHours(1.0)
            checkNow()
        }

        // bStats
        Metrics(this, bstats).apply {
            addCustomChart(SimplePie("language") { language })
            addCustomChart(SingleLineChart("cards") { Bukkit.getOnlinePlayers().sumOf { it.inventory.size } })
        }

        logger.info("Loaded Dependencies...")

        checkIntegrations()
        logger.info("Loaded Addons...")

        logger.info("Finished!")
    }

    override fun onDisable() {
        for (card in IBattleCard.spawned.values) card.despawn()
        logger.info("Unloaded Cards...")

        tasks.forEach { try { it.cancel() } catch (ignored: IllegalStateException) {} }
        Bukkit.getOnlinePlayers().forEach { w.removePacketInjector(it) }
        logger.info("Stopping Tasks...")

        saveMetadata()
        logger.info("Saved Metadata...")

        logger.info("Finished!")
    }

    private fun loadBlockMetadata(): Map<Location, BattleBlockData> {
        val metadata = File(dataFolder, "metadata").apply { if (!exists()) mkdir() }
        val blockMetadata = File(metadata, "blocks").apply { if (!exists()) mkdir() }

        val map = mutableMapOf<Location, BattleBlockData>()
        for (folder in blockMetadata.listFiles() ?: arrayOf()) {
            if (!folder.isDirectory) continue

            for (chunkFile in folder.listFiles() ?: arrayOf()) {
                if (!chunkFile.isFile) continue

                try {
                    val marked = AtomicBoolean(false)
                    BukkitObjectInputStream(chunkFile.inputStream()).use { stream ->
                        val m = (stream.readObject() as MutableMap<Location, BattleBlockData>)
                            .filterKeys { !it.block.isEmpty && !it.block.isLiquid }

                        if (m.isEmpty())
                            marked.set(true)
                        else
                            map.putAll(m)
                    }

                    if (marked.get()) chunkFile.delete()
                } catch (e: EOFException) {
                    chunkFile.delete()
                }
            }
        }

        return map
    }

    private fun loadMetadata() {
//        val metadata = File(dataFolder, "metadata").apply { if (!exists()) mkdir() }

        // Block Metadata
        CardUtils.BLOCK_DATA.putAll(loadBlockMetadata().onEach { (location, data) ->
            if (data.attributes.containsKey("attachments")) {
                val attachments = data.attributes["attachments"] as? List<UUID> ?: listOf()

                attachments.mapNotNull { id -> location.world.entities.firstOrNull { it.uniqueId == id } }
                    .forEach {
                        it.setMetadata("battlecards:block_attachment", FixedMetadataValue(this@BattleCards, true))
                    }
            }
        })
    }

    fun saveMetadata() {
        val metadata = File(dataFolder, "metadata").apply { if (!exists()) mkdir() }

        // Block Metadata

        val blockMetadata = File(metadata, "blocks").apply { if (!exists()) mkdir() }
        val splitBlockData: MutableMap<File, MutableMap<Location, BattleBlockData>> = mutableMapOf()

        for ((location, data) in CardUtils.BLOCK_DATA) {
            if (location.world == null) continue
            val parent = File(blockMetadata, location.world.uid.toString()).apply { if (!exists()) mkdir() }
            val file = File(parent, "bd.${location.chunk.x}.${location.chunk.z}.dat").apply { if (!exists()) createNewFile() }

            splitBlockData.putIfAbsent(file, mutableMapOf())
            splitBlockData[file]!![location] = data
        }

        for ((file, data) in splitBlockData) {
            BukkitObjectOutputStream(file.outputStream()).use {
                it.writeObject(data)
            }
        }
    }

    // BattleConfig Implementation

    companion object {

        @JvmStatic
        val cardRecipes: MutableMap<BattleCardType, ShapedRecipe> = mutableMapOf()
    }

    val cards: MutableSet<Class<out BattleCard<*>>> = mutableSetOf()

    val equipment: MutableSet<CardEquipment> = mutableSetOf()

    override val registeredCards: Set<Class<out BattleCard<*>>>
        get() = ImmutableSet.copyOf(cards)

    override val registeredEquipment: Set<CardEquipment>
        get() = ImmutableSet.copyOf(equipment)

    override fun registerCard(card: Class<out BattleCard<*>>) {
        if (cards.contains(card)) throw IllegalArgumentException("Card ${card.simpleName} already registered")
        val type = card.getAnnotation(Type::class.java).type

        if (type != BattleCardType.BASIC && type.craftingMaterial == Material.AIR) throw IllegalStateException("$type is not available on this Minecraft Version")

        cards.add(card)

        if (type != BattleCardType.BASIC)
            Bukkit.addRecipe(createShapedRecipe("card_${card.simpleName.lowercase()}", CardGenerator.toItem(type.createCardData())).apply {
                shape("SSS", "SMS", "SSS")

                setIngredient('M', type.craftingMaterial)

                val shard = cardShard(type.rarity)
                exactChoice(this, 'S', shard)

                try {
                    javaClass.getMethod("setGroup", String::class.java).invoke(this, "battlecards:cards")
                } catch (ignored: NoSuchMethodException) {}

                cardRecipes[type] = this
            })
    }

    override fun registerEquipment(equipment: CardEquipment) {
        if (this.equipment.contains(equipment)) throw IllegalArgumentException("Equipment ${equipment.name} already registered")
        this.equipment.add(equipment)
        Items.PUBLIC_ITEMS[equipment.name.lowercase()] = equipment.itemStack
    }

    override fun get(key: String): String {
        val p = Properties()
        val lang = if (language.equals("en", ignoreCase = true)) "" else "_$language"

        return try {
            javaClass.getResourceAsStream("/lang/battlecards$lang.properties").use { str ->
                if (str == null) return "Unknown Value"

                p.load(str)
                str.close()

                ChatColor.translateAlternateColorCodes('&', p.getProperty(key, "Unknown Value"))
            }
        } catch (e: IOException) {
            print(e)
            "Unknown Value"
        }
    }

    override fun isAvailable(type: BattleCardType): Boolean =
        registeredCards.firstOrNull { it.getAnnotation(Type::class.java).type == type } != null || type == BattleCardType.BASIC

    override fun createCardData(type: BattleCardType): Card {
        val clazz = registeredCards.firstOrNull { it.getAnnotation(Type::class.java).type == type } ?: throw IllegalStateException("$type is not available on this Minecraft Version")
        return ICard(
            clazz,
            type,
            System.currentTimeMillis()
        )
    }

}