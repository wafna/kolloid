package wafna.kolloid.db

import arrow.core.Either

private const val MaximumPoolSize = 8

private fun damnTheTorpedoes(action: () -> Unit) {
    Either.catch(action).onLeft {
        it.printStackTrace()
    }
}

private const val PostgresDelayStart = 3_000L
