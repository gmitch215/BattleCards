package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.CardQuest
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Potion
import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.messages.format
import me.gamercoder215.battlecards.messages.get
import me.gamercoder215.battlecards.messages.sendError
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.inventory.CardGenerator.generationColors
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.ChatPaginator
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

@Suppress("unchecked_cast")
object Generator {

    fun genGUI(size: Int, name: String?): BattleInventory {
        return genGUI("", size, name)
    }

    fun genGUI(key: String, size: Int, name: String?): BattleInventory {
        if (size < 9 || size > 54) throw IllegalArgumentException("Size must be between 9 and 54")
        if (size % 9 > 0) throw IllegalArgumentException("Size must be a multiple of 9")

        val inv = w.createInventory(key, name ?: "", size)
        val bg: ItemStack = Items.GUI_BACKGROUND
        if (size < 27) return inv

        for (i in 0..8) inv[i] = bg
        for (i in size - 9 until size) inv[i] = bg

        var i = 1
        while (i < floor(size.toDouble() / 9.0) - 1) {
            inv[i * 9] = bg
            inv[(i + 1) * 9 - 1] = bg
            i++
        }

        return inv
    }

    fun generatePluginInfo(): BattleInventory {
        val inv = genGUI(27, get("menu.plugin_info"))

        inv[4] = BattleMaterial.PLAYER_HEAD.findStack().apply {
            itemMeta = (itemMeta as SkullMeta).apply {
                displayName = "$AQUA${get("constants.created_by")}"
                owner = "GamerCoder"
            }
        }

        inv[12] = Items.head("github") {
            itemMeta = itemMeta.apply {
                displayName = "${DARK_GRAY}GitHub"
            }
        }.nbt { nbt ->
            nbt.id = "plugin_info:link"
            nbt["link"] = "https://github.com/GamerCoder215/BattleCards"
        }

        inv[14] = Items.head("discord") {
            itemMeta = itemMeta.apply {
                displayName = "${BLUE}Discord"
            }
        }.nbt {  nbt ->
            nbt.id = "plugin_info:link"
            nbt["link"] = "https://discord.gg/WVFNWEvuqX"
        }

        while (inv.firstEmpty() != -1)
            inv[inv.firstEmpty()] = Items.GUI_BACKGROUND

        return inv
    }

    fun generateCardInfo(card: Card): BattleInventory {
        val inv = genGUI(27, get("menu.card.info"))
        inv.isCancelled = true
        inv["card"] = card

        inv[4] = card.icon

        val info = CardGenerator.generateCardInfo(card)
        val stats = CardGenerator.generateCardStatistics(card)

        if (stats != null) {
            inv[12] = info
            inv[14] = stats
        } else
            inv[13] = info

        inv[21] =
            if (card.level >= 10)
                ItemStack(Material.DIAMOND_SWORD).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "$AQUA${get("menu.card_equipment")}"

                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    }
                }.nbt { nbt ->
                    nbt.id = "card:info_item"
                    nbt["type"] = "equipment"
                }
            else Items.locked(10)

        inv[22] =
            if (card.level >= floor(card.maxCardLevel / 2.0))
                ItemStack(Material.CHEST).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "$GOLD${get("menu.card_quests")}"
                    }
                }.nbt { nbt ->
                    nbt.id = "card:info_item"
                    nbt["type"] = "quests"
                }
            else Items.locked(floor(card.maxCardLevel / 2.0).toInt())

        if (card.type != BattleCardType.BASIC)
            inv[23] = ItemStack(Material.BOOK).apply {
                itemMeta = itemMeta.apply {
                    displayName = "$YELLOW${get("menu.card_catalogue.view_in_catalogue")}"
                }
            }.nbt { nbt ->
                nbt.id = "card:info_item"
                nbt["type"] = "catalogue"
            }

        while (inv.firstEmpty() != -1)
            inv[inv.firstEmpty()] = Items.GUI_BACKGROUND

        return inv
    }

    fun generateCardQuests(card: Card, quest: CardQuest? = null): BattleInventory {
        val inv: BattleInventory

        if (quest == null) {
            inv = genGUI(27, get("menu.card_quests"))
            inv["card"] = card
            inv["back"] = Consumer { p: Player -> p.openInventory(generateCardInfo(card)) }

            for (q in CardQuest.entries)
                inv.addItem(ItemStack(q.icon).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "$GOLD${get("menu.card_quests.${q.name.lowercase()}")}"

                        if (card.getQuestLevel(q) > 0)
                            lore = listOf(
                                "$AQUA${format(get("constants.level"), card.getQuestLevel(q).formatInt())}"
                            )

                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                    }
                }.nbt { nbt ->
                    nbt.id = "card:quest_item"
                    nbt["quest"] = q.ordinal
                })

            inv[22] = Items.back()
        } else {
            val count = ceil(quest.maxLevel / progressString.size.toDouble()).toInt()
            val invs = mutableListOf<BattleInventory>()

            var lvl = 1
            val currentLvl = card.getQuestLevel(quest)

            for (page in 0 until count) {
                val gui = genGUI(54, "${get("menu.card_quests")} | ${get("menu.card_quests.${quest.name.lowercase()}")}")
                gui["page"] = page
                gui["back"] = Consumer { p: Player -> p.openInventory(generateCardQuests(card)) }

                gui[4] = card.icon
                gui[9] = ItemStack(quest.icon).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "$GOLD${get("menu.card_quests.${quest.name.lowercase()}")}"

                        lore = listOf(
                            "$AQUA${format(get("constants.level"), card.getQuestLevel(quest).formatInt())}",
                            "$YELLOW${format(get("constants.total_experience_reward"), quest.getTotalExperience(card).withSuffix())}"
                        )

                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                    }
                }

                if (count > 1) {
                    if (page < count - 1)
                        gui[51] = Items.next()

                    if (page > 0)
                        gui[47] = Items.prev()
                }

                for (index in progressString) {
                    if (lvl > quest.maxLevel) break

                    gui[index] = when {
                        lvl == quest.maxLevel -> ItemStack(Material.BEACON)
                        lvl == (currentLvl + 1) -> BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack()
                        lvl < (currentLvl + 1) -> BattleMaterial.LIME_STAINED_GLASS_PANE.findStack()
                        else -> BattleMaterial.RED_STAINED_GLASS_PANE.findStack()
                    }.apply {
                        val unlocked = lvl <= currentLvl
                        val color = if (unlocked) GREEN else AQUA

                        itemMeta = itemMeta.apply {
                            displayName = "$color${format(get("constants.level"), lvl.formatInt())}"
                            lore = listOf(
                                "$YELLOW${quest.getLocalizedProgress(card, lvl)}",
                                " ",
                                "$DARK_GREEN${format(get("constants.completed"), "${quest.getProgressPercentage(card, lvl).times(100).format()}%")}",
                                "${if (unlocked) DARK_AQUA else BLUE}${quest.getExperienceReward(card, lvl).withSuffix()} XP"
                            )

                            if (unlocked) addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                            addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                        }
                    }

                    lvl++
                }

                gui[49] = Items.back()

                while (gui.firstEmpty() != -1)
                    gui[gui.firstEmpty()] = Items.GUI_BACKGROUND

                invs.add(gui)
            }

            invs.forEach { it["stored"] = invs }

            inv = invs[0]
        }

        inv.isCancelled = true
        inv[4] = card.icon

        return inv
    }

    private val progressString: List<Int> = listOf(
        10 nineTo 37,
        37..39,
        39 nineTo 12,
        12..14,
        14 nineTo 41,
        41..43,
        43 nineTo 16,
        17
    ).map {
        when (it) {
            is Int -> listOf(it)
            is IntRange -> it.toList()
            is Collection<*> -> it as Collection<Int>
            else -> throw IllegalArgumentException("Invalid progress type")
        }
    }.flatten().distinct()

    private infix fun Int.nineTo(other: Int): List<Int> {
        val remainder = this % 9

        val (start, reverse) = if (this > other) other to true else this to false
        val end = if (this > other) this else other

        return (start..end).filter { (it % 9) - remainder == 0 }.run { if (reverse) reversed() else this }
    }

    fun generateCardEquipment(card: Card): BattleInventory {
        val equipment = (card as ICard).cardEquipment
        val slots = card.statistics.equipmentSlots

        val inv = genGUI("card_equipment", 18, get("menu.card_equipment"))
        inv[0, 1, 7] = Items.GUI_BACKGROUND

        val onClose = BiConsumer { p: Player, inventory: BattleInventory ->
            val c = inventory["card", ICard::class.java] ?: return@BiConsumer

            val sent = AtomicBoolean(false)

            val dispose = mutableListOf<ItemStack>()

            val items = listOf(2, 3, 4, 5, 6).map {
                it to inventory[it]
            }.map { pair ->
                if (pair.second?.nbt?.hasTag("_cancel") == true) return@map pair.first to null

                if (pair.second != null) {
                    val item = pair.second!!

                    if (item.amount > 1)
                        dispose.add(item.clone().apply { amount -= 1 })

                    if (item.nbt.id != "card_equipment")
                        dispose.add(item.clone())
                }

                pair.first to (BattleConfig.config.registeredEquipment.firstOrNull { it.name == pair.second?.nbt?.getString("name") })
            }.toMap()

            items.forEach {
                val eq = it.value

                if (eq == null) c.cardEquipment.remove(it.key)
                else c.cardEquipment[it.key] = eq
            }
            p.itemInHand = c.itemStack

            dispose.forEach { item ->
                if (p.inventory.firstEmpty() == -1) {
                    p.world.dropItemNaturally(p.location, item)
                    sent.set(true)
                }
                else
                    p.inventory.addItem(item)
            }

            if (sent.get())
                p.sendError("error.card.equipment.input_1")
        }

        inv["card"] = card
        inv["back"] = Consumer { p: Player ->
            onClose.accept(p, inv)
            p.openInventory(generateCardInfo(card))
        }
        inv["on_close"] = onClose

        for (i in 1..5) {
            val slot = i + 1
            val item = equipment[slot]?.itemStack

            if (i <= slots)
                inv[slot] = item
            else {
                val req = 5 + (15 - (card.rarity.ordinal * 2)).coerceAtLeast(5).times(i - 1)

                inv[slot] = Items.locked(req)
            }
        }

        inv[8] = generateEffectiveModifiers(equipment.values)

        inv[9..17] = Items.GUI_BACKGROUND
        inv[13] = Items.back()

        return inv
    }

    fun generateEffectiveModifiers(equipment: Iterable<CardEquipment>) = ItemStack(BattleMaterial.MAP.find()).apply {
        itemMeta = itemMeta.apply {
            displayName = "$BLUE${get("constants.effective_modifiers")}"

            val lore = mutableListOf<String>()
            val modifiers = equipment.map { it.mods }.run {
                val map = mutableMapOf<CardAttribute, Double>()

                for (mods in this)
                    for ((attribute, value) in mods)
                        map[attribute] = -(1.minus(map[attribute] ?: 0.0)) + value

                map
            }.apply { CardAttribute.entries.forEach { putIfAbsent(it, 0.0) } }

            if (!modifiers.all { it.value == 0.0 })
                lore.add(" ")
            else
                lore.add("$WHITE${get("constants.none")}")

            for ((attribute, mod) in modifiers) {
                if (mod == 0.0) continue

                val modS = mod.times(100).run {
                    if (this < 0) "$RED${this.format()}%"
                    else "$GREEN+${this.format()}%"
                }

                lore.add(format(get(attribute.displayName), modS))
            }

            val effects = equipment.map { it.effects }.flatten().run {
                val effects = mutableSetOf<Potion>()

                for (type in PotionEffectType.values()) {
                    val potions = filter { potion -> potion.status != Potion.Status.USER_ONLY && potion.type == type }
                    if (potions.isEmpty()) continue

                    effects.add(potions.maxByOrNull { it.amplifier } ?: continue)
                }

                effects
            }

            if (effects.isNotEmpty()) {
                lore.add(" ")

                for (effect in effects)
                    lore.add("${effect.type.prefix}${effect.type.name.replace('_', ' ').capitalizeFully()} ${effect.amplifier.plus(1).toRoman()} (${get("constants.card_equipment.potion_status.${effect.status.name.lowercase()}")})")
            }

            this.lore = lore
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
    }.nbt { nbt ->
        nbt.addTag("_cancel")
    }

    fun generateCatalogue(original: Card, type: BattleCardType = original.type): BattleInventory {
        val card = type().apply { experience = maxCardExperience } as ICard
        val inv = genGUI(45, format(get("menu.catalogue"), card.name))
        inv.isCancelled = true
        inv["card"] = card

        inv[4] = card.icon

        val rideable = card.isRideable
        inv[12] = ItemStack(Material.ARMOR_STAND).apply {
            itemMeta = itemMeta.apply {
                displayName = "${generationColors[type.generation]}${format(get("constants.card.generation"), type.generation.toRoman())}"
                lore = listOf(
                    "$GOLD${get("constants.card.rideable")} ${if (rideable) "$GREEN${get("constants.yes")}" else "$RED${get("constants.no")}" }"
                )
            }
        }

        val attributes = mapOf(
            "health" to (RED to card.statistics.maxHealth),
            "damage" to (DARK_RED to card.statistics.attackDamage * 2),
            "defense" to (GREEN to card.statistics.defense * 1.75),
            "speed" to (DARK_AQUA to card.statistics.speed.pow(500.0)),
            "knockback_resistance" to (BLUE to card.statistics.knockbackResistance)
        )
        val best = attributes.maxBy { it.value.second }
        inv[13] = ItemStack(Material.IRON_HELMET).apply {
            itemMeta = itemMeta.apply {
                displayName = "$YELLOW${get("constants.card.attr.best")}"
                lore = mutableListOf(
                    "${best.value.first}${get("constants.card.attr.${best.key}")}",
                    " "
                ).apply {
                    addAll(ChatPaginator.wordWrap(get("constants.card.attr.${best.key}.desc"), 30).map { "$GRAY$it" })
                }

                addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            }
        }

        fun formatCount(count: Double): String {
            val long = ceil(count).toLong().coerceAtLeast(1)
            val str = long.formatInt()
            return if (long == Long.MAX_VALUE) "$str+"
            else str
        }

        val maxCardExperience = card.maxCardExperience
        val zombies = formatCount(maxCardExperience / 20.0)
        val endermen = formatCount(maxCardExperience / 40.0)
        val withers = formatCount(maxCardExperience / 300.0)
        val wardens = formatCount(maxCardExperience / 500.0)
        val passive = formatCount(maxCardExperience / BattleConfig.config.growthPassiveAmount)
        val smallBook = formatCount(maxCardExperience / Items.SMALL_EXPERIENCE_BOOK.nbt.getDouble("amount"))
        val largeBook = formatCount(maxCardExperience / Items.LARGE_EXPERIENCE_BOOK.nbt.getDouble("amount"))
        val hugeBook = formatCount(maxCardExperience / Items.HUGE_EXPERIENCE_BOOK.nbt.getDouble("amount"))

        inv[14] = ItemStack(Material.BOOK).apply {
            itemMeta = itemMeta.apply {
                displayName = "$LIGHT_PURPLE${get("constants.card.to_max")}"
                lore = listOf(
                    "$DARK_GREEN-> ${maxCardExperience.withSuffix()} XP",
                    " ",
                    "$DARK_GREEN${format(get("constants.card.to_max.zombies"), zombies)}",
                    "$DARK_PURPLE${format(get("constants.card.to_max.endermen"), endermen)}",
                    "$DARK_GRAY${format(get("constants.card.to_max.withers"), withers)}",
                    "$BLUE${format(get("constants.card.to_max.wardens"), wardens)}",
                    "$RED${format(get("constants.card.to_max.passive"), passive)}",
                    " ",
                    "$GOLD${format(get("constants.card.to_max.small_experience_books"), smallBook)}",
                    "$GOLD${format(get("constants.card.to_max.large_experience_books"), largeBook)}",
                    "$GOLD${format(get("constants.card.to_max.huge_experience_books"), hugeBook)}"
                )
            }
        }

        inv[31] = ItemStack(BattleMaterial.CRAFTING_TABLE.find()).apply {
            itemMeta = itemMeta.apply {
                displayName = "$GOLD${get("menu.card_catalogue.view_crafting_recipe")}"
            }
        }.nbt { nbt ->
            nbt.id = "card_catalogue:crafting_recipe"
            nbt["type"] = type.name
        }

        inv["back"] = Consumer { p: Player -> p.openInventory(generateCardInfo(original)) }
        inv[37] = Items.back("action")

        return inv
    }

    fun generateCatalogue(equipment: CardEquipment): BattleInventory {
        val inv = genGUI(27, format(get("menu.catalogue"), equipment.name.replace("_", " ").capitalizeFully()))
        inv.isCancelled = true
        inv[12] = equipment.itemStack
        inv[14] = generateEffectiveModifiers(listOf(equipment))

        return inv
    }

}