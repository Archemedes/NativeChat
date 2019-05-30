package me.lotc.chat.command

import me.lotc.chat.user.EmoteStyle
import me.lotc.chat.user.chat
import co.lotc.core.bukkit.util.ChatBuilder
import co.lotc.core.command.annotate.Cmd
import co.lotc.core.command.annotate.Default
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import net.md_5.bungee.api.ChatColor.*

class SettingsCommand : BaseCommand() {


    fun invoke(p: Player){
        val c = p.chat
        msg("$LIGHT_PURPLE${BOLD}These are your chat settings:")
        setting("Emote Style", c.emoteStyle.name.toLowerCase(), "emotestyle")
        setting("Emote Color", c.emoteColor.name.toLowerCase(), "emotecolor")
        setting("Timestamps on Messages", c.wantsTimestamps, "timestamps")
        setting("Auto-correct Punctuation", c.wantsTimestamps, "punctuation")
        setting("Ping you on mention", c.isMentionable, "mentionable")
    }

    @Cmd("Set your eagerness to emote your actions")
    fun emotestyle(p: Player, shtoyle: EmoteStyle){
        p.chat.emoteStyle = shtoyle
        val style = shtoyle.name.toLowerCase()
        msg("${GOLD}Set your emote style to:$WHITE $style")
    }

    @Cmd(value="Set your emote color", permission = "chat.emotecolor")
    fun emotecolor(p: Player, color: ChatColor){
        val alloweds = setOf(DARK_BLUE, RED, YELLOW, GOLD, AQUA, GREEN, DARK_GREEN, DARK_RED, DARK_AQUA, DARK_PURPLE, LIGHT_PURPLE)
        val col = color.name.toLowerCase().replace('_',' ').capitalize()
        validate(color in alloweds, "This is not an allowed color for emotes:$WHITE $col")
        p.chat.emoteColor = color
        msg("${GOLD}Set your emote color to:$WHITE $col")
    }

    @Cmd("toggle your timestamps setting on messages")
    fun timestamps(p: Player, @Default("toggle") value : String){
        var timestamp = !p.chat.wantsTimestamps
        if(value == "true") timestamp = true
        else if(value == "false") timestamp = false
        p.chat.wantsTimestamps = timestamp

        if(timestamp) msg("${GOLD}Now showing timestamps on chat messages")
        else msg("${GOLD}No longer showing timestamps on chat messages")
    }

    @Cmd("toggle auto-punctuation on roleplay chat")
    fun punctuation(p: Player, @Default("toggle") value : String){
        var punctuate = !p.chat.correctPunctuation
        if(value == "true") punctuate = true
        else if(value == "false") punctuate = false
        p.chat.correctPunctuation = punctuate

        if(punctuate) msg("${GOLD}Now auto-punctuating your roleplay chat")
        else msg("${GOLD}No longer auto-punctuating your roleplay chat")
    }

    @Cmd("toggle players being able to mention you")
    fun mentionable(p: Player, @Default("toggle") value : String){
        var mentionable = !p.chat.wantsTimestamps
        if(value == "true") mentionable = true
        else if(value == "false") mentionable = false
        p.chat.isMentionable = mentionable

        if(mentionable) msg("${GOLD}Now receiving pings on mention")
        else msg("${GOLD}No longer receiving pings on mention")
    }

    private fun setting(settingName: String, value: Any, command : String){
        val fullCommand = "/chat settings $command "
        val cb = ChatBuilder(settingName).suggest(fullCommand).color(GOLD).append(": ").append(value).color(WHITE)
        msg(cb.build())
    }

}