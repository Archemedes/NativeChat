package me.lotc.chat.depend

import net.lordofthecraft.arche.ArcheCore
import org.bukkit.entity.Player
import java.util.*

object ArcheBridge {

    fun isNew(p: Player) : Boolean{
       return ArcheCore.getPersona(p)?.isNewbie ?: false
    }

    fun getUsername(uuid: UUID): String? {
        return ArcheCore.getControls().getPlayerNameFromUUID(uuid)
    }

    fun getUUID(username : String) : UUID? {
        return ArcheCore.getControls().getPlayerUUIDFromName(username)
    }

}