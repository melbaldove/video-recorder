package ph.appsolutely.test

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.SessionType
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        camera.start()
        camera.sessionType = SessionType.VIDEO
        camera.addCameraListener(object: CameraListener(){
            override fun onVideoTaken(video: File) {
                super.onVideoTaken(video)
                Log.d("videoEvent", video.length().toString())
            }
        })

        val file = File(this.filesDir, "test")
        camera.startCapturingVideo(file)

        Handler().postDelayed({
            camera.stopCapturingVideo()
            Log.d("videoEvent", "onStop")
        }, 2000)

    }

    override fun onPause() {
        super.onPause()
        camera.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.destroy()
    }
}
