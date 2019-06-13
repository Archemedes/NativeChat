package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message
import me.lotc.chat.message.Text

class MarkdownFormatter: InFormatter {
    //Uses Telaniresque markdown instead of actual markdown characters

    override fun format(message: Message) {
        if(message.sender.hasPermission("chat.markdown")) {
            message.transform("/.+?\\^.+?\\^.+?/", ::setItalic)
            message.transform("\\^.+?\\^", ::setBold)
            message.transform("/.+?/", ::setItalic)
        }
    }

    private fun setBold(text : Text) : Text{
        text.map { s->s.trim('^') }
        text.bold = true
        return text
    }

    private fun setItalic(text : Text) : Text{
        text.map { s->s.trim('/') }
        text.italic = true
        return text
    }
}