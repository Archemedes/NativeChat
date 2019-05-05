package me.lotc.chat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.md_5.bungee.chat.ComponentSerializer

class FocusListener {

    fun listen(){
        ProtocolLibrary.getProtocolManager().asynchronousManager
            .registerAsyncHandler(object : PacketAdapter(Morphian.get(), PacketType.Play.Server.CHAT){
                override fun onPacketSending(event: PacketEvent){
                    val type = event.packet.chatTypes.read(0)
                    val chat = event.packet.chatComponents.read(0)

                    //TODO categorize
                    //TODO gatekeep
                    ComponentSerializer.parse(chat.json)
                }
            })
    }
}