package util.notepad

import org.apache.logging.log4j.LogManager
import util.extension.*
import java.awt.Robot
import java.awt.event.KeyEvent
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class WinNotepad: Notepad {
  private val robot = Robot()
  private val log = LogManager.getLogger()
  private var process: Process? = null

  companion object {
    val homeDirectory = System.getProperty("user.home") + File.separator + "ParseButPro"
  }

  init {
    Files.createDirectories(Paths.get(homeDirectory))
  }

  @Throws(IOException::class)
  override fun openNotepad() {
    process = ProcessBuilder("C:\\Program Files\\Notepad++\\notepad++.exe").start()
    robot.delay(1000)
  }

  override fun writeText(text: String) = robot.type(text)

  override fun deleteText() {
    robot.control(KeyEvent.VK_A)
    robot.type(KeyEvent.VK_BACK_SPACE)
  }

  override fun addNewLine() = robot.enter()

  override fun saveFileAs(name: String) {
    robot.control(KeyEvent.VK_S)
    robot.delay(1000)
    robot.type(name)
    repeat(7) {robot.type(KeyEvent.VK_TAB)}
    repeat(4) {
      robot.enter()
      robot.delay(500)
    }
  }

  override fun openNewFile() = robot.control(KeyEvent.VK_N)

  override fun closeFile() = robot.control(KeyEvent.VK_W)

  @Throws(InterruptedException::class)
  override fun closeNotepad() {
    robot.control(KeyEvent.VK_F4)
    process?.waitFor()?.also {log.debug("Exited Notepad++ with code: {}", it)}
  }
}