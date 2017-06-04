package com.billiardsstats.web.websocket.protocol.`in`

import com.billiardsstats.read.user.User
import com.billiardsstats.read.game.GameType
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author: Knut Esten Melandsø Nekså
 */

class CreateGameRequest(@JsonProperty("gameType") val gameType: GameType, @JsonProperty("opponent") val opponent: User)

class RejectGameRequest(@JsonProperty("id") val id: String)

class AcceptGameRequest(@JsonProperty("id") val id: String)