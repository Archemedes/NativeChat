package me.lotc.chat.channel

import me.lotc.chat.user.chat
import co.lotc.core.bukkit.util.Run
import co.lotc.core.bukkit.wrapper.BukkitSender
import me.lotc.chat.Morphian
import me.lotc.chat.format.`in`.InFormatter
import me.lotc.chat.format.out.OutFormatter
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.message.Message
import me.lotc.chat.user.Chatter
import me.lotc.chat.user.player
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
        for(chatter in getReceivers(message)) {
            val composed = ComposedMessage(message.sender, pair.first, pair.second, BukkitSender(chatter.player), message.context)
            outgoingFormatters.forEach { it.format(composed) }
            message.sender.chat?.focus?.acceptChat(this, composed)

            val sendMe = message.sender.chat?.focus?.willAcceptChat(this, composed) ?: true

            val whoSays = message.sender.player
            when {
                whoSays == null -> composed.send()
                whoSays.chat.focus.willAcceptChat(this, composed) -> composed.send()
                else -> whoSays.sendActionBar("${GRAY}Missed message in $formattedTitle")
            }

        }
    }

    @JvmDefault
    fun handle(message : Message){
        incomingFormatters.forEach { it.format(message) }
        if(sendFromMain && !Bukkit.isPrimaryThread() ) Run(Morphian.get()).sync { send(message) }
        else send(message)
    }

    @JvmDefault
    fun getReceivers(message: Message) : List<Chatter> {
        return Morphian.get().chatManager.getPlayers().filter { it.channels.isSubscribed(this) }
    }
}