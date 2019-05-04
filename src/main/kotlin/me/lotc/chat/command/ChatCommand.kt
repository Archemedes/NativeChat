package me.lotc.chat.command

import me.lotc.chat.channel.Channel
import me.lotc.chat.user.chat
import co.lotc.core.command.CommandTemplate
import co.lotc.core.command.annotate.Cmd
import me.lotc.chat.Morphian
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import net.md_5.bungee.api.ChatColor.*

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

        p.chat.channels.unsubscribe(channel)
        msg("You have left $header")
    }

    @Cmd("Show all channels you can join")
    fun list(s: CommandSender){
        val channels = Morphian.get().chatManager.channelsFor(s)
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

    @Cmd("moderate channels you are allowed to edit")
    fun mod() : CommandTemplate {
        return ModCommand()
    }

    @Cmd("see or edit your chat settings")
    fun settings() : CommandTemplate {
        return SettingsCommand()
    }
}