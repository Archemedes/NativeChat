package me.lotc.chat.channel


import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.ConfigurationSection

class YamlChannelBuilder(tagCandidate: String, private val c: ConfigurationSection) {
    private val tag = c.getString("tag", tagCandidate)!!
    private val title = c.getString("title", tagCandidate)!!
    private val cmd = c.getString("cmd", tagCandidate)!!.toLowerCase()
    private val color = ChatColor.valueOf(c.getString("color", "DARK_GRAY")!!)
    private val bracketColor = ChatColor.valueOf(c.getString("bracketcolor", color.name)!!)
    private val cooldown = c.getInt("cooldown", 0)

    constructor(c: ConfigurationSection) : this(c.name, c)

    fun asLocalChannel() : LocalChannel {
        val radius = c.getInt("radius", 20)
        val quiet = c.getBoolean("quiet", false)

        var looc : YamlChannelBuilder? = null
        if( c.isConfigurationSection("looc")) {
            val c2 = c.getConfigurationSection("looc")!!
            looc = YamlChannelBuilder(c2.getString("tag")!!, c2)
        }
        return LocalChannel(tag, title, cmd, color, bracketColor, cooldown, radius, quiet, looc)
    }

    fun asGlobalChannel() : GlobalChannel {
        val isPermanent = c.getBoolean("permanent", false)
        val isStaff = c.getBoolean("staff", false)
        val bold = c.getBoolean("bold", false)
        return GlobalChannel(
            tag,
            title,
            cmd,
            bold,
            color,
            bracketColor,
            isPermanent,
            cooldown,
            isStaff
        )
    }

    fun asLOOCChannel(parent: LocalChannel) : LOOCChannel {
        return LOOCChannel(parent, tag, title, cmd, cooldown)
    }

    fun asBroadcastChannel() : BroadcastChannel {
        val textColor = ChatColor.valueOf(c.getString("textcolor", "GRAY")!!)
        val signName = c.getBoolean("signname", false)
        return BroadcastChannel(tag, title, cmd, color, bracketColor, textColor, signName)
    }
}