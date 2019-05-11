package me.lotc.chat

import com.google.common.collect.Iterables
import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream


class OutgoingChatPacket {
    private val out = ByteStreams.newDataOutput()

    private val payloadBytes = ByteArrayOutputStream()
    val payload = DataOutputStream(payloadBytes)

    init{
        out.writeUTF("Forward")
        out.writeUTF("ALL")
        out.writeUTF(BungeeListener.SUBCHANNEL_NAME)
    }

    fun send(socket: Player? = null){
        var p = socket
        if(p == null) p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null)
        p?:return

        val array = payloadBytes.toByteArray()
        out.writeShort(array.size)
        out.write(array)
        p.sendPluginMessage(NativeChat.get(), "BungeeCord", out.toByteArray())
    }

}