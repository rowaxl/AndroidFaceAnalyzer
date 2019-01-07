package com.example.rowaxl.faceapisample

import android.graphics.Bitmap
import android.util.Log
import com.microsoft.projectoxford.face.FaceServiceClient.FaceAttributeType
import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.Face
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class FaceApiClient {
    private val TAG: String = FaceApiClient::class.java.simpleName
    private lateinit var client: FaceServiceRestClient
    var isConnected = false

    fun init(endpoint:String, apiKey:String) {
        client = FaceServiceRestClient(endpoint, apiKey)
        isConnected = true
    }

    // Detect faces by uploading a face image.
    // Frame faces after detection.
    fun startDetect(imageBitmap: Bitmap):Array<Face>? {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        try {
            Log.d(TAG,"Detecting...")

            val requiredFaceAttributes = arrayOf(
                    FaceAttributeType.Age,
                    FaceAttributeType.Gender,
                    FaceAttributeType.Smile,
                    FaceAttributeType.Emotion
            )

            val result = client.detect(
                    inputStream,
                    true, // returnFaceId
                    false,
                    requiredFaceAttributes
            )

            if (result == null) {
                Log.d(TAG,"Detection Finished. Nothing detected")
                return null
            }

            Log.d(TAG,String.format("Detection Finished. %d face(s) detected", result.size))
            return result

        } catch (e: Exception) {
            Log.e(TAG,String.format("Detection failed: %s", e.message))
            return null
        }
    }
}