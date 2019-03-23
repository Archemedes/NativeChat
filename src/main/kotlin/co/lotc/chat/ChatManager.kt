package co.lotc.chat

import co.lotc.chat.channel.Channel
import org.bukkit.entity.Player
import java.util.*

class ChatManager(val primordial: Channel, val channels: List<Channel>) {
    val channelAliases : Map<String, Channel>
    private val players: MutableMap<UUID, Chatter> = HashMap()

    init{
        val tempMap = HashMap<String, Channel>()
        channelAliases = tempMap
        for(channel in channels) channel.tags.forEach{t -> channelAliases[t] = channel}
    }

    fun join(player: Player){
        val chat = Chatter(player)
        chat.loadSettings()
        players[player.uniqueId] = chat
    }

    fun quit(player: Player){
        val chat = players[player.uniqueId]
        chat!!.saveSettings()

    }

    fun getChatSettings(player : Player) : Chatter {
        return players[player.uniqueId]!!
    }

}