package com.billiardsstats.web.websocket.protocol.out

import com.billiardsstats.read.user.User
import com.billiardsstats.web.game.GameType

/**
 * @author: Knut Esten Melandsø Nekså
 */
class GameRequest(val id: String, val gameType: GameType, val opponent: User)

class GameRequestAccepted(val id: String)