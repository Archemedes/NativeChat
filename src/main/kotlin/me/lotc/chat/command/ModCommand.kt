package me.lotc.chat.command

import co.lotc.core.agnostic.Sender
import co.lotc.core.command.annotate.Cmd
import co.lotc.core.command.annotate.Default
import co.lotc.core.util.TimeUtil
import me.lotc.chat.BungeeListener
import me.lotc.chat.channel.Channel
import me.lotc.chat.user.chat
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Duration

class ModCommand : BaseCommand() {

    @Cmd("Kick a player from a channel")
    fun kick(us: Sender, channel: Channel, p: Player){
        pexOrBust(us, channel)

        validate(!channel.isPermanent, "Cannot kick people from permanent channels")
        validate(p.chat.channels.isSubscribed(channel), "Player is not in ${channel.formattedTitle}")

        p.chat.channels.unsubscribe(channel)
        us.sendMessage("${GRAY}We have kicked$GOLD ${p.name}$GRAY from ${channel.formattedTitle}")
        p.sendMessage("${DARK_RED}You were kicked from ${channel.formattedTitle}")
    }

    @Cmd("Mute a player in channel")
    fun mute(us: Sender, channel: Channel, p: Player, @Default("0s") duration: Duration){
        pexOrBust(us, channel)

        validate(p.hasPermission(channel.permissionTalk), "Player already cannot talk in ${channel.formattedTitle}")

        val millis = duration.toMillis()
        if(millis > 0) p.chat.channels.mute(channel, duration)
        else p.chat.channels.mute(channel)

        us.sendMessage("${GRAY}We have muted$GOLD ${p.name}$GRAY in ${channel.formattedTitle}")
        if(millis == 0L) p.sendMessage("${DARK_RED}You were muted in ${channel.formattedTitle}")
        else p.sendMessage("${DARK_RED}You were muted in ${channel.formattedTitle}$DARK_RED for ${asTime(millis)}")
    }

    @Cmd("Unmute a player in channel")
    fun unmute(us: Sender, channel: Channel, p: Player){
        pexOrBust(us, channel)

        validate(p.chat.channels.isMuted(channel), "Player isn't muted in ${channel.formattedTitle}")
        p.chat.channels.unmute(channel)

        us.sendMessage("${GRAY}We have unmuted$GOLD ${p.name}$GRAY in ${channel.formattedTitle}")
        p.sendMessage("${DARK_RED}You are no longer muted in ${channel.formattedTitle}")
    }

    @Cmd("Ban a player from channel")
    fun ban(us: Sender, channel: Channel, p: Player, @Default("0s") duration: Duration){
        pexOrBust(us, channel)

        validate(p.hasPermission(channel.permission), "Player already doesn't have permission for ${channel.formattedTitle}")
        validate(!channel.isPermanent, "Cannot ban people from permanent channels")

        val millis = duration.toMillis()
        if(millis > 0) p.chat.channels.ban(channel, duration)
        else p.chat.channels.ban(channel)

        us.sendMessage("${GRAY}We have banned$GOLD ${p.name}$GRAY in ${channel.formattedTitle}")
        if(millis == 0L) p.sendMessage("${DARK_RED}You were banned from ${channel.formattedTitle}")
        else p.sendMessage("${DARK_RED}You were banned from ${channel.formattedTitle}$DARK_RED for ${asTime(millis)}")
    }

    @Cmd("Unban a player in channel")
    fun unban(us: Sender, channel: Channel, p: Player){
        pexOrBust(us, channel)

        validate(p.chat.channels.isBanned(channel), "Player isn't banned from ${channel.formattedTitle}")
        p.chat.channels.unban(channel)

        us.sendMessage("${GRAY}We have unbanned$GOLD ${p.name}$GRAY from ${channel.formattedTitle}")
        p.sendMessage("${DARK_RED}You are no longer banned from ${channel.formattedTitle}")
    }

    @Cmd("Add player to a channel")
    fun add(us: Sender, channel: Channel, p: Player){
        pexOrBust(us, channel)

        validate(!channel.isPermanent, "Cannot add people to permanent channels")
        validate(!p.chat.channels.isSubscribed(channel), "Player is already in ${channel.formattedTitle}")

        p.chat.channels.subscribe(channel)
        us.sendMessage("${GRAY}We have added$GOLD ${p.name}$GRAY to ${channel.formattedTitle}")
        p.sendMessage("${GRAY}You were added to ${channel.formattedTitle}")
    }

    @Cmd("See if channel contains a certain player")
    fun has(us: Sender, channel: Channel, p: Player){
        pexOrBust(us, channel)

        if(p.chat.channels.isSubscribed(channel))
            msg("$GOLD ${p.name}$GREEN IS in ${channel.formattedTitle}")
        else
            msg("$GOLD ${p.name}$DARK_PURPLE IS NOT in ${channel.formattedTitle}")
    }

    @Cmd("List all players currently in the channel")
    fun list(us: Sender, channel: Channel) {
        pexOrBust(us, channel)
        val players = HashSet<String>()

        for (player in Bukkit.getOnlinePlayers())
            if (channel.isSubscribed(player))
                players.add(player.name)
        msg("$LIGHT_PURPLE Players in ${channel.formattedTitle} (${players.size}): ")
        msg(players.joinToString(", "))
    }

    private fun asTime(millis: Long) = TextComponent.toLegacyText(TimeUtil.print(millis, false, GRAY, DARK_RED))


    private fun pluginMessage(s: Sender, channel: Channel, intent: BungeeListener.Intent, p: OfflinePlayer, duration:Duration) {
        val out = BungeeListener.newPluginMessageDataOutput(s, channel, intent)
        out.payload.writeUTF(p.name)
        out.payload.writeLong(duration.toMillis())

        //TODO
    }

    private fun pexOrBust(s: Sender, channel: Channel){
        validate(s.hasPermission(channel.permission), "Could not find this channel")
        validate(s.hasPermission(channel.permissionMod), "You do not have permission to moderate this channel")
    }
}