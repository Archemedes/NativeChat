package me.lotc.chat.message

import co.lotc.core.agnostic.Sender
import co.lotc.core.util.Context
import me.lotc.chat.user.player
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.entity.Player
import java.util.*

class ComposedMessage(val sender: Sender,
                      var preamble: Array<out BaseComponent>,
                      var content: Array<out BaseComponent>,
                      val receiver: Sender,
                      val context: Context) {
    val prefixes = LinkedList<Text>()
    val suffixes = LinkedList<Text>()
    val player get() = sender as? Player

    val finalMessage by lazy {
        val arrayPre = prefixes.map { it.toComponent() }.toTypedArray()
        val arraySuf = suffixes.map { it.toComponent() }.toTypedArray()

        arrayOf(*arrayPre, *preamble, *content, *arraySuf)
    }

    fun send(){
        val player = receiver.player

        @Suppress("DEPRECATION")
        if(player != null) player.sendMessage(*finalMessage)
        else receiver.sendMessage(*finalMessage)
    }

    fun getLegacyContent(): String {
        return BaseComponent.toLegacyText(*content)
    }

    fun getLegacyPreamble(): String {
        return BaseComponent.toLegacyText(*preamble)
    }

    fun replaceContent(vararg newContent: BaseComponent){
        content = newContent
    }
}