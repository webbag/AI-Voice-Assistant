package com.devkro.aiva
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devkro.aiva.R
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var recordButton: Button
    private var isRecording = false
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    private var recordingFilePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.recordButton)
        mediaRecorder = MediaRecorder()

        // Sprawdź, czy mamy uprawnienie do nagrywania audio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            // Jeśli nie mamy uprawnienia, poproś o nie
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
        }

        recordButton.setOnClickListener {
            if (isRecording) {
                // Zatrzymaj nagrywanie
                stopRecording()
                recordButton.text = "Record"
                isRecording = false
            } else {
                // Sprawdź ponownie uprawnienia i rozpocznij nagrywanie
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                    recordButton.text = "Stop"
                    isRecording = true
                } else {
                    // Jeśli nadal nie mamy uprawnienia, poproś o nie ponownie
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
                }
            }
        }
    }

    private fun getOutputFile(): File {
        val outputDir = getExternalFilesDir(null) // Zwraca katalog plików zewnętrznych aplikacji
        if (outputDir?.exists() == false) { // Bezpieczne wywołanie z ?. i porównanie z false
            outputDir.mkdirs() // Tworzy katalog, jeśli nie istnieje
        }
        return File(outputDir, "recording.3gp")
    }

    private fun startRecording() {
        val outputFile = getOutputFile()
        recordingFilePath = outputFile.absolutePath // Zapisz ścieżkę do pliku
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordingFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private fun stopRecording() {
        mediaRecorder.apply {
            try {
                stop()
                reset()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        playRecording()
    }
    private fun playRecording() {
        val mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(recordingFilePath)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, you can start recording
                } else {
                    // Permission denied, disable the functionality that depends on this permission.
                }
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            stopRecording()
        }
        mediaRecorder.release()
    }
}
