package me.lotc.chat.channel

import co.lotc.core.agnostic.Sender
import me.lotc.chat.user.chat
import co.lotc.core.bukkit.util.Run
import co.lotc.core.bukkit.wrapper.BukkitSender
import co.lotc.core.util.Context
import com.google.common.collect.Iterables
import me.lotc.chat.BungeeListener
import me.lotc.chat.Morphian
import me.lotc.chat.format.`in`.InFormatter
import me.lotc.chat.format.out.OutFormatter
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.message.Message
import me.lotc.chat.user.Chatter
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import com.google.common.io.ByteStreams
import me.lotc.chat.BungeeListener.Intent.*
import net.md_5.bungee.chat.ComponentSerializer


interface Channel {
    val tag: String
    val title: String
    val cmd: String
    val bold: Boolean
    val color: ChatColor
    val bracketColor: ChatColor
    val cooldown: Int

    val incomingFormatters: List<InFormatter>
    val outgoingFormatters: List<OutFormatter>

    val permission get() = "rp.channel.$cmd"
    val permissionTalk get() = "$permission.talk"
    val permissionMod get() = "$permission.mod"
    val formattedTitle get() = "$color$title"

    val sendFromMain : Boolean
    val isPermanent : Boolean
    val isBungee : Boolean

    @JvmDefault
    fun canJoin(s: CommandSender) : Boolean {
        return s.hasPermission(permission)
    }

    @JvmDefault
    fun canTalk(s: CommandSender) : Boolean {
        return s.hasPermission(permissionTalk)
    }

    @JvmDefault
    fun isSubscribed(s: CommandSender) : Boolean {
        if(s is Player) return s.chat.channels.isSubscribed(this)

        return true //TODO maybe
    }

    @JvmDefault
    fun send(message: Message) {
        val pair = message.build()
        sendToNetwork(message.sender, pair.first, pair.second)
        sendComposed(pair.first, pair.second, message.sender, getReceivers(message), message.context)
    }

    fun sendComposed(preamble: Array<BaseComponent>, content: Array<BaseComponent>,
                     sender: Sender, receivers: List<Chatter> = getReceivers(null),
                     context: Context = Context()){

        for(chatter in receivers) {
            val composed = ComposedMessage(sender, preamble, content, BukkitSender(chatter.player), context)
            outgoingFormatters.forEach { it.format(composed) }

            chatter.focus.acceptChat(this, composed)
            if(chatter.focus.willAcceptChat(this, composed) ) composed.send()
            else chatter.player.sendActionBar("${GRAY}Missed message in $formattedTitle")
        }
    }

    @JvmDefault
    private fun sendToNetwork(sender: Sender, preamble: Array<BaseComponent>, content: Array<BaseComponent>){
        if(!isBungee) return
        val out = BungeeListener.newPluginMessageDataOutput(sender, this, SEND_MESSAGE)

        out.payload.writeUTF(ComponentSerializer.toString(*preamble))
        out.payload.writeUTF(ComponentSerializer.toString(*content))

        out.send()
    }

    @JvmDefault
    fun handle(message : Message){
        incomingFormatters.forEach { it.format(message) }
        if(sendFromMain && !Bukkit.isPrimaryThread() ) Run(Morphian.get()).sync { send(message) }
        else send(message)
    }

    @JvmDefault
    fun getReceivers(message: Message?) : List<Chatter> {
        return Morphian.get().chatManager.getPlayers().filter { it.channels.isSubscribed(this) }
    }
}