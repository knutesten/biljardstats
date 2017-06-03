package com.billiardsstats.web.websocket.protocol.`in`

import com.billiardsstats.read.user.User
import com.billiardsstats.web.game.GameType

/**
 * @author: Knut Esten Melandsø Nekså
 */

class CreateGameRequest(val gameType: GameType, val opponent: User)

class RejectGameRequest(val id: String)

class AcceptGameRequest(val id: String)