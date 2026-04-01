package com.kracubo.controlPanel.logger

import com.intellij.openapi.application.ApplicationManager
import com.kracubo.events.logwindow.AppLogTopics
import com.kracubo.events.logwindow.LogEntry
import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

object Logger {
    private const val MAX_BUFFER_SIZE = 10

    private val logBuffer = mutableListOf<String>()

    private val timestampFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

    private val logDir = Paths.get(
        System.getProperty("user.home"),
        ".remotePyCharm",
        "logs"
    ).toFile()

    private val fileLoggerThread = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "FileLogger").apply { isDaemon = true }
    }

    private var logFile: File? = null
    private const val MAX_LOG_FILE_SIZE = 10 * 1024 * 1024

    fun init() {
        setupLogDirectory()
        logFile = getCurrentLogFile()
    }

    private fun setupLogDirectory() {
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
    }

    private fun getCurrentLogFile(): File {
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return File(logDir, "remote-pycharm-$date.log")
    }

    private fun logToFile(logMessage: String) {
        fileLoggerThread.submit {
            try {
                synchronized(this) {
                    val file = logFile ?: return@submit

                    if (file.exists() && file.length() >= MAX_LOG_FILE_SIZE) {
                        rotateLogFile()
                    }

                    RandomAccessFile(file, "rw").use { raf ->
                        raf.seek(raf.length())
                        raf.write(logMessage.toByteArray())
                    }
                }
            } catch (e: Exception) { System.err.println("Failed to write log: ${e.message}") }
        }
    }

    private fun rotateLogFile() {
        val oldFile = logFile ?: return

        var counter = 1
        var archivedFile: File

        do {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            archivedFile = File(logDir, "remote-pycharm-${timestamp}-${counter}.log")
            counter++
        } while (archivedFile.exists())

        oldFile.renameTo(archivedFile)

        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        logFile = File(logDir, "remote-pycharm-${date}.log")
    }

    fun log(message: String, senderType: SenderType, messageType: MessageType = MessageType.INFO) {
        val timestamp = LocalDateTime.now().format(timestampFormat)
        val logMessage = "[$timestamp] $senderType: $message ($messageType)"

        synchronized(logBuffer) {
            logBuffer.add(logMessage)
            if (logBuffer.size > MAX_BUFFER_SIZE) {
                logBuffer.removeAt(0)
            }
        }

        ApplicationManager.getApplication().messageBus
            .syncPublisher(AppLogTopics.LOG_EVENT)
            .onLogAdded(LogEntry(logMessage))

        logToFile("$logMessage\n")
    }

    fun getLogHistory(): List<String> = synchronized(logBuffer) { logBuffer.toList() }

    fun clearLogHistory() { synchronized(logBuffer) { logBuffer.clear() } }

}