package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber

class BlurWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters)  {

    override fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("Blurring image", appContext )
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        //SLOW DOWN THE WORKER
        sleep()


        return try {
            if (TextUtils.isEmpty(resourceUri)) {
                Timber.e("Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            makeStatusNotification("Output is $outputUri", appContext)

            Result.success(outputData)
        }catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }

    }

}