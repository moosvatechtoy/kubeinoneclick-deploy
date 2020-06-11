package com.prokarma.oneclick.hcl

import com.prokarma.oneclick.hcl.antlr.hclBaseVisitor
import com.prokarma.oneclick.hcl.antlr.hclParser
import com.prokarma.oneclick.modules.bo.Output
import com.prokarma.oneclick.modules.bo.Variable
import java.util.*

class HclVisitor : com.prokarma.oneclick.hcl.antlr.hclBaseVisitor<Unit>() {

    var variables: MutableList<Variable> = LinkedList()
    var outputs: MutableList<Output> = LinkedList()
    var provider: String = "unknown"

    private val IGNORED_PROVIDERS = setOf("null", "random", "template", "terraform")

    private var currentVariable: Variable = Variable(name = "")
    private var currentOutput: Output = Output()


    override fun visitVariableDirective(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.VariableDirectiveContext) {
        currentVariable = Variable(name = ctx.STRING().text.removeSurrounding("\""))
        variables.add(currentVariable)
        visitChildren(ctx)
    }

    override fun visitVariableType(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.VariableTypeContext) {
        currentVariable.type = ctx.type().text.removeSurrounding("\"")
    }

    override fun visitVariableDefault(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.VariableDefaultContext) {
        currentVariable.defaultValue = ctx.expression().text.removeSurrounding("\"")
    }

    override fun visitVariableDescription(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.VariableDescriptionContext) {
        currentVariable.description = ctx.STRING().text.removeSurrounding("\"")
    }

    override fun visitOutputDirective(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.OutputDirectiveContext) {
        currentOutput = Output(name = ctx.STRING().text.removeSurrounding("\""))
        outputs.add(currentOutput)
        visitChildren(ctx)
    }

    override fun visitOutputDescription(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.OutputDescriptionContext) {
        currentOutput.description = ctx.STRING().text.removeSurrounding("\"")
    }

    override fun visitOutputValue(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.OutputValueContext) {
        currentOutput.value = ctx.expression().text.removeSurrounding("\"")
    }

    override fun visitOutputSensitive(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.OutputSensitiveContext) {
        currentOutput.sensitive = ctx.BOOLEAN().text.removeSurrounding("\"")
    }

    override fun visitProviderDirective(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.ProviderDirectiveContext) {
        val parsedProvider = ctx.STRING().text.removeSurrounding("\"")
        if (! IGNORED_PROVIDERS.contains(parsedProvider)) {
            provider = parsedProvider
        }
    }

    override fun visitResourceDirective(ctx: com.prokarma.oneclick.hcl.antlr.hclParser.ResourceDirectiveContext) {
        // provider already found !
        if (provider != "unknown") return

        // check first part of the resource type
        provider = ctx.STRING(0).text.removeSurrounding("\"")
                .substringBefore("_")
    }
}