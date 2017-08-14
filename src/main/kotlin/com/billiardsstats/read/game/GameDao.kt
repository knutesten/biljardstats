package com.billiardsstats.read.game

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * @author Knut Esten Melandsø Nekså
 */

@Repository
class GameDao(private val namedTemplate: NamedParameterJdbcTemplate) {
    fun create(game: Game) {
        namedTemplate.update("" +
                "INSERT INTO eight_ball_games () " +
                "VALUES ()")
    }
}
