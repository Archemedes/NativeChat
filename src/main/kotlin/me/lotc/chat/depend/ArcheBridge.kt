package me.lotc.chat.depend

import org.bukkit.entity.Player
import java.util.*

object ArcheBridge {
    //val isEnabled = Bukkit.getPluginManager().isPluginEnabled("ArcheCore")
    val isEnabled = false

    fun isNew(p: Player) : Boolean{
        return false //TODO
    }

    fun getUsername(uuid: UUID): String{
        return "TODO"
    }

    fun getUUID(username : String) : UUID{
        return UUID.randomUUID() //TODO use ArcheNameLog
    }

}