package ph.appsolutely.test

import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.util.Log
import android.view.SurfaceHolder
import java.io.File
import java.io.FileOutputStream

/**
 * @author Melby Baldove
 * melby@appsolutely.ph
 */
class OfflineVideoRecorder : SurfaceHolder.Callback {
    private var isConfigured = false
    private var isRecording = false
    private var shouldStartRecording = false
    private var isSurfaceCreated = false
    private var outputFile: File? = null
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var camera: Camera
    private lateinit var recorder: MediaRecorder

    fun startRecording(file: File) {
        if (isSurfaceCreated) {
            configure(surfaceHolder, file)
            recorder.start()
            shouldStartRecording = false
            isRecording = true
        } else {
            shouldStartRecording = true
            outputFile = file
        }
    }

    fun stopRecording() {
        if (isRecording) {
            recorder.stop()
            cleanup()
            isRecording = false
        }
    }

    private fun configure(holder: SurfaceHolder, file: File) {
        if (outputFile == null) throw IllegalStateException("Output file must be set")
        camera = Camera.open()
        camera.run {
            setPreviewDisplay(holder)
            startPreview()
            unlock()
        }

        recorder = MediaRecorder().apply {
            setCamera(camera)
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF))
            setOutputFile(FileOutputStream(file).fd)
            setPreviewDisplay(holder.surface)
            prepare()
        }
        isConfigured = true
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        Log.d("recorder", "surfaceChanged")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        stopRecording()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        isSurfaceCreated = true
        surfaceHolder = p0
        if (shouldStartRecording) {
            startRecording(outputFile!!)
        }
    }

    private fun cleanup() {
        recorder.run {
            release()
            reset()
            isConfigured = false
        }
        camera.run {
            lock()
            release()
        }
    }
}