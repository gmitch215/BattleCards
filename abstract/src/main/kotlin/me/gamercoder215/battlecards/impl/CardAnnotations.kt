package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleCardType
import org.bukkit.ChatColor
import org.bukkit.Material
import java.lang.annotation.Inherited
import java.util.function.BiFunction

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Type(
    val type: BattleCardType
)

// Attributes

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Attributes(
    val maxHealth: Double,
    val attackDamage: Double,
    val defense: Double,
    val speed: Double,
    val knockbackResistance: Double
)

@Inherited
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AttributesModifier(
    val attribute: CardAttribute,
    val operation: CardOperation,
    val value: Double = Double.NaN,
    val max: Double = Double.MAX_VALUE
)

enum class CardAttribute {
    MAX_HEALTH,
    ATTACK_DAMAGE,
    DEFENSE,
    SPEED,
    KNOCKBACK_RESISTANCE

    ;

    fun getAttribute(attributes: Attributes): Double {
        return when (this) {
            MAX_HEALTH -> attributes.maxHealth
            ATTACK_DAMAGE -> attributes.attackDamage
            DEFENSE -> attributes.defense
            SPEED -> attributes.speed
            KNOCKBACK_RESISTANCE -> attributes.knockbackResistance
        }
    }
}

enum class CardOperation(
    private val apply: BiFunction<Double, Double, Double>
) : BiFunction<Double, Double, Double> by apply {
    ADD({ a, b -> a + b }),
    MULTIPLY({ a, b -> a * b }),
    DIVIDE({ a, b -> a / b }),
    SUBTRACT({ a, b -> a - b }),
}

// Abilities

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class CardAbility(
    val name: String,
    val color: ChatColor = ChatColor.WHITE,
    val desc: String = "<desc>"
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Defensive(
    val chance: Double = 1.0,
    val operation: CardOperation = CardOperation.ADD,
    val value: Double = Double.NaN,
    val max: Double = 1.0
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Damage(
    val chance: Double = 1.0,
    val operation: CardOperation = CardOperation.ADD,
    val value: Double = Double.NaN,
    val max: Double = 1.0
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Offensive(
    val chance: Double = 1.0,
    val operation: CardOperation = CardOperation.ADD,
    val value: Double = Double.NaN,
    val max: Double = 1.0
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Passive(
    val interval: Long,
    val operation: CardOperation = CardOperation.ADD,
    val value: Long = Long.MIN_VALUE,
    val max: Long = Long.MAX_VALUE,
    val min: Long = 1
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UnlockedAt(val level: Int)

// Visuals

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BlockAttachment(
    val material: Material,
    // Uses Directional (^ ^ ^) offsets
    val offsetX: Double,
    val offsetY: Double,
    val offsetZ: Double,
    val small: Boolean = false
)

// User Grants

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UserDefensive(
    val chance: Double = 1.0,
    val operation: CardOperation = CardOperation.ADD,
    val value: Double = Double.NaN,
    val max: Double = 1.0
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UserOffensive(
    val chance: Double = 1.0,
    val operation: CardOperation = CardOperation.ADD,
    val value: Double = Double.NaN,
    val max: Double = 1.0
)

// Listener

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Listener