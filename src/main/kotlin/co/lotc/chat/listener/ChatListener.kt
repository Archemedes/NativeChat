package co.lotc.chat.listener

import co.lotc.chat.Message
import co.lotc.chat.Morphian
import co.lotc.chat.chat
import co.lotc.core.bukkit.wrapper.BukkitSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import net.md_5.bungee.api.ChatColor.*

class ChatListener(val plugin: Morphian) : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun chat(e: AsyncPlayerChatEvent){
        //Note that we are async
        var channel = e.player.chat.channel

        if(e.message.startsWith("#")){
            val alias = e.message.substring(1, e.message.indexOf(' '))
            val otherChannel = plugin.chatManager.channelAliases.keys.firstOrNull { it == alias }

            if(otherChannel == null){
                e.player.sendMessage("${DARK_RED}ERROR:$RED Could not find channel with name$WHITE $alias")
                return
            } else {
                channel = otherChannel
            }
        }

        val message = Message(BukkitSender(e.player), channel)
        //TODO: throw it through the formatters
        //TODO: Give it to the Channel
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun login(e: PlayerJoinEvent){
        plugin.chatManager.join(e.player)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun logout(e: PlayerQuitEvent){
        plugin.chatManager.quit(e.player)
    }

}