package blackjack.domain.player.state

import blackjack.domain.card.Card
import blackjack.domain.card.Score

sealed class PlayerState(open val hands: Hands) {
    fun isEmpty(): Boolean = hands.isEmpty()
    fun score(): Score = hands.score()
    abstract fun isFinished(): Boolean
    abstract fun stay(): PlayerState
    abstract fun plus(cards: List<Card>): PlayerState
}

sealed class Running(hands: Hands) : PlayerState(hands) {
    override fun isFinished(): Boolean = false
    override fun stay(): PlayerState = Stay(hands)
}

data class Started(override val hands: Hands = Hands.EMPTY) : Running(hands) {
    override fun plus(cards: List<Card>): PlayerState {
        val newHands = hands + cards
        if (newHands.isStart() && newHands.score().isMaxiMum()) {
            return BlackJack(newHands)
        }
        if (newHands.score().isBust()) {
            return Bust(newHands)
        }
        return Hit(newHands)
    }
}

data class Hit(override val hands: Hands) : Running(hands) {
    override fun plus(cards: List<Card>): PlayerState {
        val newHands = hands + cards
        if (newHands.score().isBust()) {
            return Bust(newHands)
        }
        return Hit(newHands)
    }
}

sealed class Finish(hands: Hands) : PlayerState(hands) {
    override fun isFinished(): Boolean = true
    override fun stay(): PlayerState = throw IllegalArgumentException()
    override fun plus(cards: List<Card>): PlayerState = throw IllegalArgumentException()
}

data class BlackJack(override val hands: Hands) : Finish(hands)
data class Bust(override val hands: Hands) : Finish(hands)
data class Stay(override val hands: Hands) : Finish(hands)
