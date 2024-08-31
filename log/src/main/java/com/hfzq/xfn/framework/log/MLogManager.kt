package com.hfzq.xfn.framework.log

import com.hfzq.xfn.framework.log.printer.MConsolePrinter
import com.hfzq.xfn.framework.log.printer.MFilePrinter
import com.hfzq.xfn.framework.log.printer.MLogPrinter
import com.hfzq.xfn.framework.log.printer.MViewPrinter

object MLogManager {
    private var mConfig: MLogConfig? = null
    private var mPrinters = mutableSetOf<MLogPrinter>()

    //支持init自己定制config，也可以使用默认的
    fun init(config: MLogConfig, vararg printers: MLogPrinter) {
        mConfig = config
        mPrinters.clear()
        mPrinters.add(MConsolePrinter())
        printers.forEach {
            //因为会强制加一个consoleprinter，所以外面的就不用了，但会导致外部有点懵逼，可以优化下
            if (it !is MConsolePrinter) {
                mPrinters.add(it)
            }
        }
    }

    fun getConfig(): MLogConfig {
        if (mConfig == null) {
            mConfig = getDefaultConfig()
        }
        return mConfig!!
    }

    fun getPrinters(): List<MLogPrinter> {
        return mPrinters.toList()
    }

    fun addPrinter(printer: MLogPrinter) {
        checkPrinterLegal(printer)
        mPrinters.add(printer)
    }

    fun removePrinter(printer: MLogPrinter) {
        mPrinters.remove(printer)
    }

    private fun getDefaultConfig(): MLogConfig {
        val config = MLogConfig()
        addPrinter(MConsolePrinter())
        return config
    }

    //这里需要想办法保证mPrinters容器只允许每个MLogPrinter子类类型放一个元素，新的元素进来就要进行判断，把老的删除掉
    private fun checkPrinterLegal(printer: MLogPrinter) {
        when (printer) {
            is MConsolePrinter -> {
                mPrinters.find { it is MConsolePrinter }?.let {
                    removePrinter(it)
                }
            }

            is MViewPrinter -> {
                mPrinters.find { it is MViewPrinter }?.let {
                    removePrinter(it)
                }
            }

            is MFilePrinter -> {
                mPrinters.find { it is MFilePrinter }?.let {
                    removePrinter(it)
                }
            }
        }
    }

}