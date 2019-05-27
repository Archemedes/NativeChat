package me.lotc.chat.user

import me.lotc.chat.NativeChat
import me.lotc.chat.channel.Channel
import co.lotc.core.bukkit.util.Run
import me.lucko.luckperms.LuckPerms
import me.lucko.luckperms.api.Node
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList
import kotlin.concurrent.read
import kotlin.concurrent.write

class ChannelData(val owner: UUID, val lock: ReentrantReadWriteLock) {
    internal val subscribedChannels = HashSet<Channel>()
    var channel = NativeChat.get().chatManager.primordial
        internal set

    private val cooldowns = ConcurrentHashMap<String, Instant>()

    fun isSubscribed(channel: Channel) : Boolean{
        lock.read { return subscribedChannels.contains(channel) }
    }

    fun subscribe(channel: Channel) {
        lock.write { if(channel !in subscribedChannels) subscribedChannels.add(channel) }
    }

    fun unsubscribe(channel: Channel){
        lock.write {
            if(this.channel === channel) this.channel = NativeChat.get().chatManager.primordial
            subscribedChannels.remove(channel)
        }
    }

    fun focusChannel(channel: Channel){
        lock.write {
            this.channel = channel
            subscribe(channel)
        }
    }

    ///LuckPerms-powered ban and mute commands

    fun ban(channel: Channel, duration: Duration? = null){
        unsubscribe(channel)
        saveOnUser(negatedNode(channel.permission, duration))
    }

    fun isBanned(channel: Channel) : Boolean {
        return isNegated(channel.permission)
    }

    fun unban(channel: Channel){
        saveOnUser(negatedNode(channel.permission), true)
    }

    fun isGlobalMuted() : Boolean { //TODO
        return isNegated("chat.*.talk")
    }

    fun mute(duration: Duration? = null){
        saveOnUser(negatedNode("chat.*.talk", duration))
    }

    fun isMuted(channel: Channel) : Boolean {
        return isNegated(channel.permissionTalk)
    }

    fun mute(channel: Channel, duration: Duration? = null){
        saveOnUser(negatedNode(channel.permissionTalk, duration))
    }

    fun unmute(channel: Channel){
        saveOnUser(negatedNode(channel.permissionTalk), true)
    }

    private fun negatedNode(permission: String, duration : Duration? = null) : Node {
        val node = lucko().nodeFactory.newBuilder(permission).setNegated(true)
        duration?.let{ node.setExpiry(Instant.now().plus(it).toEpochMilli()) }
        return node.build()
    }

    private fun saveOnUser(node: Node, remove : Boolean = false){
        val api = lucko()
        val user = api.getUser(owner)!!
        if(remove) user.unsetPermission(node) else user.setPermission(node)
        api.userManager.saveUser(user)
    }

    private fun isNegated(permission: String) : Boolean{
        return lucko().getUser(owner)!!.permissions.stream()
            .filter(Node::isNegated)
            .anyMatch { n->n.permission == permission }
    }

    private fun lucko() = LuckPerms.getApi()

    fun getRemainingCooldown(channel: Channel) : Duration {
        var ins = cooldowns[channel.cmd] ?: return Duration.ZERO
        ins = ins.plusSeconds(channel.cooldown.toLong())
        val now = Instant.now()!!
        if(ins.isBefore(now)) return Duration.ZERO

        return Duration.between(now, ins)
    }

    fun isOrSetCooldown(channel: Channel) : Boolean{
        val name = channel.cmd
        val ins = cooldowns[name]
        if(ins == null) {
            cooldowns[name] = Instant.now()
            Run(NativeChat.instance).delayed(channel.cooldown * 20L) { cooldowns.remove(channel.cmd) }
        }

        return ins != null
    }
}