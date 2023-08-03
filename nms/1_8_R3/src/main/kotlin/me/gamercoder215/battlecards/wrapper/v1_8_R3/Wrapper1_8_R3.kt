package me.gamercoder215.battlecards.wrapper.v1_8_R3

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.PACKET_INJECTOR_ID
import net.md_5.bungee.api.chat.BaseComponent
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.*
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

@Suppress("unchecked_cast")
internal class Wrapper1_8_R3 : Wrapper {

    override fun setEntityNBT(entity: org.bukkit.entity.Entity, key: String, value: Any) {
        val nms = (entity as CraftEntity).handle
        val nbt = NBTTagCompound()
        nms.e(nbt)

        val root = nbt.getCompound(NBTWrapper.ROOT)
        when (value) {
            is String, is Class<*> -> root.setString(key, value.toString())
            is Int -> root.setInt(key, value)
            is Double -> root.setDouble(key, value)
            is Float -> root.setFloat(key, value)
            is Boolean -> root.setBoolean(key, value)
            is Long -> root.setLong(key, value)
            is Short -> root.setShort(key, value)
            is ByteArray -> root.setByteArray(key, value)
            else -> throw IllegalArgumentException("Unsupported NBT type: ${value.javaClass}")
        }
        nbt[NBTWrapper.ROOT] = root

        nms.f(nbt)
    }

    override fun getEntityNBT(entity: org.bukkit.entity.Entity, key: String): Any? {
        val nms = (entity as CraftEntity).handle
        val nbt = NBTTagCompound()
        nms.e(nbt)

        val root = nbt.getCompound(NBTWrapper.ROOT)
        val tag = root.get(key) ?: return null

        return when (tag) {
            is NBTTagString -> tag.a_()
            is NBTTagInt -> tag.e()
            is NBTTagDouble -> tag.h()
            is NBTTagFloat -> tag.h()
            is NBTTagByte -> tag.f() == 1.toByte()
            is NBTTagLong -> tag.d()
            is NBTTagShort -> tag.f()
            is NBTTagByteArray -> tag.c()
            else -> throw IllegalArgumentException("Unsupported NBT type: ${tag.javaClass}")
        }
    }

    override fun getCommandVersion(): Int = 1

    override fun sendActionbar(player: Player, message: String) {
        val packet = PacketPlayOutChat(ChatComponentText(message), 2.toByte())
        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    override fun sendActionbar(player: Player, component: BaseComponent) {
        sendActionbar(player, component.toLegacyText())
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        // Withers do not have boss bars until 1.9
    }

    fun toNMS(attribute: CardAttribute): AttributeBase? {
        return when (attribute) {
            CardAttribute.MAX_HEALTH -> GenericAttributes.maxHealth
            CardAttribute.ATTACK_DAMAGE -> GenericAttributes.ATTACK_DAMAGE
            CardAttribute.KNOCKBACK_RESISTANCE -> GenericAttributes.c
            CardAttribute.SPEED -> GenericAttributes.MOVEMENT_SPEED
            CardAttribute.FOLLOW_RANGE -> GenericAttributes.FOLLOW_RANGE
            else -> null
        } as? AttributeBase
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms = (en as CraftCreature).handle
        EntityLiving::class.java.getDeclaredField("drops").apply { isAccessible = true }[nms] = emptyList<ItemStack>()

        for (entry in card.statistics.attributes) {
            val attribute = toNMS(entry.key) ?: continue
            val value = entry.value

            var handle = nms.getAttributeInstance(attribute)
            if (handle == null) {
                val attributesF = AttributeMapBase::class.java.getDeclaredField("b")
                attributesF.isAccessible = true
                val attributes = attributesF.get(nms) as MutableMap<String, AttributeInstance>

                handle = AttributeModifiable(nms.attributeMap, attribute)
                attributes[attribute.name] = handle
            }

            handle.value = value
        }

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_8_R3(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_8_R3(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_8_R3(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms, true))

        val tag = NBTTagCompound()
        nms.b(tag)
        tag.setBoolean("battlecard", true)
        nms.a(tag)

        if (nms is EntityWither)
            object : BukkitRunnable() {
                override fun run() {
                    if (en.isDead)
                        return cancel()

                    for (i in 0..2) {
                        val alt = nms.world.entityList.firstOrNull { it.id == nms.s(i) }?.bukkitEntity ?: continue

                        if ((alt is Player && !BattleConfig.config.cardAttackPlayers) || (alt !is Player && !alt.isCard))
                            nms.b(i, 0)
                    }
                }
            }.runTaskTimer(BattleConfig.plugin, 0L, 1L)
    }

    override fun <T : Creature> spawnMinion(clazz: Class<T>, ownerCard: IBattleCard<*>): T {
        val card = ownerCard.entity
        val en = card.world.spawn(card.location, clazz)

        en.isCustomNameVisible = true
        en.customName = "${ownerCard.rarity.color}${ownerCard.name}'s Minion"

        val equipment = en.equipment!!
        equipment.itemInHandDropChance = 0F
        equipment.helmetDropChance = 0F
        equipment.chestplateDropChance = 0F
        equipment.leggingsDropChance = 0F
        equipment.bootsDropChance = 0F

        en.target = ownerCard.target

        val nms = (en as CraftCreature).handle

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_8_R3(nms, ownerCard))

        nms.targetSelector.a(1, CardMasterHurtByTargetGoal1_8_R3(nms, ownerCard))
        nms.targetSelector.a(2, CardMasterHurtTargetGoal1_8_R3(nms, ownerCard))
        nms.targetSelector.a(3, CardOwnerHurtByTargetGoal1_8_R3(nms, ownerCard))
        nms.targetSelector.a(4, CardOwnerHurtTargetGoal1_8_R3(nms, ownerCard))
        nms.targetSelector.a(5, PathfinderGoalHurtByTarget(nms, true))

        ownerCard.minions.add(en)
        return en
    }

    override fun addFollowGoal(entity: LivingEntity, ownerCard: IBattleCard<*>) {
        val handle = (entity as CraftLivingEntity).handle as? EntityInsentient ?: return
        handle.goalSelector.a(2, FollowCardOwner1_8_R3(handle, ownerCard))
    }

    private fun removeGoals(goalSelector: PathfinderGoalSelector, targetSelector: PathfinderGoalSelector) {
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(goalSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }
        goals.filter {
            it is PathfinderGoalAvoidTarget<*> || it is PathfinderGoalRestrictSun || it is PathfinderGoalFleeSun || it is PathfinderGoalBeg || it is PathfinderGoalBreed
        }.forEach { goalSelector.a(it) }

        val targets = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(targetSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }
        targets.filter {
            it is PathfinderGoalNearestAttackableTarget<*> || it is PathfinderGoalDefendVillage
        }.forEach { targetSelector.a(it) }
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_8_R3(item)
    }

    override fun isCard(en: Creature): Boolean {
        val tag = NBTTagCompound()
        (en as CraftCreature).handle.b(tag)
        return tag.getBoolean("battlecard")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_8_R3(id, name, size)
    }

    private fun toNMS(particle: BattleParticle): EnumParticle {
        return when (particle) {
            BattleParticle.CLOUD -> EnumParticle.CLOUD
            else -> throw IllegalArgumentException("Invalid particle: $particle")
        }
    }

    override fun spawnParticle(
        particle: BattleParticle, location: Location, count: Int,
        dX: Double, dY: Double, dZ: Double,
        speed: Double, force: Boolean
    ) {
        if (location.world == null) return

        val packet = PacketPlayOutWorldParticles(toNMS(particle), force, location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), dX.toFloat(), dY.toFloat(), dZ.toFloat(), speed.toFloat(), count)
        Bukkit.getOnlinePlayers().forEach{ (it as CraftPlayer).handle.playerConnection.sendPacket(packet) }
    }

    override fun getDefaultAttribute(type: EntityType, attribute: CardAttribute): Double {
        val w = Bukkit.getWorlds()[0] as CraftWorld
        val creature = w.createEntity(Location(w, 0.0, 0.0, 0.0), type.entityClass) as? EntityLiving ?: throw AssertionError("Failed to create dummy creature")

        return creature.getAttributeInstance(toNMS(attribute)).b()
    }

    private fun removeAttackGoals(entity: EntityCreature) {
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(entity.goalSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }

        goals.filter {
            it is PathfinderGoalMeleeAttack || it is PathfinderGoalArrowAttack
        }.forEach { entity.goalSelector.a(it) }
    }

    override fun setAttackType(entity: Creature, attackType: CardAttackType) {
        val nms = (entity as CraftCreature).handle
        removeAttackGoals(nms)

        nms.goalSelector.a(3, when (attackType) {
            CardAttackType.MELEE -> PathfinderGoalMeleeAttack(nms, 1.0, false)
            CardAttackType.BOW -> {
                if (nms !is IRangedEntity) throw UnsupportedOperationException("Invalid Ranged Entity ${entity::class.java.simpleName}")

                PathfinderGoalArrowAttack(nms, 1.0, 20, 15.0F)
            }
            CardAttackType.CROSSBOW -> throw AssertionError("Using Crossbow AttackType below 1.14")
        })
    }

    override fun getAttackType(entity: Creature): CardAttackType {
        val nms = (entity as CraftCreature).handle
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(nms.goalSelector).run {
            (this as Set<Any>).map {
                Pair(
                    it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal },
                    it::class.java.getDeclaredField("b").apply { isAccessible = true }.get(it).run { this as Int }
                )
            }
        }
        val targetGoals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(nms.targetSelector).run {
            (this as Set<Any>).map {
                Pair(
                    it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal },
                    it::class.java.getDeclaredField("b").apply { isAccessible = true }.get(it).run { this as Int }
                )
            }
        }

        return (goals + targetGoals)
            .sortedBy { it.second }
            .firstNotNullOf {
                when (it.first) {
                    is PathfinderGoalMeleeAttack -> CardAttackType.MELEE
                    is PathfinderGoalArrowAttack -> CardAttackType.BOW
                    else -> null
                } ?: CardAttackType.MELEE
            }
    }

    override fun getYBodyRot(entity: org.bukkit.entity.LivingEntity): Float = (entity as CraftLivingEntity).handle.aI

    override fun addPacketInjector(p: Player) {
        val sp = (p as CraftPlayer).handle
        val ch = sp.playerConnection.networkManager.channel

        if (ch.pipeline().get(PACKET_INJECTOR_ID) != null) return
        ch.pipeline().addAfter("decoder", PACKET_INJECTOR_ID, PacketHandler1_8_R3(p))

        PacketHandler1_8_R3.PACKET_HANDLERS[p.uniqueId] = handler@{ packet ->
            if (packet is PacketPlayInSteerVehicle) {
                val vehicle = p.vehicle as? CraftCreature ?: return@handler
                val card = vehicle.card ?: return@handler
                if (!card.isRideable) return@handler

                vehicle.handle.apply {
                    yaw = p.location.yaw; f(p.location.yaw)
                }

                val vector = (p.location.apply { pitch = 0F }.direction * packet.b()).plus(Vector(0, 1, 0).crossProduct(p.location.apply { pitch = 0F }.direction) * packet.a()) * card.statistics.speed * 1.1
                vehicle.handle.move(vector.x, vector.y, vector.z)
            }
        }
    }

    override fun removePacketInjector(p: Player) {
        val sp = (p as CraftPlayer).handle
        val ch = sp.playerConnection.networkManager.channel

        if (ch.pipeline().get(PACKET_INJECTOR_ID) == null) return
        ch.pipeline().remove(PACKET_INJECTOR_ID)
    }

}