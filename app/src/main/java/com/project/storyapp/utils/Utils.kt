package com.project.storyapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.project.storyapp.BuildConfig
import com.project.storyapp.utils.BitmapUtils.getRotatedBitmap
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private const val MAXIMAL_SIZE = 1000000 // 1MB in bytes
    private const val COMPRESS_QUALITY_STEP = 5
    private const val INITIAL_COMPRESS_QUALITY = 100
    private const val BUFFER_SIZE = 1024
    private const val IMAGE_JPEG_SUFFIX = ".jpg"
    private const val IMAGE_MIME_TYPE = "image/jpeg"
    private const val PICTURES_DIR = "Pictures/MyCamera"

    private val timestamp: String
        get() = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun getImageUri(context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getImageUriForQ(context)
        } else {
            getImageUriForPreQ(context)
        }
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timestamp, IMAGE_JPEG_SUFFIX, filesDir)
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            FileOutputStream(myFile).use { outputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
        return myFile
    }

    fun File.reduceFileImage(): File {
        val bitmap = BitmapFactory.decodeFile(path).getRotatedBitmap(this)
        var compressQuality = INITIAL_COMPRESS_QUALITY
        var streamLength: Int

        do {
            ByteArrayOutputStream().use { bmpStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                streamLength = bmpStream.toByteArray().size
                compressQuality -= COMPRESS_QUALITY_STEP
            }
        } while (streamLength > MAXIMAL_SIZE && compressQuality > 0)

        FileOutputStream(this).use { fileOutputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, fileOutputStream)
        }

        return this
    }

    private fun getImageUriForQ(context: Context): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timestamp$IMAGE_JPEG_SUFFIX")
            put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_MIME_TYPE)
            put(MediaStore.MediaColumns.RELATIVE_PATH, PICTURES_DIR)
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "$PICTURES_DIR/$timestamp$IMAGE_JPEG_SUFFIX")
        if (imageFile.parentFile?.exists() == false) {
            imageFile.parentFile?.mkdirs()
        }
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            imageFile
        )
    }
}

object BitmapUtils {

    fun Bitmap.getRotatedBitmap(file: File): Bitmap {
        val exif = ExifInterface(file)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(270f)
            else -> this
        }
    }

    private fun Bitmap.rotateImage(angle: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(angle)
        }
        return Bitmap.createBitmap(
            this,
            0,
            0,
            width,
            height,
            matrix,
            true
        )
    }
}