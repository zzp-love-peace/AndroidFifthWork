 package com.zzp.fifthwork.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import com.zzp.fifthwork.Camera
import com.zzp.fifthwork.MyDatabaseHelper
import com.zzp.fifthwork.R

 class MainActivity : AppCompatActivity() {

     private val camera = Camera(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        camera.initManager()
        val musicButton = findViewById<ImageButton>(R.id.music_button)
        val videoButton = findViewById<ImageButton>(R.id.video_button)
        val photoButton = findViewById<ImageButton>(R.id.photo_button)
        val albumButton = findViewById<ImageButton>(R.id.album_button)
        musicButton.setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }
        videoButton.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }
        photoButton.setOnClickListener {
            camera.takePhoto()
        }
        albumButton.setOnClickListener {
            camera.fromAlbum()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        }
        val dbHelper = MyDatabaseHelper(this, "Contacts.db", 1)
        dbHelper.writableDatabase
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         camera.handle(requestCode, resultCode, data)
     }

     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
         grantResults: IntArray) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         when (requestCode) {
             1 -> {
                 if (grantResults.isNotEmpty()
                     && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     readContacts()
                 }
                 else {
                     Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_LONG).show()
                 }
             }
          }
     }

     private fun readContacts() {
         contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)?.apply {
                while (moveToNext()) {
                    val displayName = getString(getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = getString(getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                    addToDatabase(displayName, number)
                    Log.d("MainActivity", displayName + "\n" + number)
                }
             close()
         }
     }

     private fun addToDatabase(name:String, number: String) {
         val dbHelper = MyDatabaseHelper(this, "Contacts.db", 1)
         val db = dbHelper.writableDatabase
         val values = contentValuesOf("name" to name, "number" to number)
         db.insert("Contact", null, values)
     }
 }