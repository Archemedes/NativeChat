package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import me.lotc.chat.user.EmoteStyle
import net.md_5.bungee.api.ChatColor.WHITE
import net.md_5.bungee.api.ChatColor.YELLOW

class EmoteFormatter : InFormatter {
    private val prefixes = listOf("*","[!]","'s")
    private val suffixes = listOf("*","[!]")

    override fun format(message: Message) {
        val c = message.content

        val willEmote = (message.chatter?.emoteStyle == EmoteStyle.ALWAYS)
                || (message.chatter?.emoteStyle == EmoteStyle.QUOTATIONS && message.contains("\""))
                || prefixes.stream().anyMatch(c.first::startsWith)
                || suffixes.stream().anyMatch(c.last::endsWith)

        val willNamelessEmote = willEmote && (c.first.startsWith("**") || c.first.startsWith("[!]")
                || c.last.endsWith("[!]") || c.last.endsWith("**"))

        if(willEmote){
            message.context["emote"] = true

            val emoteColor = message.chatter?.emoteColor ?: YELLOW
            if(c.first.startsWith("[!]")) c.first.map { s->s.substring(3) }
            c.first.map { s->s.trimStart('*',' ') }
            val transform: (String) -> String = { s -> s.trimEnd('*',' ') }
            c.last.map(transform)

            c.forEach { it.color = emoteColor }
            message.transform("\".*?\"", ::quotify)
            message.transform("((?<=\\s|^)\").*?\$|^.*?(\"(?=\\s|\$))", ::quotify)
            val formatUsername = willNamelessEmote || !message.toRawText().startsWith("“")

            if(formatUsername) {
                val prefixes = message.prefixes
                prefixes.removeLast() //Understood to be the colon
                prefixes.last.color = emoteColor //Understood to be the username
                if (willNamelessEmote) prefixes.last.content = "[!] " //Retains hover and click stuff
                else if (c.first.content.isEmpty() || c.first.content[0] != '\'' && c.first.content[0] != ',') prefixes.addLast(Text(" "))
            }

        } else { //Must present as normal RP speech. Add quotes
            c.first.map { s-> '“' + s.trimStart('"') }
            c.last.map { s->s.trimEnd('"') + '”' }
        }
    }

    private fun quotify(speech : Text) : Text {
        speech.color = WHITE
        var c = speech.content
        c = c.trim('"')
        speech.content = "“$c”"
        return speech
    }
}
