package com.hfzq.xfn.framework.log.printer

import com.hfzq.xfn.framework.log.MLogConfig

interface MLogPrinter {
    fun print(config: MLogConfig, level: Int, tag: String, printString: String)
}