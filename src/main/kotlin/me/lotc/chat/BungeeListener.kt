package me.lotc.chat

import com.google.common.io.ByteStreams
import me.lotc.chat.BungeeListener.Intent.*
import me.lotc.chat.channel.Channel
import me.lotc.chat.user.chat
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.DataInput
import java.lang.IllegalArgumentException

object BungeeListener : PluginMessageListener {
    const val SUBCHANNEL_NAME = "NativeChat"

    fun listen(plugin: Morphian){
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this)
    }

    override fun onPluginMessageReceived(bungeeChannel: String, player: Player, message: ByteArray) {
        if (bungeeChannel != "BungeeCord") return

        val input = ByteStreams.newDataInput(message)
        val subChannel = input.readUTF()

        if(subChannel == SUBCHANNEL_NAME) {

            //Get some values common to all plugin messages
            val channelCmd = input.readUTF()
            val channel = Morphian.get().chatManager.getByAlias(channelCmd)

            if(channel == null || !channel.isBungee) return
            val intent = Intent.fromInt(input.readInt())

            //Handle based on the type of action (Intent) meant to be taken
            handlePluginMessage(channel, intent, input)
        }
    }


    private fun handlePluginMessage(channel: Channel, intent: Intent, input: DataInput){
        when(intent){
            SEND_MESSAGE -> receiveNetworkMessage(channel, input)
            else -> Bukkit.getPlayer(input.readUTF())?.also { punishPlayer(channel, intent, it) }
        }
    }

    private fun receiveNetworkMessage(channel: Channel, input: DataInput){
        val jsonPreamble = input.readUTF()
        val jsonContent = input.readUTF()

        val preamble = ComponentSerializer.parse(jsonPreamble)
        val content = ComponentSerializer.parse(jsonContent)
        channel.sendComposed(preamble, content) //Sends off with default receiver list and no Context
    }

    private fun punishPlayer(channel: Channel, intent: Intent, player: Player){
        val channels = player.chat.channels
        when(intent){ //TODO: how can this ever be read from other places?
            KICK_PLAYER -> channels.unsubscribe(channel)
            BAN_PLAYER -> channels.ban(channel)
            MUTE_PLAYER -> channels.mute(channel)
            else -> throw IllegalStateException("Wrong intent for code point")
        }
    }


    enum class Intent(val intent: Int){
        SEND_MESSAGE(1),
        KICK_PLAYER(2),
        MUTE_PLAYER(3),
        BAN_PLAYER(4);

        companion object {
            fun fromInt(intent: Int) = values().asSequence().find { intent == it.intent }
                ?: throw IllegalArgumentException("Bad encoded Intent value!")
        }
    }
}