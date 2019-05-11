package me.lotc.chat.command

import co.lotc.core.bukkit.util.ChatBuilder
import me.lotc.chat.channel.Channel
import me.lotc.chat.user.chat
import co.lotc.core.command.CommandTemplate
import co.lotc.core.command.annotate.Cmd
import me.lotc.chat.NativeChat
import me.lotc.chat.user.Focus
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatMessageType

class ChatCommand : BaseCommand() {

    @Cmd("Join a channel")
    fun join(p: Player, channel: Channel){
        val header = channel.formattedTitle
        validate(channel.canJoin(p), "You don't have permission to join $header")
        validate(!channel.isSubscribed(p), "You're already in $header")

        p.chat.channels.subscribe(channel)
        msg("You have joined $header")
    }

    @Cmd("Leave a channel")
    fun leave(p: Player, channel: Channel){
        val header = channel.formattedTitle
        validate(channel.isSubscribed(p), "You aren't currently in $header")
        validate(!channel.isPermanent, "You cannot leave this channel")

        p.chat.channels.unsubscribe(channel)
        msg("You have left $header")
    }

    @Cmd("Show all channels you can join")
    fun list(s: CommandSender){
        val channels = NativeChat.get().chatManager.channelsFor(s)
        val channelString = channels.joinToString(", ") { "${it.bracketColor}#${it.cmd}: ${it.formattedTitle}" }

        msg("$LIGHT_PURPLE${BOLD}These are the channels we have for you:")
        msg(channelString)
    }

    @Cmd("Clear stored content from continuity buffer")
    fun cc(p: Player){
        validate(p.chat.continuity.any(), "No content in continuity buffer")

        p.chat.continuity.clear()
        msg("Cleared continuity")
    }

    @Cmd("Focus on a specific set of channels")
    fun focus(p: Player, category: Focus.Category){
        val f = p.chat.focus
        val oldCat = f.focus

        validate(category != oldCat, "Already focused on ${category.color}${category.description}")

        f.focus = category
        f.resend()

        //Do not add the lower bar for the ALL category
        if(category === Focus.Category.ALL) return

        val cb = ChatBuilder()
        Focus.Category.values().forEach {
            val selected = it === category

            cb.append("[")
            if(selected) cb.color(YELLOW).bold()
            else cb.color(DARK_GRAY)

            cb.append(it.tag).color(it.color).bold(false)
                .hover("${GRAY}Click to see ${it.color}${it.description}$GRAY messages")
                .command("/chat focus ${it.name}")

            cb.append("]").reset()
            if(selected) cb.color(YELLOW).bold()
            else cb.color(DARK_GRAY)
            cb.append(" ")
        }

        @Suppress("DEPRECATION")
        p.sendMessage(ChatMessageType.CHAT, cb.build())
    }


    @Cmd("moderate channels you are allowed to edit")
    fun mod() : CommandTemplate {
        return ModCommand()
    }

    @Cmd("see or edit your chat settings")
    fun settings() : CommandTemplate {
        return SettingsCommand()
    }
}