package co.lotc.chat

import org.bukkit.entity.Player
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

val Player.chat: Chatter
    get() = Morphian.chatManager.getChatSettings(this)

class Chatter(player: Player) {
    val uuid = player.uniqueId
    val lock = ReentrantReadWriteLock()

    var channel = Morphian.chatManager.primordial
        get() = lock.read { return field }
        set(channel) = lock.write { field = channel }


    fun saveSettings(){
        lock.read {
            //TODO luckperms meta pex integration
        }
    }

    fun loadSettings(){
        //Doesn't need a lock: Only called onJoin when object not yet exposed to other threads
        //TODO luckperms meta pex integration
    }
}