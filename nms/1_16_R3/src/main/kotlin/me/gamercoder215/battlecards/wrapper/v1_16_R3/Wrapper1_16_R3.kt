package me.gamercoder215.battlecards.wrapper.v1_16_R3

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.PACKET_INJECTOR_ID
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_16_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey
import org.bukkit.entity.*
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

@Suppress("unchecked_cast", "KotlinConstantConditions")
internal class Wrapper1_16_R3 : Wrapper {

    override fun setEntityNBT(entity: Entity, key: String, value: Any) {
        val nms = (entity as CraftEntity).handle
        val nbt = NBTTagCompound()
        nms.save(nbt)

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

        nms.load(nbt)
    }

    override fun getEntityNBT(entity: Entity, key: String): Any? {
        val nms = (entity as CraftEntity).handle
        val nbt = NBTTagCompound()
        nms.save(nbt)

        val root = nbt.getCompound(NBTWrapper.ROOT)
        val tag = root.get(key) ?: return null

        return when (tag) {
            is NBTTagString -> tag.asString()
            is NBTTagInt -> tag.asInt()
            is NBTTagDouble -> tag.asDouble()
            is NBTTagFloat -> tag.asFloat()
            is NBTTagByte -> tag.asByte() == 1.toByte()
            is NBTTagLong -> tag.asLong()
            is NBTTagShort -> tag.asShort()
            is NBTTagByteArray -> tag.bytes
            else -> throw IllegalArgumentException("Unsupported NBT type: ${tag.javaClass}")
        }
    }

    private fun <T> registry(r: IRegistry<T>): RegistryMaterials<T> {
        return (Bukkit.getServer() as CraftServer).server.customRegistry.b(r.f()) as RegistryMaterials<T>
    }

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        boss.bossBar?.isVisible = visible
    }

    fun toNMS(key: NamespacedKey): MinecraftKey {
        return CraftNamespacedKey.toMinecraft(key)
    }

    fun toBukkit(attribute: CardAttribute): Attribute {
        return when (attribute) {
            CardAttribute.MAX_HEALTH -> Attribute.GENERIC_MAX_HEALTH
            CardAttribute.ATTACK_DAMAGE -> Attribute.GENERIC_ATTACK_DAMAGE
            CardAttribute.KNOCKBACK_RESISTANCE -> Attribute.GENERIC_KNOCKBACK_RESISTANCE
            CardAttribute.SPEED -> Attribute.GENERIC_MOVEMENT_SPEED
            CardAttribute.DEFENSE -> Attribute.GENERIC_ARMOR
            CardAttribute.FOLLOW_RANGE -> Attribute.GENERIC_FOLLOW_RANGE
        }
    }

    fun toNMS(attribute: Attribute): AttributeBase {
        return IRegistry.ATTRIBUTE.get(toNMS(attribute.key)) ?: throw NullPointerException("Attribute ${attribute.key} not found")
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms = (en as CraftCreature).handle
        EntityLiving::class.java.getDeclaredField("drops").apply { isAccessible = true }[nms] = emptyList<ItemStack>()

        for (entry in card.statistics.attributes) {
            val attribute = toNMS(toBukkit(entry.key))
            val value = entry.value

            var handle = nms.getAttributeInstance(attribute)
            if (handle == null) {
                val attributesF = AttributeMapBase::class.java.getDeclaredField("b")
                attributesF.isAccessible = true
                val attributes = attributesF.get(nms) as MutableMap<AttributeBase, AttributeModifiable>

                handle = AttributeModifiable(attribute) {}
                attributes[attribute] = handle
            }

            handle.value = value
        }

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_16_R3(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_16_R3(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_16_R3(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms))

        nms.addScoreboardTag("battlecards")

        if (nms is EntityWither)
            object : BukkitRunnable() {
                override fun run() {
                    if (en.isDead)
                        return cancel()

                    for (i in 0..2) {
                        val alt = nms.world.getEntity(nms.getHeadTarget(i))?.bukkitEntity ?: continue

                        if ((alt is Player && !BattleConfig.config.cardAttackPlayers) || (alt !is Player && !alt.isCard))
                            nms.setHeadTarget(i, 0)
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
        equipment.itemInMainHandDropChance = 0F
        equipment.itemInOffHandDropChance = 0F
        equipment.helmetDropChance = 0F
        equipment.chestplateDropChance = 0F
        equipment.leggingsDropChance = 0F
        equipment.bootsDropChance = 0F

        en.target = ownerCard.target

        val nms = (en as CraftCreature).handle

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_16_R3(nms, ownerCard))

        nms.targetSelector.a(1, CardMasterHurtByTargetGoal1_16_R3(nms, ownerCard))
        nms.targetSelector.a(2, CardMasterHurtTargetGoal1_16_R3(nms, ownerCard))
        nms.targetSelector.a(3, CardOwnerHurtByTargetGoal1_16_R3(nms, ownerCard))
        nms.targetSelector.a(4, CardOwnerHurtTargetGoal1_16_R3(nms, ownerCard))
        nms.targetSelector.a(5, PathfinderGoalHurtByTarget(nms))

        ownerCard.minions.add(en)
        return en
    }

    private fun removeGoals(goalSelector: PathfinderGoalSelector, targetSelector: PathfinderGoalSelector) {
        val field = PathfinderGoalSelector::class.java.getDeclaredField("d").apply { isAccessible = true }
        (field.get(goalSelector) as Set<PathfinderGoalWrapped>).map { it.j() }.filter {
            it is PathfinderGoalAvoidTarget<*> || it is PathfinderGoalRestrictSun || it is PathfinderGoalFleeSun || it is PathfinderGoalBeg || it is PathfinderGoalBreed
        }.forEach { goalSelector.a(it) }

        (field.get(targetSelector) as Set<PathfinderGoalWrapped>).map { it.j() }.filter {
            it is PathfinderGoalNearestAttackableTarget<*> || it is PathfinderGoalNearestAttackableTargetWitch<*> || it is PathfinderGoalNearestHealableRaider<*> || it is PathfinderGoalDefendVillage || it is PathfinderGoalUniversalAngerReset<*>
        }.forEach { targetSelector.a(it) }
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_16_R3(item)
    }

    override fun isCard(en: Creature): Boolean {
        return (en as CraftCreature).handle.scoreboardTags.contains("battlecards")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_16_R3(id, name, size)
    }

    override fun spawnParticle(
        particle: BattleParticle, location: Location, count: Int,
        dX: Double, dY: Double, dZ: Double,
        speed: Double, force: Boolean
    ) {
        if (location.world == null) return
        location.world!!.spawnParticle(Particle.valueOf(particle.name.uppercase()), location, count, dX, dY, dZ, speed)
    }

    private fun toNMS(type: EntityType): EntityTypes<*> {
        return IRegistry.ENTITY_TYPE[CraftNamespacedKey.toMinecraft(type.key)]
    }

    override fun getDefaultAttribute(type: EntityType, attribute: CardAttribute): Double {
        val supplier = AttributeDefaults.a(toNMS(type) as EntityTypes<out EntityLiving>)
        return supplier.b(toNMS(toBukkit(attribute)))
    }

    private fun removeAttackGoals(entity: EntityCreature) {
        val field = PathfinderGoalSelector::class.java.getDeclaredField("d").apply { isAccessible = true }
        (field.get(entity.goalSelector) as Set<PathfinderGoalWrapped>).map { it.j() }.filter {
            it is PathfinderGoalMeleeAttack || it is PathfinderGoalArrowAttack || it is PathfinderGoalBowShoot<*> || it is PathfinderGoalCrossbowAttack<*>
        }.forEach { entity.goalSelector.a(it) }
    }

    override fun setAttackType(entity: Creature, attackType: CardAttackType) {
        val nms = (entity as CraftCreature).handle
        removeAttackGoals(nms)

        nms.goalSelector.a(3, when (attackType) {
            CardAttackType.MELEE -> PathfinderGoalMeleeAttack(nms, 1.0, false)
            CardAttackType.BOW -> {
                if (nms !is EntityMonster) throw UnsupportedOperationException("Invalid Monster Type ${entity::class.java.simpleName}")
                if (nms !is IRangedEntity) throw UnsupportedOperationException("Invalid Ranged Type ${entity::class.java.simpleName}")

                PathfinderGoalBowShoot(nms, 1.0, 20, 15.0F)
            }
            CardAttackType.CROSSBOW -> {
                if (nms !is EntityMonster) throw UnsupportedOperationException("Invalid Monster Type ${entity::class.java.simpleName}")
                if (nms !is ICrossbow) throw UnsupportedOperationException("Invalid Crossbow Type ${entity::class.java.simpleName}")
                PathfinderGoalCrossbowAttack(nms,1.0, 15.0F)
            }
        })
    }

    override fun getAttackType(entity: Creature): CardAttackType {
        val nms = (entity as CraftCreature).handle

        val field = PathfinderGoalSelector::class.java.getDeclaredField("d").apply { isAccessible = true }
        val goals = (field.get(nms.goalSelector) as Set<PathfinderGoalWrapped>)
        val targetGoals = (field.get(nms.targetSelector) as Set<PathfinderGoalWrapped>)

        return (goals + targetGoals)
            .sortedBy { it.h() }
            .firstNotNullOf {
                when (it.j()) {
                    is PathfinderGoalMeleeAttack -> CardAttackType.MELEE
                    is PathfinderGoalBowShoot<*> -> CardAttackType.BOW
                    is PathfinderGoalCrossbowAttack<*> -> CardAttackType.CROSSBOW
                    else -> null
                } ?: CardAttackType.MELEE
            }
    }

    override fun getYBodyRot(entity: LivingEntity): Float = (entity as CraftLivingEntity).handle.aA

    override fun addPacketInjector(p: Player) {
        val sp = (p as CraftPlayer).handle
        val ch = sp.playerConnection.networkManager.channel

        if (ch.pipeline().get(PACKET_INJECTOR_ID) != null) return
        ch.pipeline().addAfter("decoder", PACKET_INJECTOR_ID, PacketHandler1_16_R3(p))

        PacketHandler1_16_R3.PACKET_HANDLERS[p.uniqueId] = handler@{ packet ->
            if (packet is PacketPlayInSteerVehicle) {
                val vehicle = p.vehicle ?: return@handler
                val card = vehicle.card ?: return@handler
                if (!card.isRideable) return@handler

                vehicle.setRotation(p.location.yaw, p.location.pitch)
                vehicle.velocity += (p.location.apply { pitch = 0F }.direction * packet.c()).plus(Vector(0, 1, 0).crossProduct(p.location.apply { pitch = 0F }.direction) * packet.b()) * (card.statistics.speed * 0.75) * if (vehicle.isOnGround) 1 else 0.3

                if (packet.d() && vehicle.isOnGround)
                    (vehicle as CraftCreature).handle.controllerJump.jump()
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