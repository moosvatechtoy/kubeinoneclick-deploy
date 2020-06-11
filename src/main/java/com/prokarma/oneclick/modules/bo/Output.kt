package com.prokarma.oneclick.modules.bo

/**
 * Represents a module output
 */
data class Output(
        var name: String = "",
        var value: String = "",
        var description: String = "",
        var sensitive: String = "false");
