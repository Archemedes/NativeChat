package co.lotc.chat.channel

import co.lotc.chat.format.Formatter
import co.lotc.core.agnostic.Sender
import net.md_5.bungee.api.ChatColor

interface Channel {
    val name: String
    val title: String
    val tags: List<String>
    val color: ChatColor

    val formatters: List<Formatter>

    val permission get() = "rp.channel.$name"
    val permissionTalk get() = "$permission.talk"
    val permissionMod get() = "$permission.mod"

    fun chat(sender: Sender, input: String)

    fun getReceivers(sender: Sender) : List<Sender>


}