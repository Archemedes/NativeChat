package me.lotc.chat

import co.lotc.core.agnostic.Sender
import com.google.common.collect.Iterables
import com.google.common.io.ByteStreams
import me.lucko.luckperms.LuckPerms
import me.lucko.luckperms.api.User
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit

/**
 * Represents a player that was recently online somewhere on the proxy
 */
class ProxiedSender(private val username: String, private val user: User) : Sender {
    val uniqueId = user.uuid
    override fun getName() = username

    override fun sendMessage(msg: String?) {
        msg?: return

        val randomPlayer = Iterables.getFirst(Bukkit.getOnlinePlayers(), null) ?: return

        val out = ByteStreams.newDataOutput()
        out.writeUTF("Message")
        out.writeUTF(username)
        out.writeUTF(BungeeListener.SUBCHANNEL_NAME)
        out.writeUTF(msg)

        randomPlayer.sendPluginMessage(NativeChat.get(), "BungeeCord", out.toByteArray())
    }

    override fun sendMessage(msg: BaseComponent?) {
        msg?.let { sendMessage(it.toLegacyText()) }
    }

    override fun hasPermission(permission: String?): Boolean {
        permission?:return true

        val contextManager = LuckPerms.getApi().contextManager
        val contexts = contextManager.lookupApplicableContexts(user)
            .orElseGet { contextManager.staticContexts }

        val permissionData = user.cachedData.getPermissionData(contexts)
        return permissionData.getPermissionValue(permission).asBoolean()
    }

}