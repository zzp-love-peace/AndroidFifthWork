package com.zzp.fifthwork

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import java.io.File

class Camera(private val activity: Activity) {

    private val takePhoto = 1
    private val fromAlbum = 2
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    private lateinit var manager: NotificationManager

    fun initManager() {
        manager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("camera", "Camera",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    fun takePhoto() {
        outputImage = File(activity.externalCacheDir, "output_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(activity, "com.zzp.fifthwork." +
                    "fileprovider", outputImage)
        }
        else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activity.startActivityForResult(intent, takePhoto)
    }

    fun fromAlbum() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(intent, fromAlbum)
    }

    fun handle(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(activity.contentResolver.
                    openInputStream(imageUri))
                    val notification = NotificationCompat.Builder(activity, "camera")
                        .setContentTitle("这是刚刚拍的")
                        .setContentText("你看拍的怎么样")
                        .setSmallIcon(R.drawable.ic_android)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(activity.resources,
                                R.drawable.ic_large))
                        .setStyle(
                            NotificationCompat.BigPictureStyle().
                            bigPicture(rotateIfRequired(bitmap)))
                        .setAutoCancel(true)
                        .build()
                    manager.notify(1, notification)
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        val bitmap = getBitmapFromUri(uri)
                        val notification = NotificationCompat.Builder(activity, "camera")
                            .setContentTitle("这是相册里的")
                            .setContentText("没有相机拍的好看")
                            .setSmallIcon(R.drawable.ic_android)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(activity.resources,
                                    R.drawable.ic_large))
                            .setStyle(
                                NotificationCompat.BigPictureStyle().
                                bigPicture(bitmap))
                            .setAutoCancel(true)
                            .build()
                        manager.notify(2, notification)
                    }
                }
            }
        }
    }


    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    private fun getBitmapFromUri(uri: Uri) = activity.contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }
}