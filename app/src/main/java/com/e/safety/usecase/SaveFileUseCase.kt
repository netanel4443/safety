package com.e.safety.usecase

import android.R.attr.name
import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.e.safety.utils.printErrorIfDbg
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


class SaveFileUseCase @Inject constructor(private val application: Application) {

    private val TAG = this.javaClass.name

    fun saveImageToPicturesDir(directoryName: String, uri: Uri): Single<Uri?> {
        return Single.fromCallable {

            val file =
                File(
                    Environment.getExternalStorageDirectory(),
                    Environment.DIRECTORY_PICTURES + "/$directoryName/"
                )

            if (!file.exists() && !file.mkdirs()) {
                printErrorIfDbg(TAG, "Directory not created")
                return@fromCallable null
            }
            saveBitmap(uri, directoryName)
        }
    }

    private fun saveToExternalStorage(bitmap: Bitmap, IMAGES_FOLDER_NAME: String): Uri? {
        val saved: Boolean
        val fos: OutputStream?
        val imageUri: Uri?

        fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = application.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$IMAGES_FOLDER_NAME")
            imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(imageUri!!)
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString() + File.separator + IMAGES_FOLDER_NAME
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, name.toString() + ".png")
            imageUri = Uri.parse(image.path) // todo check if it should be path or absolutepath
            FileOutputStream(image)
        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos!!.flush()
        fos.close()
        return imageUri
    }

    fun saveBitmap(imageUri: Uri, directoryName: String):Uri? {
        var bitmap: Bitmap? = null
        val contentResolver: ContentResolver = application.contentResolver
        var uri:Uri? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            printErrorIfDbg(e)
        }
        try {
            uri = saveToExternalStorage(bitmap!!, directoryName)
        } catch (e: Exception) {
            println(e)
        }
        return uri
    }


}