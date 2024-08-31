package com.hfzq.xfn.framework.log.formatter

interface MLogFormatter<T> {
    fun format(data: T): String
}