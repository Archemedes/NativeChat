package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message
import me.lotc.chat.message.Text
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.ClickEvent

class LinkFormatter : InFormatter {

    override fun format(message: Message) {
        message.transform("https?://[^ ]*", ::clickableUrl)
    }

    private fun clickableUrl(initial: Text) : Text {
        val url = initial.content.trim()
        val text = Text("«Link»")
        text.color = BLUE
        text.click = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        text.tooltip("Open Url")
        return text
    }
}