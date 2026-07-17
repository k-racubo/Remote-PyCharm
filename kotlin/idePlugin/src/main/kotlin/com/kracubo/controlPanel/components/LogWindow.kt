package com.kracubo.controlPanel.components

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import com.kracubo.controlPanel.logger.Logger
import com.kracubo.events.logwindow.AppLogListener
import com.kracubo.events.logwindow.AppLogTopics
import com.kracubo.events.logwindow.ClearLogWindowListener
import com.kracubo.events.logwindow.ClearLogWindowTopics
import com.kracubo.events.logwindow.LogEntry
import javax.swing.SwingUtilities

class LogWindow : JBScrollPane(), Disposable {

    val logArea: JBTextArea

    init {
        maximumSize = JBUI.size(Int.MAX_VALUE, 500)

        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER

        logArea = JBTextArea().apply {
            isEditable = false
            lineWrap = true
        }

        setViewportView(logArea)

        loadHistory()

        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(AppLogTopics.LOG_EVENT,
                object : AppLogListener {
                    override fun onLogAdded(entry: LogEntry) {
                        SwingUtilities.invokeLater {
                            logArea.append("${entry.message}\n")
                            logArea.caretPosition = logArea.document.length
                        }
                    }
                })

        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(ClearLogWindowTopics.CLEAR_LOG,
                object : ClearLogWindowListener {
                    override fun onLogClear() {logArea.text = "" }
                })
    }

    private fun loadHistory() {
        val history = Logger.getLogHistory()
        if (history.isNotEmpty()) {
            SwingUtilities.invokeLater {
                history.forEach { line ->
                    logArea.append("$line\n")
                }
                logArea.caretPosition = logArea.document.length
            }
        }
    }

    override fun dispose() {}
}