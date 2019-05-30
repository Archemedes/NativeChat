package me.lotc.chat.depend

import net.lordofthecraft.arche.ArcheCore
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object ArcheBridge {
    val isEnabled = Bukkit.getPluginManager().isPluginEnabled("ArcheCore")

    fun isNew(p: Player?) : Boolean{
        p?:return false
        return ArcheCore.getPersona(p)?.isNewbie ?: false
    }

    fun getUsername(uuid: UUID): String? {
        return ArcheCore.getControls().getPlayerNameFromUUID(uuid)
    }

    fun getUUID(username : String) : UUID? {
        return ArcheCore.getControls().getPlayerUUIDFromName(username)
    }

    fun getDisplayName(p: Player) : String {
        return ArcheCore.getPersona(p)?.chatName ?: p.displayName
    }

}