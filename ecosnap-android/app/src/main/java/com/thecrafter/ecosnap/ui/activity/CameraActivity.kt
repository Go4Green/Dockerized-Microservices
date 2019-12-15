package com.thecrafter.ecosnap.ui.activity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.thecrafter.ecosnap.ui.CameraPreview
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_camera.*
import android.util.Base64
import com.thecrafter.ecosnap.MyApplication
import com.thecrafter.ecosnap.R


fun Context.CameraPreviewIntent(): Intent {
    return Intent(this, CameraActivity::class.java)
}

class CameraActivity : AppCompatActivity() {

    private val mPicture = Camera.PictureCallback { data: ByteArray, _ ->
        val base64 = String(Base64.encode(data, Base64.DEFAULT), Charsets.UTF_8)
        MyApplication.imageBase64 = base64
        startActivity(SubmitActivityIntent(), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Create an instance of Camera
        val camera = getCameraInstance()
        camera?.setDisplayOrientation(90)

        val cameraPreview = camera?.let {
            // Create our Preview view
            CameraPreview(this, it)
        }

        // Set the Preview view as the content of our activity.
        cameraPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }

        btn_capture.setOnClickListener {
            camera?.takePicture(null, null, mPicture)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_camera, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                startActivity(DashboardActivityIntent(), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /** A safe way to get an instance of the Camera object. */
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }
}
