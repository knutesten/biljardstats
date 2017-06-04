package com.billiardsstats.web.websocket.protocol.out

import com.billiardsstats.read.user.User
import com.billiardsstats.read.game.Game
import com.billiardsstats.read.game.GameType

/**
 * @author: Knut Esten Melandsø Nekså
 */
class GameRequest(val id: String, val gameType: GameType, val challenger: User)

class GameRequestAccepted(val gameType: GameType, val game: Game)