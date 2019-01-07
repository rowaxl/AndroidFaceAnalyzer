package com.example.rowaxl.faceapisample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.microsoft.projectoxford.face.contract.Face
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var textAge: TextView
    private lateinit var textGender: TextView
    private lateinit var textSmile: TextView
    private lateinit var textAnger: TextView
    private lateinit var textFear: TextView
    private lateinit var textContempt: TextView
    private lateinit var textDisgust: TextView
    private lateinit var textHappiness: TextView
    private lateinit var textNeutral: TextView
    private lateinit var textSadness: TextView
    private lateinit var textSurprise: TextView

    private val cameraFragment = Camera2BasicFragment.newInstance()

    private val faceApiClient = FaceApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.picture).setOnClickListener { EventBus.getDefault().post(Events.ButtonCaptureClicked()) }

        textAge = findViewById(R.id.text_age)
        textGender = findViewById(R.id.text_gender)
        textSmile = findViewById(R.id.text_smile)
        textAnger = findViewById(R.id.text_anger)
        textFear = findViewById(R.id.text_fear)
        textContempt = findViewById(R.id.text_contempt)
        textDisgust = findViewById(R.id.text_disgust)
        textNeutral = findViewById(R.id.text_neutral)
        textHappiness = findViewById(R.id.text_happiness)
        textSadness = findViewById(R.id.text_sadness)
        textSurprise = findViewById(R.id.text_surprise)

        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, cameraFragment)
                .commit()

        faceApiClient.init(getString(R.string.face_api_endpoint), getString(R.string.face_api_key))

        // for event delegation.(Suggest Butterknife pattern!!)
        EventBus.getDefault().register(this@MainActivity)
    }



    private fun callFaceApi(captureFile: File) {
        if(faceApiClient.isConnected) {
            val results = async { faceApiClient.startDetect(BitmapFactory.decodeStream(FileInputStream(captureFile))) }

            launch {
                updateFaceAnalyzedResult(results.await()!!)
            }

        }
    }

    private fun updateFaceAnalyzedResult(faceAnalyzeResults:Array<Face>) {

        // Use only a face which recognized
        if(!faceAnalyzeResults.isEmpty()) {
            val face = faceAnalyzeResults[0]

            launch(UI) {
                // update texts
                textAge.text = String.format(getString(R.string.text_age), face.faceAttributes.age)
                textGender.text = String.format(getString(R.string.text_gender), face.faceAttributes.gender)
                textSmile.text = String.format(getString(R.string.text_smile), face.faceAttributes.smile)
                textAnger.text = String.format(getString(R.string.text_anger), face.faceAttributes.emotion.anger)
                textFear.text = String.format(getString(R.string.text_fear), face.faceAttributes.emotion.fear)
                textContempt.text = String.format(getString(R.string.text_contempt), face.faceAttributes.emotion.contempt)
                textDisgust.text = String.format(getString(R.string.text_disgust), face.faceAttributes.emotion.disgust)
                textNeutral.text = String.format(getString(R.string.text_neutral), face.faceAttributes.emotion.neutral)
                textHappiness.text = String.format(getString(R.string.text_happiness), face.faceAttributes.emotion.happiness)
                textSadness.text = String.format(getString(R.string.text_sadness), face.faceAttributes.emotion.sadness)
                textSurprise.text = String.format(getString(R.string.text_surprise), face.faceAttributes.emotion.surprise)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@MainActivity)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun successCapture(event: Events.SuccessCapture) {
        if (cameraFragment.file.canRead()) {
            callFaceApi(cameraFragment.file)
        }
    }
}
