package com.zzp.fifthwork

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class MyProvider : ContentProvider() {

    private val contactDir = 0
    private val authority = "com.zzp.myprovider.provider"
    private var dbHelper: MyDatabaseHelper? = null

    private val uriMatcher by lazy {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority, "contact", contactDir)
        matcher
    }

    override fun onCreate() = context?.let {
        dbHelper = MyDatabaseHelper(it, "Contacts.db", 1)
        true
    } ?: false

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
            selectionArgs: Array<String>?, sortOrder: String?) = dbHelper?.let {
        val db = it.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            contactDir -> db.query("Contact", projection, selection, selectionArgs,
                null, null, sortOrder)
            else -> null
        }
        cursor
    }

    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        contactDir -> "vnd.android.cursor.dir/vnd.com.zzp.myprovider.provider.contact"
        else -> null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?) = 0

}