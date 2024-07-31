package parse.audio

import ai.picovoice.leopard.*
import kotlinx.coroutines.*
import lombok.experimental.ExtensionMethod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import util.*
import util.Keys.get
import util.ResourcePath.getResourcePath
import util.audio.*
import util.extension.*
import util.extension.RobotUtils.special
import util.extension.ScrollOption.Companion.showScrollableMessageDialog
import util.notepad.NotepadProcessor
import java.awt.*
import java.awt.event.InputEvent
import java.io.IOException
import java.net.URI
import java.util.*
import javax.swing.JOptionPane

@ExtensionMethod(RobotUtils::class)
class LiveMic {
  companion object {
    private val log: Logger = LogManager.getLogger()
    private val n = NotepadProcessor()

    @JvmField
    var maxWords = 40

    private fun process(input: String) {
      when {
        input.trueContains("ask gemini") -> {
          ask("Answer the request while staying concise but without contractions: $input")
        }

        input.trueContains("left press") -> {
          Robot().mousePress(InputEvent.BUTTON1_DOWN_MASK)
        }

        input.trueContains("left release") -> {
          Robot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        }

        input.trueContains("right press") -> {
          Robot().mousePress(InputEvent.BUTTON3_DOWN_MASK)
        }

        input.trueContains("right release") -> {
          Robot().mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
        }

        input.trueContains("middle click") -> {
          Robot().apply {
            mousePress(InputEvent.BUTTON2_DOWN_MASK)
            mouseRelease(InputEvent.BUTTON2_DOWN_MASK)
          }
        }

        input.trueContains("left click") -> {
          Robot().leftClick()
        }

        input.trueContains("right click") -> {
          Robot().rightClick()
        }

        input.trueContains("command shift") -> {
          Robot().command{r1 -> r1.shift{r2 -> r2.type(input.replace("command shift", "", ignoreCase = true).trim {it <= ' '})}}
        }

        input.trueContains("control shift") -> {
          Robot().control{r1 -> r1.shift{r2 -> r2.type(input.replace("control shift", "", ignoreCase = true).trim {it <= ' '})}}
        }

        input.trueContains("shift") -> {
          Robot().shift{r -> r.type(input.replace("shift", "", ignoreCase = true).trim {it <= ' '})}
        }

        input.trueContainsAny("control") -> {
          Robot().control{r -> r.type(input.replace("control", "", ignoreCase = true).replaceSpecial().trim {it <= ' '})}
        }

        input.trueContainsAny("command") -> {
          Robot().command{r -> r.type(input.replace("command", "", ignoreCase = true).trim {it <= ' '})}
        }

        input.trueContainsAny("write special", "right special") -> {
          input.split(" ").forEach {c ->
            if (special.containsKeyFirst(c)) {
              special.getFromFirst(c).forEach {key ->
                Robot().keyPress(key)
              }
              special.getFromFirst(c).reversed().forEach {key ->
                Robot().keyRelease(key)
              }
            }
          }
        }

        input.trueContainsAny("write", "right") -> {
          Robot().type(input.replace("write", "", ignoreCase = true).replace("right", "", ignoreCase = true).trim {it <= ' '})
        }

        input.trueContains("search") -> {
          open("https://www.google.com/search?q=" + input.removeForIfFirst().replace("search", "", ignoreCase = true).trim {it <= ' '}
            .replace(" ", "+"))
        }

        input.trueContains("mouse") -> {
          Robot().mouseMoveString(input.replace("mouse", "", ignoreCase = true).trim {it <= ' '})
        }

        input.trueContainsAny("scroll", "scrolled") -> {
          Robot().scroll(input.replace("scroll", "", ignoreCase = true).replace("scrolled", "", ignoreCase = true).trim { it <= ' ' })
        }

        input.trueContainsAny("open notepad", "opened notepad") -> {
          n.openNotepad()
        }

        input.trueContainsAny("close notepad", "closed notepad", "clothes notepad") -> {
          n.closeNotepad()
        }

        input.trueContainsAny("open new", "open knew") -> {
          n.openNewFile()
        }

        input.trueContains("delete everything") -> {
          n.deleteText()
        }

        input.trueContains("save file") -> {
          n.saveFileAs(
            input.replace("save file", "", ignoreCase = true).trim {it <= ' '}.removeForIfFirst().replace(" ", "_")
          )
        }

        input.trueContains("enter") -> {
          n.addNewLine()
        }

        input.trueContains("arrow") -> {
          Robot().arrow(input.replace("arrow", "", ignoreCase = true).trim {it <= ' '})
        }

        else -> {
          ask("Answer the request while staying concise but without contractions: $input")
        }
      }
    }

    private fun ask(input: String) {
      runBlocking {
        val gemini = generateContent(input).replace("*", "")
        log.info(gemini)
        launch {
          if (gemini.split(" ").size > maxWords) {
            NativeTTS.tts("The response is over $maxWords words.")
          } else {
            log.debug(gemini)
            NativeTTS.tts(gemini)
          }
        }
        Thread {
          showScrollableMessageDialog(null, gemini, "Gemini", JOptionPane.INFORMATION_MESSAGE)
        }.start()
      }
    }

    private fun String.removeForIfFirst(): String {
      return if (startsWith("for ")) {
        removePrefix("for ").trimStart()
      } else {
        this
      }
    }

    fun startRecognition() {
      val leopard = Leopard.Builder().setAccessKey(get("pico")).setModelPath(getResourcePath("Aries.pv")).build()
      log.debug("Leopard version: {}", leopard.version)
      log.info("Ready...")
      NativeTTS.tts("Aries is starting up...")
      var recorder: Recorder? = null

      try {
        processAudio({
          NativeTTS.tts("Yes?")
          log.info(">>> Wake word detected.")
          recorder = Recorder(-1)
          recorder!!.start()
          log.info(">>> Recording...")
        }, {
          log.info(">>> Silence detected.")
          recorder!!.end()
          recorder!!.join()
          val pcm = recorder!!.pcm
          recorder = null
          val transcript = leopard.process(pcm)
          log.info("{}\n", transcript.transcriptString)
          process(transcript.transcriptString)
        }) {
          recorder != null
        }
      } catch (e: Exception) {
        log.error("Error: {}", e.message)
        e.printStackTrace()
      } finally {
        leopard.delete()
        startRecognition()
      }
    }
  }
}

/**
 * Opens a web page in the system's default browser using a URL string.
 *
 * @param page The URL of the web page to open as a String.
 * @throws IOException If the default browser is not found, or it fails to be launched.
 */
@Throws(IOException::class)
fun open(page: String) {
  Desktop.getDesktop().browse(URI.create(page))
}