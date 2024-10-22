package com.aidarn.richedit.data

import kotlin.time.Duration


sealed interface Token {
    fun acceptVisitor(visitor: TokenVisitor)

    data class Text(val content: String) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class Bold(val content: List<Token>) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class Italic(val content: List<Token>) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class Heading(val level: Int, val content: List<Token>) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class UnorderedList(val content: List<List<Token>>) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class OrderedList(val content: List<List<Token>>, val startIndex: Int) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class Timer(val duration: Duration) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }

    data class Image(val imageModel: Any, val placeholderText: String) : Token {
        override fun acceptVisitor(visitor: TokenVisitor) {
            visitor.visit(this)
        }
    }
}

interface TokenVisitor {
    fun visit(token: Token.Text)
    fun visit(token: Token.Bold)
    fun visit(token: Token.Italic)
    fun visit(token: Token.Heading)
    fun visit(token: Token.UnorderedList)
    fun visit(token: Token.OrderedList)
    fun visit(token: Token.Timer)
    fun visit(token: Token.Image)
}