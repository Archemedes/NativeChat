package me.lotc.chat.user

import jdk.nashorn.internal.objects.Global
import me.lotc.chat.channel.*
import me.lotc.chat.message.ComposedMessage
import me.lotc.chat.user.Focus.Category
import me.lotc.chat.user.Focus.Category.*
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

typealias Chat = Array<BaseComponent>
typealias History = LinkedList<Chat>
const val MAX_HISTORY = 50

class Focus(val uuid : UUID) {
    private val categories = EnumMap<Category, History>(Category::class.java)
    private val lock = ReentrantReadWriteLock()

    var focus by Lockable(lock, NONE)
    val isFocused = focus != NONE

    init{
        Category.values().forEach { cat -> categories[cat] = LinkedList() }
        categories.remove(NONE)
    }

    internal fun acceptChat(channel: Channel, msg: ComposedMessage){
        Category.values()
           .filter { willAcceptChat(it, channel, msg) }
           .forEach { accept(it,msg.finalMessage)}
    }

    internal fun acceptSystem(msg: Chat){
        accept(SYSTEM, msg)
        //TODO
    }

    internal fun willAcceptChat(cat: Category, channel: Channel, msg: ComposedMessage): Boolean{
        if(cat != NONE && channel is BroadcastChannel) return true //Always show broadcasts

        return when (cat) {
            NONE, PM, SYSTEM -> false
            MENTION -> msg.context.has("mention:$uuid")
            STAFF -> (channel as? GlobalChannel)?.isStaff ?: false
            RP -> channel is LocalChannel && channel !is LOOCChannel
            LOCAL -> channel is LocalChannel
            GLOBAL -> channel is GlobalChannel || channel is BroadcastChannel
            CHAT -> true
        }
    }

    internal fun willAcceptSystem(cat: Category, channel: Channel, msg: Chat) : Boolean {
        return when (cat) {
            NONE, STAFF, RP, LOCAL, GLOBAL,CHAT -> false
            MENTION -> false //I think?
            PM -> msg.joinToString { it.toPlainText() }.matches(Regex("^\\[[A-Za-z0-9_]{3,16}->[A-Za-z0-9_]\\] .*") )
            SYSTEM -> true
        }
    }

    private fun toRaw(msg: Chat){
        msg.joinToString { it.toPlainText() }
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

    enum class Category(val tag: String, val color: ChatColor , val description : String = tag){
        NONE("All", WHITE),
        MENTION("@", GOLD,"Mentions"),
        STAFF("Staff", DARK_PURPLE),
        RP("RP",DARK_GREEN, "Roleplay"),
        LOCAL("Local", BLUE),
        GLOBAL("Global", RED),
        PM("PM", AQUA, "Private Message"),
        CHAT("Chat", GREEN),
        SYSTEM("System", LIGHT_PURPLE),
    }
}