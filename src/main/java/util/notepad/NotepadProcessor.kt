package util.notepad

import util.Platform
import java.awt.AWTException
import java.io.IOException

class NotepadProcessor {
  private val notepad: Notepad = when (val p = Platform.detectPlatform()) {
    Platform.WINDOWS -> WinNotepad()
    Platform.LINUX -> LinuxNotepad()
    Platform.MAC -> MacNotepad()
    else -> {
      error("Platform not supported")
    }
  }

  @Throws(IOException::class, InterruptedException::class, AWTException::class)
  fun openNotepad() = notepad.openNotepad()

  fun writeText(text: String) = notepad.writeText(text)

  fun deleteText() = notepad.deleteText()

  fun addNewLine() = notepad.addNewLine()

  fun saveFileAs(name: String) = notepad.saveFileAs(name)

  fun openNewFile() = notepad.openNewFile()

  @Throws(InterruptedException::class)
  fun closeNotepad() = notepad.closeNotepad()
}