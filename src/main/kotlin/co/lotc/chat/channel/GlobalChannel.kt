package co.lotc.chat.channel

import co.lotc.chat.format.Formatter
import co.lotc.core.agnostic.Sender
import net.md_5.bungee.api.ChatColor

class GlobalChannel(
    override val name: String,
    override val title: String,
    override val tags: List<String>,
    override val color: ChatColor) : Channel {

    override val formatters = composeFormatters()
    private val receivers = ArrayList<Sender>()

    override fun chat(sender: Sender, input: String) {
       //Probably an obvious
    }

    override fun getReceivers(sender: Sender): List<Sender> {
        return receivers //TODO
    }


    private fun composeFormatters() : List<Formatter> {
        return null!!//TODO
    }
}