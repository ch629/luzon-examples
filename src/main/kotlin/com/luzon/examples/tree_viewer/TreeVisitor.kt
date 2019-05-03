package com.luzon.examples.tree_viewer

import com.luzon.recursive_descent.ast.ASTNode
import com.luzon.recursive_descent.expression.ASTNodeVisitor
import com.luzon.recursive_descent.expression.accept

data class Group(val name: String, val children: List<Group>? = null) {
    constructor(name: String, vararg children: Group) : this(name, children.asList())
}

object TreeVisitor : ASTNodeVisitor<Group> {
    override fun visit(node: ASTNode.Block) =
        Group("Block", node.nodes.map { it.accept(this) })

    override fun visit(node: ASTNode.Class) =
        Group(
            "Class",
            Group("Name", Group(node.name)),
            node.constructor?.accept(this) ?: Group("No Constructor"),
            node.block.accept(this)
        )

    override fun visit(node: ASTNode.Constructor) =
        Group(
            "Group",
            Group("Params", node.variables.map { it.accept(this) })
        )

    override fun visit(node: ASTNode.ConstructorVariableDeclaration) =
        Group(
            "Param",
            Group(
                "Name",
                Group(node.name),
                Group("Type", Group(node.type))
            )
        )

    override fun visit(node: ASTNode.ElseStatements.ElseIfStatement) =
        Group("Else", node.ifStatement.accept(this))

    override fun visit(node: ASTNode.ElseStatements.ElseStatement) =
        Group("Else", node.block.accept(this))

    private fun binaryType(name: String, node: ASTNode.Expression.Binary) =
        Group(
            name,
            Group(
                "Left",
                node.left?.accept(this) ?: Group("Empty")
            ),
            Group(
                "Right",
                node.right?.accept(this) ?: Group("Empty")
            )
        )

    override fun visit(node: ASTNode.Expression.Binary.And) =
        binaryType("And", node)

    override fun visit(node: ASTNode.Expression.Binary.Div) =
        binaryType("Divide", node)

    override fun visit(node: ASTNode.Expression.Binary.Equals) =
        binaryType("Equals", node)

    override fun visit(node: ASTNode.Expression.Binary.Greater) =
        binaryType("Greater", node)

    override fun visit(node: ASTNode.Expression.Binary.GreaterEquals) =
        binaryType("Greater Equals", node)

    override fun visit(node: ASTNode.Expression.Binary.Less) =
        binaryType("Less", node)

    override fun visit(node: ASTNode.Expression.Binary.LessEquals) =
        binaryType("Less Equals", node)

    override fun visit(node: ASTNode.Expression.Binary.Mult) =
        binaryType("Multiply", node)

    override fun visit(node: ASTNode.Expression.Binary.NotEquals) =
        binaryType("Not Equals", node)

    override fun visit(node: ASTNode.Expression.Binary.Or) =
        binaryType("Or", node)

    override fun visit(node: ASTNode.Expression.Binary.Plus) =
        binaryType("Plus", node)

    override fun visit(node: ASTNode.Expression.Binary.Sub) =
        binaryType("Sub", node)

    override fun visit(node: ASTNode.Expression.LiteralExpr.BooleanLiteral) =
        Group("Boolean: ${node.value}")

    override fun visit(node: ASTNode.Expression.LiteralExpr.DotChainLiteral) =
        Group(
            node.value.accept(this).name,
            node.next?.accept(this) ?: Group("")
        )

    override fun visit(node: ASTNode.Expression.LiteralExpr.DoubleLiteral) =
        Group("Double: ${node.value}")

    override fun visit(node: ASTNode.Expression.LiteralExpr.FloatLiteral) =
        Group("Float: ${node.value}")

    override fun visit(node: ASTNode.Expression.LiteralExpr.FunctionCall) =
        Group(
            "Function Call",
            Group("Name", Group(node.name)),
            Group("Params", node.params.map { it.accept(this) })
        )

    override fun visit(node: ASTNode.Expression.LiteralExpr.IdentifierLiteral) =
        Group("Identifier: ${node.name}")

    override fun visit(node: ASTNode.Expression.LiteralExpr.IntLiteral) =
        Group("Int: ${node.value}")

    override fun visit(node: ASTNode.Expression.LiteralExpr.StringLiteral) =
        Group("String: ${node.value}")

    override fun visit(node: ASTNode.Expression.Unary.Decrement) =
        Group("Decrement", node.expr!!.accept(this))

    override fun visit(node: ASTNode.Expression.Unary.Increment) =
        Group("Increment", node.expr!!.accept(this))

    override fun visit(node: ASTNode.Expression.Unary.Not) =
        Group("Not", node.expr!!.accept(this))

    override fun visit(node: ASTNode.Expression.Unary.Sub) =
        Group("Minus", node.expr!!.accept(this))

    override fun visit(node: ASTNode.ForLoop) =
        Group(
            "For",
            Group("id", Group(node.id)),
            Group("Start", Group(node.start.toString())),
            Group("End", Group(node.end.toString())),
            node.block.accept(this)
        )

    override fun visit(node: ASTNode.FunctionDefinition) =
        Group(
            "Function Definition",
            Group("Name", Group(node.name)),
            Group("Params", node.parameters.map { it.accept(this) }),
            node.block.accept(this)
        )

    override fun visit(node: ASTNode.FunctionParameter) =
        Group("${node.name} : ${node.type}")

    override fun visit(node: ASTNode.IfStatement) =
        Group(
            "If",
            node.expr.accept(this),
            node.block.accept(this),
            Group(
                "Else",
                node.elseStatement?.accept(this) ?: Group("None")
            )
        )

    override fun visit(node: ASTNode.OperatorVariableAssign) =
        Group(
            "Var Assign",
            Group("Name: ${node.name}"),
            Group("Type: ${node.operator.regex}"),
            node.expr.accept(this)
        )

    override fun visit(node: ASTNode.Return) =
        Group(
            "Return",
            node.data?.accept(this) ?: Group("Empty")
        )

    override fun visit(node: ASTNode.VariableAssign) = Group(
        "Var Assign",
        Group("Name", Group(node.name)),
        node.expr.accept(this)

    )

    override fun visit(node: ASTNode.VariableDeclaration) =
        Group(
            "Var Declaration",
            Group("Name", Group(node.name)),
            node.expr.accept(this)
        )

    override fun visit(node: ASTNode.WhileLoop) =
        Group(
            "While",
            Group("Do", Group(node.doWhile.toString())),
            node.expr.accept(this),
            node.block.accept(this)
        )
}