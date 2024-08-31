package com.hfzq.xfn.framework.log.printer

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfzq.framework.log.databinding.SodalogItemBinding
import com.hfzq.xfn.framework.commlib.dp2px
import com.hfzq.xfn.framework.commlib.getColorByString
import com.hfzq.xfn.framework.log.MLogConfig
import com.hfzq.xfn.framework.log.bean.MLogBean

class MViewPrinter(context: Context) : MLogPrinter {
    companion object {
        const val TAG_LOG_VIEW = "TAG_LOG_VIEW"
    }

    private var recyclerView: RecyclerView? = null
    private var adapter: LogAdapter? = null

    private var logView: View? = null
    private var isOpen: Boolean = false
    private var mContext: Context? = null

    private val mWindowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val mParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }

    init {
        mContext = context
        recyclerView = RecyclerView(mContext!!)
        adapter = LogAdapter()
        val layoutManager = LinearLayoutManager(recyclerView!!.context)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = adapter
    }

    override fun print(config: MLogConfig, level: Int, tag: String, printString: String) {
        adapter?.addItem(MLogBean(System.currentTimeMillis(), level, tag, printString))
        recyclerView?.smoothScrollToPosition(adapter?.itemCount ?: 1 - 1)
    }

    fun showLogView() {
        val params = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.format = PixelFormat.RGBA_8888
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = 300.dp2px
        val logView: View = getLogView()
        logView.tag = TAG_LOG_VIEW
        mWindowManager.addView(logView, params)
        isOpen = true
    }

    private fun getLogView(): View {
        if (logView != null) {
            return logView!!
        }
        logView = FrameLayout(recyclerView?.context!!)
        logView?.setBackgroundColor(Color.parseColor("#77000000"))
        (logView as ViewGroup).addView(recyclerView)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.END
        val closeView = TextView(mContext!!)
        closeView.setOnClickListener { closeLogView() }
        closeView.text = "close"
        (logView as ViewGroup).addView(closeView, params)
        return logView!!
    }

    private fun closeLogView() {
        isOpen = false
        mWindowManager.removeView(getLogView())
    }

    inner class LogAdapter : RecyclerView.Adapter<LogViewHolder>() {
        private val logs: MutableList<MLogBean> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
            val inflater = LayoutInflater.from(recyclerView?.context)
            val binding = SodalogItemBinding.inflate(inflater)
            return LogViewHolder(binding)
        }

        override fun getItemCount(): Int = logs.size

        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            logs[position].apply {
                holder.binding.mTagTv.setTextColor(getHighlightColor(level))
                holder.binding.mMessageTv.setTextColor(getHighlightColor(level))
                holder.binding.mTagTv.text = getFlattened()
                holder.binding.mMessageTv.text = log
            }
        }

        fun addItem(logItem: MLogBean) {
            logs.add(logItem)
            notifyItemInserted(logs.size - 1)
        }

        /**
         * 跟进log级别获取不同的高了颜色
         *
         * @param logLevel log 级别
         * @return 高亮的颜色
         */
        private fun getHighlightColor(logLevel: Int): Int {
            return when (logLevel) {
                Log.VERBOSE -> -0x444445
                Log.DEBUG -> -0x1
                Log.INFO -> getColorByString("#00ff00")
                Log.WARN -> -0x444ad7
                Log.ERROR -> -0x9498
                else -> -0x100
            }
        }
    }

    class LogViewHolder(val binding: SodalogItemBinding) : RecyclerView.ViewHolder(binding.root)
}