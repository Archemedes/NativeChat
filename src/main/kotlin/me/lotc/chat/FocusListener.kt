package me.lotc.chat

import com.comphenix.protocol.AsynchronousManager
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import me.lotc.chat.user.chat
import net.md_5.bungee.chat.ComponentSerializer

class FocusListener {

    fun listen(){
        manager().registerAsyncHandler(object : PacketAdapter(Morphian.get(), PacketType.Play.Server.CHAT){
            override fun onPacketSending(event: PacketEvent){
                if(event.isCancelled) return

                val type = event.packet.chatTypes.read(0)

                if(type == EnumWrappers.ChatType.SYSTEM){
                    val chat = event.packet.chatComponents.read(0)
                    val focus = event.player.chat.focus

                    val msg = ComponentSerializer.parse(chat.json)
                    focus.acceptSystem(msg)

                    if(!msg[0].toPlainText().startsWith("Error:") && !focus.willAcceptSystem(msg))
                        event.isCancelled = true
                }
            }
        })
    }

    private fun manager(): AsynchronousManager {
        return ProtocolLibrary.getProtocolManager().asynchronousManager
    }
}