package me.lotc.chat.user

import me.lotc.chat.channel.BroadcastChannel
import me.lotc.chat.channel.Channel
import me.lotc.chat.channel.GlobalChannel
import me.lotc.chat.channel.LocalChannel
import me.lotc.chat.message.ComposedMessage
import net.md_5.bungee.api.chat.BaseComponent
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

typealias History = LinkedList<BaseComponent>
const val MAX_HISTORY = 50

class Focus(val owner: Chatter) {
    private val categories = EnumMap<Category, History>(Category::class.java)
    private val lock = ReentrantReadWriteLock()

    init{ Category.values().forEach { cat -> categories[cat] = LinkedList() } }

    internal fun acceptChat(channel: Channel, msg: ComposedMessage){
        val comp = msg.toSingleComponent()
        acceptChat(channel, comp)
    }

    internal fun acceptChat(channel: Channel, comp: BaseComponent){
        lock.read { //Claim read lock so message A doesn't get sorted before message B and vice-versa in different categories
            accept(Category.CHAT, comp)
            if(channel is GlobalChannel){
                accept(Category.OOC, comp)
                if(channel.isStaff) accept(Category.STAFF, comp)
            } else if(channel is LocalChannel) {
                accept(Category.RP, comp)
            } else if(channel is BroadcastChannel){
                accept(Category.OOC, comp)
                accept(Category.SYSTEM, comp)
            }
        }
    }

    internal fun acceptSystem(msg: BaseComponent){
        accept(Category.SYSTEM, msg)
        //TODO
    }

    fun accept(cat: Category, msg: BaseComponent){
        lock.write {
            val h = history(cat)
            h.addLast(msg)
            if(h.size > MAX_HISTORY) h.removeFirst()
        }
    }

    private fun history(cat: Category) : History {
        lock.read { return categories[cat]!! }
    }

    enum class Category{
        NONE,
        MENTION,
        STAFF,
        RP,
        OOC,
        PM,
        SYSTEM,
        CHAT,
    }
}