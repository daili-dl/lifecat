package com.ten.lifecat.phone.view.activity

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.ten.lifecat.phone.R
import me.kareluo.imaging.IMGEditActivity
import java.io.File


/**
 * TODO 用户设置界面
 */
class SettingsActivity : BaseActivity() {
    val REQ_SELECT_PHOTO = 0xf0a
    private var avatarBitMap: Bitmap? = null
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var btEdit: Button
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 导航按钮
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu)
        }
        button = findViewById(R.id.button)
        button.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                seleteImg()
            }
        })
    }
    private fun seleteImg() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQ_SELECT_PHOTO)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_SELECT_PHOTO -> {
                Log.d("daili", "onactivityresult")
                Log.d("daili", "data${data.toString()}" +" " + "data.data${data?.data}")
                // 选取照片。
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        avatarBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                        //此处获得了Bitmap图片，可以用作设置头像等等。
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    System.out.println("daili")
                    val intent = Intent(this, IMGEditActivity::class.java)
                    try {
//                        val uri_path: String = this.getFilePathByUri(getFilePathByUrithis, data.data)!!
//                        val uri: Uri = Uri.fromFile(File(uri_path))
                        val uri: Uri = data.data
                        System.out.println(uri.toString())
                        intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, uri)
                        //intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH,);
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {
            }
        }
    }
    fun getFilePathByUri(context: Context, uri: Uri): String? {
        var path: String? = null
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            path = uri.path
            return path
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val cursor: Cursor = context.getContentResolver().query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex)
                    }
                }
                cursor.close()
            }
            return path
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        return path
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id))
                    path = getDataColumn(context, contentUri, null, null)
                    return path
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    path = getDataColumn(context, contentUri, selection, selectionArgs)
                    return path
                }
            }
        }
        return null
    }
    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}
