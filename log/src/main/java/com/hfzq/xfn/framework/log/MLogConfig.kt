package com.hfzq.xfn.framework.log

import com.hfzq.xfn.framework.log.formatter.MStackTraceFormatter
import com.hfzq.xfn.framework.log.printer.MLogPrinter

open class MLogConfig {
    companion object {
        const val MAX_LEN = 512 //每一行最多显示这么多的字符
        val M_STACK_TRACE_FORMATTER = MStackTraceFormatter()
    }

    open fun enable(): Boolean {
        return true
    }

    open fun printers(): Array<MLogPrinter>? {
        return null
    }

    open fun injectJsonParser(): JsonParser? {
        return null
    }

    open fun stackTraceDepth(): Int {
        return 0
    }

    interface JsonParser {
        fun toJson(src: Any): String
    }
}