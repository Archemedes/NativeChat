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
            //val lc = txt.content.toLowerCase()
            for( (ugly,nice) in replaces){
                txt.map { m->m.replace(Regex("\\b$ugly\\b,",RegexOption.IGNORE_CASE),nice)}
/*                val uglySpaced = " $ugly "

                var index = lc.indexOf( uglySpaced )
                while( index != -1 && index < lc.length){
                    val resumeAt = index + uglySpaced.length
                    txt.map { m-> m.substring(0, index) + " $nice " + m.substring(resumeAt) }
                    index = lc.indexOf(uglySpaced, resumeAt)
                }*/
            }
            var nc = txt.content
            nc = capitalize(nc, '.')
            nc = capitalize(nc, '?')
            nc = capitalize(nc, '!')
            txt.content = nc
        }

        message.content.first.map(::capitalizeFirst)
    }

    private fun capitalize(content: String, dot: Char): String {
        val sb = StringBuilder()
        var capitalize = false
        for(x in content){
            if(x==dot) {
                capitalize = true
                sb.append(x)
            } else if(capitalize && x.isLetter()) {
                sb.append(x.toUpperCase())
                capitalize = false
            } else {
                sb.append(x)
            }
        }

        return sb.toString()
    }

    private fun capitalizeFirst(content:String) : String {
        val sb = StringBuilder()
        for(i in 0 until content.length){
            val x = content[i]
            if(x.isLetter()){
                sb.append(x.toUpperCase())
                if(i + 1 < content.length) sb.append(content.substring(i+1))
                break
            }  else {
                sb.append(x)
            }
        }

        return sb.toString()
    }
}