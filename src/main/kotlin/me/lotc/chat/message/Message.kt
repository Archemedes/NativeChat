package me.lotc.chat.message

import co.lotc.core.agnostic.Sender
import co.lotc.core.util.Context
import me.lotc.chat.user.chat
import me.lotc.chat.user.player
import net.md_5.bungee.api.chat.BaseComponent
import java.util.*
import java.util.regex.Pattern

class Message(val sender: Sender, initialText: String) {
    val prefixes = LinkedList<Text>()
    val content = LinkedList<Text>()
    val player get() = sender.player
    val chatter get() = player?.chat

    val context = Context()

    init{ if(initialText.isNotEmpty()) content.addFirst(Text(initialText)) }

    fun copy() : Message {
        val other = Message(this.sender, "")

        this.content.forEach { other.content.add(it) }
        this.prefixes.forEach { other.prefixes.add(it) }

        return other
    }

    fun startsWith(prefix: String) : Boolean {
        return content.first.startsWith(prefix)
    }

    fun endsWith(suffix: String) : Boolean {
        return content.last.endsWith(suffix)
    }

    fun contains(middle: String, ignoreCase : Boolean = false) : Boolean {
        return content.stream().anyMatch { it.content.contains(middle, ignoreCase)}
    }

    fun prefixToRawText() = prefixes.map { it.content }.joinToString("")

    fun toRawText() = content.map { it.content }.joinToString("")

    fun transform(regex: String, transformer : (initial: Text) -> Text) {
        val newContent = LinkedList<Text>()
        for(txt in content){
            val pat = Pattern.compile(regex)
            val unmatched = pat.split(txt.content, -1)

            if(unmatched.size < 2){
                newContent.add(txt)
                continue
            }

            newContent.add(txt.copy(content = unmatched[0]))
            val matcher = pat.matcher(txt.content)

            for(i in 1 until unmatched.size){
                matcher.find()
                val sb = StringBuilder()
                if(matcher.groupCount() > 0) {
                    for(j in 0 until matcher.groupCount()) if(matcher.group(j) != null) sb.append(matcher.group(j))
                }else {
                    sb.append(matcher.group())
                }

                if(sb.isNotEmpty()) {
                    val matchedText = transformer.invoke(txt.copy(content = sb.toString()))
                    newContent.add(matchedText)
                    if (unmatched[i].isNotEmpty()) newContent.add(txt.copy(content = unmatched[i]))
                }
            }
        }

        content.clear()
        content.addAll(newContent)
    }

    fun build() : Pair<Array<BaseComponent>, Array<BaseComponent> > {
        val preamble = prefixes.filterNot { it.isEmpty() }.map { it.toComponent() }.toTypedArray()
        val payload = content.filterNot { it.isEmpty() }.map { it.toComponent() }.toTypedArray()
        return Pair(preamble, payload)
    }
}