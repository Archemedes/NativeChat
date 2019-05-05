package me.lotc.chat

import co.lotc.core.agnostic.Sender
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import me.lotc.chat.BungeeListener.Intent.*
import me.lotc.chat.channel.Channel
import me.lotc.chat.command.ModCommand
import me.lotc.chat.user.chat
import me.lotc.chat.user.uuid
import me.lucko.luckperms.LuckPerms
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.DataInput
import java.lang.IllegalArgumentException
import java.time.Duration
import java.util.*
import javax.naming.OperationNotSupportedException

object BungeeListener : PluginMessageListener {
    const val SUBCHANNEL_NAME = "NativeChat"

    fun listen(plugin: Morphian){
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this)
    }

    fun newPluginMessageDataOutput(sender: Sender, channel: Channel, intent: Intent) : ByteArrayDataOutput{

        val out = ByteStreams.newDataOutput()
        out.writeUTF("Forward")
        out.writeUTF("ALL")
        out.writeUTF(SUBCHANNEL_NAME)
        out.writeUTF(channel.cmd)
        out.writeInt(intent.encoded)
        out.writeUTF(sender.name)
        out.writeUTF(( sender.uuid ?: UUID(0L,0L)).toString() )

        return out
    }

    override fun onPluginMessageReceived(bungeeChannel: String, player: Player, message: ByteArray) {
        if (bungeeChannel != "BungeeCord") return

        println("MESSAGE ON THREAD: " + Bukkit.isPrimaryThread())

        val input = ByteStreams.newDataInput(message)
        val subChannel = input.readUTF()

        if(subChannel == SUBCHANNEL_NAME) {

            //Get some values common to all plugin messages
            val channelCmd = input.readUTF()
            val channel = Morphian.get().chatManager.getByAlias(channelCmd)

            if(channel == null || !channel.isBungee) return
            val intent = Intent.fromInt(input.readInt())
            val userName = input.readUTF()
            val uuid = UUID.fromString(input.readUTF())

            if(uuid.leastSignificantBits == 0L && uuid.mostSignificantBits == 0L)
                throw OperationNotSupportedException("TODO: un-uuidable senders")

            val user = LuckPerms.getApi().userManager.loadUser(uuid).get()
            val sender = ProxiedSender(userName, user)
            //Handle based on the type of action (Intent) meant to be taken
            handlePluginMessage(channel, intent, sender, input)
        }
    }


    private fun handlePluginMessage(channel: Channel, intent: Intent, sender: Sender, input: DataInput){
        when(intent){
            SEND_MESSAGE -> receiveNetworkMessage(channel, sender, input)
            else -> punishPlayer(channel, intent, sender, input)
        }
    }

    private fun receiveNetworkMessage(channel: Channel, sender: Sender, input: DataInput){
        val jsonPreamble = input.readUTF()
        val jsonContent = input.readUTF()

        val preamble = ComponentSerializer.parse(jsonPreamble)
        val content = ComponentSerializer.parse(jsonContent)

        channel.sendComposed(preamble, content, sender) //Sends off with default receiver list and no Context
    }

    private fun punishPlayer(channel: Channel, intent: Intent, sender: Sender, input: DataInput){
        val player = Bukkit.getPlayer(input.readUTF()) ?: return
        val duration = Duration.ofMillis(input.readLong())

        val command = ModCommand()
        when(intent){ //TODO: how can this ever be read from other places?
            KICK_PLAYER -> command.kick(sender, channel, player)
            BAN_PLAYER -> command.ban(sender, channel, player, duration)
            MUTE_PLAYER -> command.mute(sender, channel, player, duration)
            UNMUTE_PLAYER -> command.unmute(sender, channel, player)
            UNBAN_PLAYER -> command.unban(sender, channel, player)
            else -> throw IllegalStateException("punishPlayer can't process this intent: ${intent.name}")
        }
    }


    enum class Intent(val encoded: Int){
        SEND_MESSAGE(1),
        KICK_PLAYER(2),
        MUTE_PLAYER(3),
        BAN_PLAYER(4),
        UNMUTE_PLAYER(5),
        UNBAN_PLAYER(6);

        companion object {
            fun fromInt(intent: Int) = values().asSequence().find { intent == it.encoded }
                ?: throw IllegalArgumentException("Bad encoded Intent value!")
        }
    }
}