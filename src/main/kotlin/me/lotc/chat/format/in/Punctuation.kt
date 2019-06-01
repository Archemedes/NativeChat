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
            "youve" to "you've",
            "youd" to "you'd",
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
            "didnt" to "didn't",
            "gday" to "g'day",
            "cant" to "can't",
            "whos" to "who's")
    }

    override fun format(message: Message) {
        val c = message.chatter ?: return
        if(!c.correctPunctuation) return



        //Capitalize first letter if its not an emote and everything after .?!
        val capDot = CapParser('.', "emote" !in message.context)
        val capQmk = CapParser('?')
        val capExc = CapParser('!')
        for(txt in message.content){
            txt.map { m->m.replace(Regex("\\.\\.\\."),"…")}

            for( (ugly,nice) in replaces){
                txt.map { m->m.replace(Regex("\\b$ugly\\b",RegexOption.IGNORE_CASE),nice)}
            }
            var nc = txt.content
            nc = capDot.capitalize(nc)
            nc = capQmk.capitalize(nc)
            nc = capExc.capitalize(nc)
            txt.content = nc
        }

        val lastText = message.content.last
        var last = lastText.content

        //Add a period if no punctuation found at the very end
        var replaceQuote = false
        if (last.endsWith("”")) {
            replaceQuote = true
            last = last.trimEnd('”')
        }

        if(last.isNotBlank()) {
            if(last.last() != '.' && last.last() != ',' && last.last() != '!' && last.last() != '?')
                last += '.'
            if(replaceQuote) last += '”'

            lastText.content = last
        }
    }

    private class CapParser(val dot: Char, var capitalize: Boolean = false){

        fun capitalize(content: String): String {
            val sb = StringBuilder()
            for (x in content) {
                if (x == dot) {
                    capitalize = true
                    sb.append(x)
                } else if (capitalize && x.isLetter()) {
                    sb.append(x.toUpperCase())
                    capitalize = false
                } else {
                    sb.append(x)
                }
            }

            return sb.toString()
        }
    }
}