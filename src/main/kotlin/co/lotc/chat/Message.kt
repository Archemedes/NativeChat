package co.lotc.chat

import co.lotc.chat.channel.Channel
import co.lotc.core.agnostic.Sender
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.entity.Player
import java.util.*

class Message(private val sender: Sender, val channel : Channel) {

    val prefixes = LinkedList<BaseComponent>()
    val content = LinkedList<BaseComponent>()
    val player get() = sender as? Player
}