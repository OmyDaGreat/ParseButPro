package util.extension

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File

// URL of the icon image
const val icon =
  "https://www.dropbox.com/scl/fi/x4zl6mzfpm2g8j9d461z3/icon.png?rlkey=l63vlj7hywe1p97p9mcgdwc2k&st=83ot1zd3&dl=1"

// URL of the Aries.pv file
const val pv =
  "https://www.dropbox.com/scl/fi/f4370qnb7d05e90odjebo/Aries.pv?rlkey=a1ceeoipvdah4nosr00w3blv6&st=uoc4kwn3&dl=1"

// URL of the db.properties file
const val props =
  "https://www.dropbox.com/scl/fi/uhod5m8oqvhoq89ank0tf/db.properties?rlkey=iwvuaq617j5099zgjqgzm9bjm&st=ge9s493z&dl=1"

// URL of the openNotepad.scpt file
const val openNotepad =
  "https://www.dropbox.com/scl/fi/lppie7xcxbzloo860t7qg/openNotepad.scpt?rlkey=xv413n4dclvsjj6hdj580u0s6&st=1uk85psv&dl=1"

// URL of leopard library for windows
const val leolibwin =
  "https://www.dropbox.com/scl/fi/mztcz5q5p8qiv91vfchi8/libpv_leopard_jni.dll?rlkey=9aumenejyt39iewm2d8kwze6b&st=qaw0fewr&dl=1"

// URL of leopard library for mac
const val leolibmac =
  "https://www.dropbox.com/scl/fi/vq8h75rpfbdxac89cer1k/libpv_leopard_jni.dylib?rlkey=zsidzj6mxbg4s5vnsbw2irx14&st=xjd6ft5p&dl=1"

// URL of leopard library for linux
const val leoliblin =
  "https://www.dropbox.com/scl/fi/v12jnksld7bhepd40tp0s/libpv_leopard_jni.so?rlkey=w6votdishp3bre7t4d9dmhstf&st=oz8rmcti&dl=1"

// URL of beep
const val beep =
  "https://www.dropbox.com/scl/fi/2le9oihdhpg417iz7ovmx/beep.mp3?rlkey=bo0usio4gdadt8vdn1i4yxtrt&st=t1p6i258&dl=1"

/**
 * Downloads a file from the specified URL to the given destination path.
 * If the file already exists at the destination, it will not be downloaded again.
 *
 * @param fileURL The URL of the file to download.
 * @param destinationPath The path where the downloaded file should be saved.
 * @return The downloaded file.
 * @throws Exception if the file download fails.
 */
suspend fun downloadFile(fileURL: String, destinationPath: String): File {
  return File(destinationPath).apply {
    if (!exists()) {
      println("Downloading file ${destinationPath.split(File.separator).last()}.")
      HttpClient(CIO){
        install(HttpTimeout) {
          requestTimeoutMillis = 120000 // 2 minutes
        }
      }.use {
        val fileBytes: ByteArray = it.get(fileURL).readBytes()
        writeBytes(fileBytes)
        println("File downloaded successfully.")
      }
    }
  }
}