package com.hfzq.xfn.framework.log

import android.util.Log
import com.hfzq.xfn.framework.log.printer.MFilePrinter
import com.hfzq.xfn.framework.log.utils.MStackTraceUtil

object MLog {
    private val M_LOG_PACKAGE: String =
        MLog::class.java.name.substring(0, MLog::class.java.name.lastIndexOf('.') + 1)
    private var mClassname: String? = null
    private var mMethods: ArrayList<String>? = null

    init {
        mClassname = MLog::class.java.name
        mMethods = ArrayList()

        val ms = MLog::class.java.declaredMethods
        for (m in ms) {
            mMethods!!.add(m.name)
        }
    }

    fun d(contents: Any) {
        log(Log.DEBUG, "", contents)
    }

    fun d(tag: String = "", contents: Any) {
        log(Log.DEBUG, tag, contents)
    }

    fun i(contents: Any) {
        log(Log.INFO, "", contents)
    }

    fun i(tag: String = "", contents: Any) {
        log(Log.INFO, tag, contents)
    }

    fun w(contents: Any) {
        log(Log.WARN, "", contents)
    }

    fun w(tag: String = "", contents: Any) {
        log(Log.WARN, tag, contents)
    }

    fun e(contents: Any) {
        log(Log.ERROR, "", contents)
    }

    fun e(tag: String = "", contents: Any) {
        log(Log.ERROR, tag, contents)
    }

    /**
     * 用这个方法，只要设置了filePrinter，就一定会存在本地（注意数据脱敏）
     */
    fun logToLocal(contents: Any) {
        log(MLogManager.getConfig(), Log.DEBUG, "", contents, forceToLocal = true)
    }

    fun log(type: Int, tag: String, contents: Any) {
        log(MLogManager.getConfig(), type, tag, contents)
    }

    fun log(
        config: MLogConfig,
        type: Int = Log.DEBUG,
        tag: String = "",
        contents: Any,
        forceToLocal: Boolean = false
    ) {
        //如果日志关闭，do nothing
        //如果需要强制存日志，那么逻辑还需要走下去
        if (!config.enable() && !forceToLocal) {
            return
        }
        val sb = StringBuilder()
        var mTag = tag
        var mLineNum = ""

        //获得tag和行号
        val extraInfo = getTagAndLineNumber(mTag)
        if (extraInfo.size >= 2) {
            mTag = extraInfo[0]
            mLineNum = extraInfo[1]
        }

        //根据深度打印调用栈
        if (config.stackTraceDepth() > 0) {
            val stackTrace = MLogConfig.M_STACK_TRACE_FORMATTER.format(
                MStackTraceUtil.getCroppedRealStackTrack(
                    Throwable().stackTrace,
                    M_LOG_PACKAGE,
                    config.stackTraceDepth()
                )
            )
            sb.append(stackTrace).append("\n")
        }

        //获得需要打印的真正日志
        var body = parseBody(config, contents, mLineNum)
        if (body.isNotBlank()) {
            body = body.replace("\\\"", "\"")
        }
        sb.append(body)
        //获得注册的打印器列表
        val printers = if (config.printers() != null) {
            config.printers()!!.asList()
        } else {
            MLogManager.getPrinters()
        }
        printers.forEach {
            //如果当前日志关闭了，但需要强制存本地，还是会触达到存本地逻辑
            if (!config.enable() && forceToLocal) {
                if (it is MFilePrinter) {
                    it.print(config, type, mTag, sb.toString())
                }
            } else {
                it.print(config, type, mTag, sb.toString())
            }
        }
    }

    private fun getTagAndLineNumber(tag: String): Array<String> {
        try {
            for (st in Throwable().stackTrace) {
                //过滤掉日志类本身的方法和调用
                if (mClassname == st.className || mMethods!!.contains(st.methodName)) {
                    continue
                } else {
                    val b = st.className.lastIndexOf(".") + 1
                    val defaultTag = if (tag.isBlank()) {
                        st.className.substring(b)
                    } else {
                        tag
                    }
                    val message =
                        "method:${st.methodName}() line:${st.lineNumber} thread:${Thread.currentThread()} --> "
                    return arrayOf(defaultTag, message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayOf()
    }

    private fun parseBody(config: MLogConfig, contents: Any, prefixInfo: String): String {
        if (config.injectJsonParser() != null) {
            return prefixInfo + config.injectJsonParser()!!.toJson(contents)
        }
        //集合类型只能默认只能打印一层，嵌套太深还是自己注入一个json解析器好了
        if (contents is Collection<*>) {
            val sb = StringBuilder()
            sb.append("$prefixInfo[")
            contents.forEach {
                sb.append(it).append(",")
            }
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            sb.append("]")
            return sb.toString()
        }
        return prefixInfo + contents.toString()
    }
}