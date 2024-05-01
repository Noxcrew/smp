package com.noxcrew.smp

/**
 * CLI for testing/using SMP.
 */
object SMPCommandLine {
    /**
     * Main entrypoint for the application.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val printRpn = "--print-rpn" in args

        while (true) {
            print("Enter an expression: ")
            val input = readln().trim()

            if (input.equals("exit", true)) {
                return
            }

            val expression = SMP.parse(input)

            if (printRpn) {
                println("RPN: ${expression.rpnSortedTokens.joinToString(separator = " ")}")
            }

            println("Result: ${SMP.computeUnresolved(input)}.")
            println()
        }
    }
}
