package com.kracubo.controlPanel.components

import com.intellij.ide.actions.RevealFileAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.kracubo.controlPanel.logger.Logger
import com.kracubo.controlPanel.logger.MessageType
import com.kracubo.controlPanel.logger.SenderType
import com.kracubo.events.logwindow.ClearLogWindowTopics
import java.awt.Color
import java.awt.Cursor
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

class LogControlPanel : JPanel() {

    private val clearLogBtn: JButton
    private val clickableText: JBLabel

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        clearLogBtn = JButton("Clear logs").apply {
            toolTipText = "Clear only displayed logs (log files remain unchanged)"

            addActionListener {
                Logger.clearLogHistory() // clear buffer

                ApplicationManager.getApplication().messageBus
                    .syncPublisher(ClearLogWindowTopics.CLEAR_LOG)
                    .onLogClear()
            }
        }

        clickableText = JBLabel("Open logs directory").apply {
            val normalColor = JBColor(
                Color(0, 102, 204),
                Color(100, 180, 255)
            )

            val hoverColor = JBColor(
                Color(255, 102, 0),
                Color(255, 180, 100)
            )

            foreground = normalColor

            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        val userHome = System.getProperty("user.home")

                        try {
                            Logger.log("Open log dir action", SenderType.LOGGER)

                            val logDir = File("$userHome/.RemotePyCharm/logs")

                            if (!logDir.exists()) { logDir.mkdirs() }

                            val project = ProjectManager.getInstance().openProjects.firstOrNull()

                            if (project != null && !project.isDisposed) {
                                RevealFileAction.openDirectory(logDir)
                            } else {
                                Desktop.getDesktop().open(logDir) }
                        } catch (e: Exception) {
                            Logger.log("Can not open log dir", SenderType.LOGGER, MessageType.WARNING)

                            val logPath = "$userHome/.RemotePyCharm/logs"

                            JOptionPane.showMessageDialog(
                                this@apply,
                                "Log files are stored at:\n$logPath\n\nError: ${e.message}",
                                "Log Directory",
                                JOptionPane.WARNING_MESSAGE
                            )
                        }
                    }
                }

                override fun mouseEntered(e: MouseEvent) { foreground = hoverColor }

                override fun mouseExited(e: MouseEvent) { foreground = normalColor }
            })
        }

        add(clearLogBtn)
        add(Box.createHorizontalGlue())
        add(clickableText)
    }
}