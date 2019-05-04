package me.lotc.chat.message

import co.lotc.core.bukkit.util.ChatBuilder
import co.lotc.core.util.MessageUtil
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent

data class Text(
    var content: String,
    var color : ChatColor = ChatColor.WHITE,
    var bold : Boolean = false,
    var italic : Boolean = false,
    var strikethrough : Boolean = false,
    var magic : Boolean = false,

    var hover: HoverEvent? = null,
    var click: ClickEvent? = null){

    constructor(component: BaseComponent): this(BaseComponent.toPlainText(component)){
        this.color = component.color
        this.bold = component.isBold
        this.italic = component.isItalic
        this.strikethrough = component.isStrikethrough
        this.magic = component.isObfuscated
    }

    fun tooltip(tip : String){
        hover = MessageUtil.hoverEvent(tip)
    }

    fun suggests(suggestion: String){
        click = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion)
    }

    fun map(transform : (m:String)->String?){
        val theNew = transform.invoke(content)
        if(theNew != null) content = theNew
    }

    fun startsWith(prefix: String) : Boolean {
        return content.startsWith(prefix)
    }

    fun endsWith(suffix: String) : Boolean {
        return content.endsWith(suffix)
    }

    fun isEmpty() : Boolean {
        return content.isEmpty()
    }

    fun toComponent() : BaseComponent {
        val cb = ChatBuilder(this.content)
        cb.color(color)
        if(bold) cb.bold()
        if(italic) cb.italic()
        if(strikethrough) cb.strikethrough()
        if(magic) cb.obfuscated()
        if(click != null) cb.event(click)
        if(hover != null) cb.event(hover)

        return cb.build()
    }


}