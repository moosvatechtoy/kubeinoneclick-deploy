package com.prokarma.oneclick.hcl

import com.prokarma.oneclick.hcl.antlr.hclLexer
import com.prokarma.oneclick.hcl.antlr.hclParser
import com.prokarma.oneclick.modules.bo.Output
import com.prokarma.oneclick.modules.bo.Variable
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.springframework.stereotype.Service

interface HclParser {
    fun parseContent(content: String): com.prokarma.oneclick.hcl.HclVisitor
    fun parseVariables(content: String): List<Variable>
    fun parseOutputs(content: String): List<Output>
    fun parseProvider(content: String): String
}

@Service
class HclParserImpl : com.prokarma.oneclick.hcl.HclParser {

    override fun parseContent(content: String): com.prokarma.oneclick.hcl.HclVisitor {
        // loading test file
        val charStream = CharStreams.fromString(content)

        // configuring antlr lexer
        val lexer = com.prokarma.oneclick.hcl.antlr.hclLexer(charStream)

        // using the lexer to configure a token stream
        val tokenStream = CommonTokenStream(lexer)

        // configuring antlr parser using the token stream
        val parser = com.prokarma.oneclick.hcl.antlr.hclParser(tokenStream)

        // visit the AST
        val hclVisitor = com.prokarma.oneclick.hcl.HclVisitor()
        hclVisitor.visit(parser.file())
        return hclVisitor
    }

    override fun parseVariables(content:String): List<Variable> {
        val hclVisitor = parseContent(content)
        return hclVisitor.variables
    }

    override fun parseOutputs(content:String): List<Output> {
        val hclVisitor = parseContent(content)
        return hclVisitor.outputs
    }

    override fun parseProvider(content: String): String {
        val hclVisitor = parseContent(content)
        return hclVisitor.provider
    }
}