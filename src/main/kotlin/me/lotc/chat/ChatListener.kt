package me.lotc.chat

import co.lotc.core.bukkit.wrapper.BukkitSender
import co.lotc.core.util.MessageUtil.asError
import me.lotc.chat.channel.Channel
import me.lotc.chat.channel.LOOCChannel
import me.lotc.chat.channel.LocalChannel
import me.lotc.chat.message.Message
import me.lotc.chat.user.chat
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ChatListener(private val plugin: NativeChat) : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun continuity(e: AsyncPlayerChatEvent) {
        val MAX_CONTINUITY_LENGTH = 520
        val cont = e.player.chat.continuity

        if(e.message.endsWith("--")){
            val length = e.message.length - 2
            if(length + cont.length > MAX_CONTINUITY_LENGTH) e.player.sendMessage(asError("Sorry! Your message is too long"))
            else{
                e.player.sendMessage("${GRAY}Your chat has been appended to your message")
                cont.append(e.message.substring(0,length)).append(' ')
            }

            e.isCancelled = true
        } else {
            e.message = cont.toString() + e.message
            cont.clear()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onEvent(e: AsyncPlayerChatEvent) { //Event MIGHT be Async, but sync if called from plugins
        e.isCancelled = true //Always cancel the default event because we're taking over the chat

        val p = e.player
        val msg = e.message.trim()
        chat(p, msg)
    }

    fun chat(p: Player, msg: String){
        val d = ChatAttempt(p, msg)

        if(hasChannelBracket(d)) determineChannel(d)
        if(!d.shouldContinue) return

        redirectOnSymbol(d)
        if(!d.shouldContinue) return

        checkTalkPermissions(d)
        if(!d.shouldContinue) return

        checkCooldown(d)
        if(!d.shouldContinue) return

        d.channel.handle(Message(BukkitSender(p), d.message))
    }

    private fun redirectOnSymbol(d: ChatAttempt){
        if(!d.chatter.shouldRedirect) return

        val m = d.message

        if(m.startsWith("((") || m.startsWith("[[")){
            d.channel = (d.channel as? LOOCChannel) ?:
                    (d.channel as? LocalChannel)?.looc ?:
                    NativeChat.get().chatManager.defaultLocal?.looc ?:
                    d.channel
            d.message = m.trimStart('(','[').trimEnd(')',']')
        }else if(m.startsWith("\"") || m.startsWith("*") || m.startsWith("[!]")){
            if(d.channel is LocalChannel && d.channel !is LOOCChannel) return

            d.channel = (d.channel as? LOOCChannel)?.parent ?:
                    NativeChat.get().chatManager.defaultLocal ?:
                    d.channel
        }

        checkTalkPermissions(d)
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
            if(checkTalkPermissions(channel, d.player)) {
                d.chatter.channels.focusChannel(channel)
                d.shouldContinue = false //Don't send anything, only switch channel
                d.player.sendMessage("${AQUA}You are now talking in: ${channel.formattedTitle}")
            }
            return
        } else {
            d.chatter.channels.subscribe(channel)
            d.channel = channel
            d.message = msg.substring(firstSpace+1)
        }
    }

    private fun checkCooldown(d: ChatAttempt){
        val c = d.channel
        val cd = c.cooldown
        if(cd <= 0 || d.player.hasPermission("chat.nocooldown") || d.player.hasPermission(c.permissionMod))
            return

        val seconds = "%.2f".format(d.chatter.channels.getRemainingCooldown(c).toNanos() / 1000000000.0)
        if(d.chatter.channels.isOrSetCooldown(c)){
            d.shouldContinue = false
            d.player.sendMessage( asError("You must wait $seconds seconds before talking in ${c.formattedTitle}") )
        }
    }

    private fun checkTalkPermissions(d: ChatAttempt) {
        d.shouldContinue = checkTalkPermissions(d.channel, d.player)
    }

    private fun checkTalkPermissions(c: Channel, p: Player) : Boolean{
        if(!c.canTalk(p)){
            p.sendMessage( asError("You do not have permission to talk in ${c.formattedTitle}") )
            return false
        }
        return true
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