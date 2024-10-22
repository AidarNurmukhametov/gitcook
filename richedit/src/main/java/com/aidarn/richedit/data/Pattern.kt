package com.aidarn.richedit.data

object Pattern {
    object Line {
        val Timer = Regex("""(?m)\n?>!TIMER\s(.+)\n?""")
        val Heading2 = Regex("""(?m)^##\s(.*\n?)""")
        val Heading1 = Regex("""(?m)^#\s(.*\n?)""")
        val OrderedList = Regex("""(?m)^(\d+)\.\s(.*\n?)""")
        val RawUnorderedList = Regex("""(?m)^-\s(.*\n?)""")
    }

    object Entry {
        val OrderedList = Regex("""(?m)^(\d+)\.\s(.*\n?)""")
        val UnorderedList = Regex("""(?m)^\u2022\s(.*\n?)""")
    }

    object Head {
        val OrderedList = Regex("""(?m)^(\d+)\.\s""")
        val ProcessedUnorderedList = Regex("""(?m)^\u2022\s""")
    }

    val Image = Regex("""(?m)\n?!(\[(.*)]\((.+)\))\n?""")
}