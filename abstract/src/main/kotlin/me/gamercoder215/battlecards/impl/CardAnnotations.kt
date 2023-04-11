package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.Rarity
import org.bukkit.Material
import java.lang.annotation.Inherited
import java.util.function.BiFunction

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class CardDetails(val id: String, val name: String, val description: String, val rarity: Rarity)

// Attributes

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class Settings(
    val float: Boolean = false,
)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class Attributes(
    val maxHealth: Double,
    val attackDamage: Double,
    val attackKnockback: Double,
    val defense: Double,
    val speed: Double,
    val knockbackResistance: Double
)

@Inherited
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class AttributesModifier(
    val attribute: CardAttribute,
    val operation: CardAttributeOperation,
    val value: Double = Double.NaN,
)

enum class CardAttribute {
    MAX_HEALTH,
    ATTACK_DAMAGE,
    ATTACK_KNOCKBACK,
    DEFENSE,
    SPEED,
    KNOCKBACK_RESISTANCE
}

enum class CardAttributeOperation(
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
@Target(AnnotationTarget.FUNCTION)
internal annotation class CardAbility(val name: String, val description: String)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class Defensive(val chance: Double = 1.0)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class Offensive(val chance: Double = 1.0)

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class Passive(val interval: Long)

// Visuals

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
internal annotation class BlockAttachment(
    val material: Material,
    // Uses Directional (^ ^ ^) offsets
    val offsetX: Double,
    val offsetY: Double,
    val offsetZ: Double,
    val small: Boolean = false
)
