package com.hfzq.xfn.framework.log.formatter


class MStackTraceFormatter : MLogFormatter<Array<StackTraceElement?>> {
    override fun format(stackTrace: Array<StackTraceElement?>): String {
        val sb = StringBuilder(128)
        return when {
            stackTrace.isEmpty() -> {
                ""
            }

            stackTrace.size == 1 -> {
                "\t─ " + stackTrace[0].toString()
            }

            else -> {
                for (i in stackTrace.indices) {
                    if (i == 0) {
                        sb.append("stackTrace:  \n")
                    }
                    if (i != stackTrace.size - 1) {
                        sb.append("\t├ ")
                        sb.append(stackTrace[i].toString())
                        sb.append("\n")
                    } else {
                        sb.append("\t└ ")
                        sb.append(stackTrace[i].toString())
                    }
                }
                sb.toString()
            }
        }
    }
}