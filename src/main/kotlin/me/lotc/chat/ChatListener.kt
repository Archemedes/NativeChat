package me.lotc.chat

import me.lotc.chat.message.Message
import me.lotc.chat.channel.LocalChannel
import me.lotc.chat.user.chat
import co.lotc.core.bukkit.wrapper.BukkitSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import net.md_5.bungee.api.ChatColor.*
import co.lotc.core.util.MessageUtil.*
import org.bukkit.entity.Player

class ChatListener(private val plugin: NativeChat) : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onEvent(e: AsyncPlayerChatEvent) { //Event MIGHT be Async, but sync if called from plugins
        e.isCancelled = true //Always cancel the default event because we're taking over the chat

        val p = e.player
        val msg = e.message.trim()
        chat(p, msg)
    }

    fun chat(p: Player, msg: String){
        val d = ChatAttempt(p, msg)

        continuityCheck(d)
        if(!d.shouldContinue) return

        if(hasChannelBracket(d)) determineChannel(d)
        if(!d.shouldContinue) return

        checkTalkPermissions(d)
        if(!d.shouldContinue) return

        checkCooldown(d)
        if(!d.shouldContinue) return

        if(d.message.startsWith("((") || d.message.startsWith("[[")){
            d.channel = (d.channel as? LocalChannel)?.looc ?: d.channel
            d.message = d.message.trimStart('(','[').trimEnd(')',']')
        }

        d.channel.handle(Message(BukkitSender(p), d.message))
    }

    private fun continuityCheck(d : ChatAttempt) {
        val MAX_CONTINUITY_LENGTH = 270
        val cont = d.chatter.continuity

        if(d.message.endsWith("--")){
            val length = d.message.length - 2
            if(length + cont.length > MAX_CONTINUITY_LENGTH) d.player.sendMessage(asError("Sorry! Your message is too long"))
            else{
                d.player.sendMessage("${GRAY}Your chat has been appended to your message")
                d.chatter.continuity.append(d.message.substring(0,length)).append(' ')
            }

            d.shouldContinue = false
        } else {
            d.message = cont.toString() + d.message
            cont.clear()
        }
    }

    private fun hasChannelBracket(d : ChatAttempt) = d.message.startsWith("#")

    private fun determineChannel(d : ChatAttempt){
        val msg = d.message.substring(1)

        val firstSpace = msg.indexOf(' ')
        val alias = if(firstSpace > 0) msg.substring(0, firstSpace).trim().toLowerCase() else msg

        val channel = plugin.chatManager.getByAlias(alias)

        if(channel == null || !d.player.hasPermission(channel.permission)){

            d.player.sendMessage( asError("Could not find channel with name$WHITE $alias") )
            d.shouldContinue = false
            return
        }


        if(firstSpace == -1){ //Switches active channel to be spoken in
            d.chatter.channels.focusChannel(channel)
            d.shouldContinue = false //Don't send anything, only switch channel
            d.player.sendMessage("${AQUA}You are now talking in: ${channel.formattedTitle}")
            return
        } else {
            d.channel = channel
            d.message = msg.substring(firstSpace+1)
        }
    }

    private fun checkCooldown(d: ChatAttempt){
        val c = d.channel
        val cd = c.cooldown
        if(cd <= 0 || d.player.hasPermission(c.permissionMod)) return

        if(d.chatter.channels.isOrSetCooldown(c)){
            d.shouldContinue = false
            val remaining = d.chatter.channels.getRemainingCooldown(c).seconds
            d.player.sendMessage( asError("You must wait $remaining seconds before talking in ${c.formattedTitle}") )
        }
    }

    private fun checkTalkPermissions(d: ChatAttempt) {
        if(!d.player.hasPermission(d.channel.permissionTalk)){
            d.player.sendMessage( asError("You do not have permission to talk in ${d.channel.formattedTitle}") )
            d.shouldContinue = false
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun login(e: PlayerJoinEvent){
        plugin.chatManager.join(e.player)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun logout(e: PlayerQuitEvent){
        plugin.chatManager.quit(e.player)
    }


    data class ChatAttempt(val player: Player, var message: String){
        val chatter = player.chat
        var channel = chatter.channel
        var shouldContinue = true
    }

}