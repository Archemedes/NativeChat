package me.lotc.chat.user

import co.lotc.core.command.brigadier.TooltipProvider
import jdk.nashorn.internal.objects.Global
import me.lotc.chat.channel.*
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.user.Focus.Category
import me.lotc.chat.user.Focus.Category.*
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

typealias Chat = Array<BaseComponent>
typealias History = LinkedList<Chat>
const val MAX_HISTORY = 50

class Focus(private val uuid : UUID) {
    private val categories = EnumMap<Category, History>(Category::class.java)
    private val lock = ReentrantReadWriteLock()

    var focus by Lockable(lock, ALL)

    init{
        Category.values().forEach { cat -> categories[cat] = LinkedList() }
        //categories.remove(ALL) //I think we also need an "all" history
    }

    @Suppress("DEPRECATION")
    fun resend(){
        val p = Bukkit.getPlayer(uuid)!!
        repeat(20) { p.sendMessage(ChatMessageType.CHAT, TextComponent("")) }

        categories[focus]!!.forEach { p.sendMessage(ChatMessageType.CHAT, *it) }
    }

    internal fun acceptChat(channel: Channel, msg: ComposedMessage){
        categories.keys
           .filter { willAcceptChat(it, channel, msg) }
           .forEach { accept(it,msg.finalMessage)}
    }

    internal fun acceptSystem(msg: Chat){
        categories.keys
            .filter { willAcceptSystem(it) }
            .forEach { accept(it,msg)}
    }

    fun willAcceptChat(channel: Channel, msg: ComposedMessage) : Boolean {
        return willAcceptChat(focus, channel, msg)
    }

    private fun willAcceptChat(cat: Category, channel: Channel, msg: ComposedMessage): Boolean{
        if(channel is BroadcastChannel) return true //Always show broadcasts

        return when (cat) {
            SYSTEM -> false
            MENTION -> msg.context.has("mention:$uuid")
            STAFF -> (channel as? GlobalChannel)?.isStaff ?: false
            RP -> channel is LocalChannel && channel !is LOOCChannel
            LOCAL -> channel is LocalChannel
            GLOBAL -> channel is GlobalChannel || channel is BroadcastChannel
            ALL,CHAT -> true
        }
    }

    fun willAcceptSystem() : Boolean {
        return willAcceptSystem(focus)
    }

    private fun willAcceptSystem(cat: Category) : Boolean {
        return when (cat) {
            STAFF, RP, LOCAL, GLOBAL,CHAT -> false
            MENTION -> false //I think?
            ALL,SYSTEM -> true
        }
    }

    fun accept(cat: Category, msg: Chat){
        lock.write {
            val h = history(cat)
            h.addLast(msg)
            if(h.size > MAX_HISTORY) h.removeFirst()
        }
    }

    private fun history(cat: Category) : History {
        lock.read { return categories[cat]!! }
    }

    enum class Category(val tag: String, val color: ChatColor , val description : String = tag) : TooltipProvider{
        ALL("All", WHITE),
        MENTION("@", GOLD,"Mentions"),
        STAFF("Staff", DARK_PURPLE),
        RP("RP",DARK_GREEN, "Roleplay"),
        LOCAL("Local", BLUE),
        GLOBAL("Global", RED),
        CHAT("Chat", GREEN),
        SYSTEM("System", LIGHT_PURPLE);

        override fun getTooltip()= color.toString() + description
    }
}