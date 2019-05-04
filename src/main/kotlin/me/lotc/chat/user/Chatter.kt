package me.lotc.chat.user

import me.lotc.chat.Morphian
import me.lotc.chat.channel.Channel
import co.lotc.core.agnostic.Sender
import co.lotc.core.bukkit.wrapper.BukkitSender
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import me.lucko.luckperms.LuckPerms
import me.lucko.luckperms.api.User
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KProperty

val Sender.player: Player? get() = ((this as? BukkitSender)?.handle as? Player)
val Player.chat: Chatter get() = Morphian.get().chatManager.getChatSettings(this)
val Sender.chat: Chatter? get() = this.player?.chat

operator fun Multimap<*,*>.contains(key: Any) = this.containsKey(key)

class Chatter(player: Player) {
    private val lock = ReentrantReadWriteLock()
    private val uuid = player.uniqueId
    val player get() = Bukkit.getPlayer(uuid)!! //Chatter must only exist if Player is online

    val channels = ChannelData(uuid, lock)
    val channel get() = channels.channel
    val subscribedChannels get() = channels.subscribedChannels as List<Channel>

    val continuity = StringBuilder()
    val focus = Focus()

    var emoteColor by Lockable(lock, ChatColor.YELLOW)
    var wantsTimestamps by Lockable(lock, false)
    var emoteStyle by Lockable(lock, EmoteStyle.EXPLICIT)
    var correctPunctuation by Lockable(lock, true)
    var isMentionable by Lockable(lock, true)


    fun saveSettings(){
        val api = LuckPerms.getApi()
        val user = api.getUser(uuid)

        user?: throw IllegalStateException("saving Chatter settings but no LuckPerms for $uuid")
        user.clearMatching { n->n.isMeta && n.meta.key.startsWith("rp_") }
        lock.read {
            for(chan in channels.subscribedChannels)
                if(!chan.isPermanent) metaNode(user, "rp_channel", chan.cmd)

            metaNode(user, "rp_focus", channel.cmd)
            metaNode(user, "rp_emotecolor", emoteColor.name)
            metaNode(user, "rp_emotestyle", emoteStyle.name)

            metaNode(user, "rp_timestamps", wantsTimestamps.toString())
            metaNode(user, "rp_punctuate", correctPunctuation.toString())
        }
        api.userManager.saveUser(user)
    }

    private fun metaNode(user: User, key: String, value: String) {
        user.setPermission(LuckPerms.getApi().nodeFactory.makeMetaNode(key,value).build())
    }

    fun loadSettings(){
        //Doesn't need a lock: Only called onJoin when object not yet exposed to other threads
        val chatManager = Morphian.get().chatManager

        val user = LuckPerms.getApi().getUser(uuid)
        user?: throw IllegalStateException("loading Chatter settings but no LuckPerms for $uuid")
        @Suppress("UnstableApiUsage")
        val settings = MultimapBuilder.hashKeys().arrayListValues().build<String, String>()

        //Go through LuckPerms to find all the meta nodes that are RPEngine settings nodes
        user.allNodes.stream().filter { it.isMeta }.filter{ it.value }.filter{it.isPermanent}.filter{ it.appliesGlobally() }
            .map { it.meta }.filter { it.key.startsWith("rp_") }.forEach { settings.put(it.key,it.value) }

        settings["rp_channel"].forEach{chatManager.getByAlias(it)?.run(channels.subscribedChannels::add) }

        chatManager.channels.stream().filter { it.isPermanent }
            .filter { !channels.isSubscribed(it) }
            .filter { player.hasPermission(it.permission)}
            .forEach{ channels.subscribedChannels.add(it) }

        if("rp_focus" in settings) channels.channel = chatManager.getByAlias(settings["rp_focus"][0]) ?: channel
        if("rp_emotecolor" in settings) emoteColor = ChatColor.valueOf(settings["rp_emotecolor"][0])
        if("rp_emotestyle" in settings) emoteStyle = EmoteStyle.valueOf(settings["rp_emotestyle"][0])

        if("rp_timestamps" in settings) wantsTimestamps = settings["rp_timestamps"][0]!!.toBoolean()
        if("rp_punctuate" in settings) correctPunctuation = settings["rp_punctuate"][0]!!.toBoolean()
        if("rp_mention" in settings) isMentionable = settings["rp_mention"][0]!!.toBoolean()
    }
}

enum class EmoteStyle {
    ALWAYS,
    QUOTATIONS,
    EXPLICIT
}

class Lockable<T>(private val lock: ReentrantReadWriteLock, private var field : T)  {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        lock.read { return field }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        lock.write { field = value }
    }
}