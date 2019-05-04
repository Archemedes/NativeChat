package me.lotc.chat.format.`in`

import me.lotc.chat.message.Message

class Punctuation : InFormatter {
    companion object {
        val replaces = mapOf(
            "im" to "I'm",
            "i" to "I",
            "i'm" to "I'm",
            "doesnt" to "doesn't",
            "theres" to "there's",
            "youre" to "you're",
            "wheres" to "where's",
            "yall" to "y'all",
            "whered" to "where'd",
            "weve" to "we've",
            "thats" to "that's",
            "dont" to "don't",
            "couldnt" to "couldn't",
            "mustnt" to "mustn't",
            "wouldnt" to "wouldn't",
            "howre" to "how're",
            "howve" to "how've",
            "isnt" to "isn't",
            "havent" to "haven't",
            "shouldnt" to "shouldn't",
            "shouldve" to "should've",
            "wont" to "won't",
            "youll" to "you'll",
            "wasnt" to "wasn't",
            "werent" to "weren't",
            "theyd" to "they'd",
            "heres" to "here's",
            "arent" to "aren't",
            "aint" to "ain't",
            "hadnt" to "hadn't",
            "howd" to "how'd",
            "hows" to "how's",
            "howll" to "how'll",
            "id" to "I'd",
            "itd" to "it'd",
            "itll" to "it'll",
            "whatve" to "what've",
            "whats" to "what's",
            "i'll" to "I'll",
            "didn't" to "didn't",
            "gday" to "g'day")
    }

    override fun format(message: Message) {
        val c = message.chatter ?: return
        if(!c.correctPunctuation) return

        for(txt in message.content){
            val lc = txt.content.toLowerCase()
            for( (ugly,nice) in replaces){
                val uglySpaced = " $ugly "
                var index = lc.indexOf( uglySpaced )
                while( index != -1 && index < lc.length){
                    val resumeAt = index + uglySpaced.length
                    txt.map { m-> m.substring(0, index) + " $nice " + m.substring(resumeAt) }
                    index = lc.indexOf(uglySpaced, resumeAt)
                }
            }
            var nc = txt.content
            nc = capitalize(nc, '.')
            nc = capitalize(nc, '?')
            nc = capitalize(nc, '!')
            txt.content = nc
        }
    }

    private fun capitalize(content: String, dot: Char): String {
        val find = "$dot "
        var lc = content
        var index = content.indexOf(find)
        while(index != -1 && index+2<lc.length){
            val capital = lc.elementAt(index+2)
            if(capital.isLowerCase()) lc = lc.substring(0, index+2) + capital.toUpperCase() + lc.substring(index+3)
            index = lc.indexOf(find, index+3)
        }

        return lc
    }
}