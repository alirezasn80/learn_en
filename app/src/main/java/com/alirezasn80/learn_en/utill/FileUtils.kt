package com.alirezasn80.learn_en.utill

import android.webkit.MimeTypeMap
import android.database.DatabaseUtils
import android.os.Build
import android.provider.DocumentsContract
import android.os.Environment
import android.content.ContentUris
import android.provider.MediaStore
import android.graphics.Bitmap
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.DocumentsProvider
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class LocalStorageProvider : DocumentsProvider() {
    @Throws(FileNotFoundException::class)
    override fun queryRoots(projection: Array<String>): Cursor {
        // Create a cursor with either the requested fields, or the default
        // projection if "projection" is null.
        val result = MatrixCursor(projection ?: DEFAULT_ROOT_PROJECTION)
        // Add Home directory
        val homeDir = Environment.getExternalStorageDirectory()
        val row = result.newRow()
        // These columns are required
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, homeDir.absolutePath)
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, homeDir.absolutePath)
        row.add(DocumentsContract.Root.COLUMN_TITLE, "Internal Storage")
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_LOCAL_ONLY or DocumentsContract.Root.FLAG_SUPPORTS_CREATE)
        //row.add(Root.COLUMN_ICON, R.drawable.ic_provider);
        // These columns are optional
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, homeDir.freeSpace)
        // Root.COLUMN_MIME_TYPE is another optional column and useful if you
        // have multiple roots with different
        // types of mime types (roots that don't match the requested mime type
        // are automatically hidden)
        return result
    }

    @Throws(FileNotFoundException::class)
    override fun createDocument(
        parentDocumentId: String, mimeType: String,
        displayName: String
    ): String? {
        val newFile = File(parentDocumentId, displayName)
        try {
            newFile.createNewFile()
            return newFile.absolutePath
        } catch (e: IOException) {
            Log.e(LocalStorageProvider::class.java.simpleName, "Error creating new file $newFile")
        }
        return null
    }

    @Throws(FileNotFoundException::class)
    override fun openDocumentThumbnail(
        documentId: String, sizeHint: Point,
        signal: CancellationSignal
    ): AssetFileDescriptor? {
        // Assume documentId points to an image file. Build a thumbnail no
        // larger than twice the sizeHint
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(documentId, options)
        val targetHeight = 2 * sizeHint.y
        val targetWidth = 2 * sizeHint.x
        val height = options.outHeight
        val width = options.outWidth
        options.inSampleSize = 1
        if (height > targetHeight || width > targetWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / options.inSampleSize > targetHeight
                || halfWidth / options.inSampleSize > targetWidth
            ) {
                options.inSampleSize *= 2
            }
        }
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(documentId, options)
        // Write out the thumbnail to a temporary file
        var tempFile: File? = null
        var out: FileOutputStream? = null
        try {
            tempFile = File.createTempFile("thumbnail", null, context!!.cacheDir)
            out = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        } catch (e: IOException) {
            Log.e(LocalStorageProvider::class.java.simpleName, "Error writing thumbnail", e)
            return null
        } finally {
            if (out != null) try {
                out.close()
            } catch (e: IOException) {
                Log.e(LocalStorageProvider::class.java.simpleName, "Error closing thumbnail", e)
            }
        }
        // It appears the Storage Framework UI caches these results quite
        // aggressively so there is little reason to
        // write your own caching layer beyond what you need to return a single
        // AssetFileDescriptor
        return AssetFileDescriptor(
            ParcelFileDescriptor.open(
                tempFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            ), 0,
            AssetFileDescriptor.UNKNOWN_LENGTH
        )
    }

    @Throws(FileNotFoundException::class)
    override fun queryChildDocuments(
        parentDocumentId: String, projection: Array<String>,
        sortOrder: String
    ): Cursor {
        // Create a cursor with either the requested fields, or the default
        // projection if "projection" is null.
        val result = MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION)
        val parent = File(parentDocumentId)
        for (file in parent.listFiles()) {
            // Don't show hidden files/folders
            if (!file.name.startsWith(".")) {
                // Adds the file's display name, MIME type, size, and so on.
                includeFile(result, file)
            }
        }
        return result
    }

    @Throws(FileNotFoundException::class)
    override fun queryDocument(documentId: String, projection: Array<String>): Cursor {
        // Create a cursor with either the requested fields, or the default
        // projection if "projection" is null.
        val result = MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION)
        includeFile(result, File(documentId))
        return result
    }

    @Throws(FileNotFoundException::class)
    private fun includeFile(result: MatrixCursor, file: File) {
        val row = result.newRow()
        // These columns are required
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, file.absolutePath)
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, file.name)
        val mimeType = getDocumentType(file.absolutePath)
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, mimeType)
        var flags = if (file.canWrite()) DocumentsContract.Document.FLAG_SUPPORTS_DELETE or DocumentsContract.Document.FLAG_SUPPORTS_WRITE else 0
        // We only show thumbnails for image files - expect a call to
        // openDocumentThumbnail for each file that has
        // this flag set
        if (mimeType.startsWith("image/")) flags = flags or DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL
        row.add(DocumentsContract.Document.COLUMN_FLAGS, flags)
        // COLUMN_SIZE is required, but can be null
        row.add(DocumentsContract.Document.COLUMN_SIZE, file.length())
        // These columns are optional
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, file.lastModified())
        // Document.COLUMN_ICON can be a resource id identifying a custom icon.
        // The system provides default icons
        // based on mime type
        // Document.COLUMN_SUMMARY is optional additional information about the
        // file
    }

    @Throws(FileNotFoundException::class)
    override fun getDocumentType(documentId: String): String {
        val file = File(documentId)
        if (file.isDirectory) return DocumentsContract.Document.MIME_TYPE_DIR
        // From FileProvider.getType(Uri)
        val lastDot = file.name.lastIndexOf('.')
        if (lastDot >= 0) {
            val extension = file.name.substring(lastDot + 1)
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            if (mime != null) {
                return mime
            }
        }
        return "application/octet-stream"
    }

    @Throws(FileNotFoundException::class)
    override fun deleteDocument(documentId: String) {
        File(documentId).delete()
    }

    @Throws(FileNotFoundException::class)
    override fun openDocument(
        documentId: String, mode: String,
        signal: CancellationSignal?
    ): ParcelFileDescriptor {
        val file = File(documentId)
        val isWrite = mode.indexOf('w') != -1
        return if (isWrite) {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } else {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    companion object {
        const val AUTHORITY = "com.ianhanniballake.localstorage.documents"

        /**
         * Default root projection: everything but Root.COLUMN_MIME_TYPES
         */
        private val DEFAULT_ROOT_PROJECTION = arrayOf(
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.COLUMN_TITLE, DocumentsContract.Root.COLUMN_DOCUMENT_ID, DocumentsContract.Root.COLUMN_ICON,
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES
        )

        /**
         * Default document projection: everything but Document.COLUMN_ICON and
         * Document.COLUMN_SUMMARY
         */
        private val DEFAULT_DOCUMENT_PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_FLAGS, DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )
    }
}

/*
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * @version 2009-07-03
 * @author Peli
 * @version 2013-12-11
 * @author paulburke (ipaulpro)
 */
object FileUtils {
    /** TAG for log messages.  */
    const val TAG = "FileUtils"
    private const val DEBUG = false // Set to true to enable logging
    const val MIME_TYPE_AUDIO = "audio/*"
    const val MIME_TYPE_TEXT = "text/*"
    const val MIME_TYPE_IMAGE = "image/*"
    const val MIME_TYPE_VIDEO = "video/*"
    const val MIME_TYPE_APP = "application/*"
    const val HIDDEN_PREFIX = "."

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    fun getExtension(uri: String?): String? {
        if (uri == null) {
            return null
        }
        val dot = uri.lastIndexOf(".")
        return if (dot >= 0) {
            uri.substring(dot)
        } else {
            // No extension.
            ""
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    fun isLocal(url: String?): Boolean {
        return if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            true
        } else false
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    fun isMediaUri(uri: Uri?): Boolean {
        return "media".equals(uri!!.authority, ignoreCase = true)
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    fun getUri(file: File?): Uri? {
        return if (file != null) {
            Uri.fromFile(file)
        } else null
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    fun getPathWithoutFilename(file: File?): File? {
        return if (file != null) {
            if (file.isDirectory) {
                // no file to be split off. Return everything
                file
            } else {
                val filename = file.name
                val filepath = file.absolutePath

                // Construct path without file name.
                var pathwithoutname = filepath.substring(
                    0,
                    filepath.length - filename.length
                )
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
                }
                File(pathwithoutname)
            }
        } else null
    }

    /**
     * @return The MIME type for the given file.
     */
    fun getMimeType(file: File): String? {
        val extension = getExtension(file.name)
        return if (extension!!.length > 0) MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            extension.substring(1)
        ) else "application/octet-stream"
    }

    /**
     * @return The MIME type for the give Uri.
     */
    fun getMimeType(context: Context, uri: Uri): String? {
        val file = File(getPath(context, uri))
        return getMimeType(file)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is [LocalStorageProvider].
     * @author paulburke
     */
    fun isLocalStorageDocument(uri: Uri): Boolean {
        return LocalStorageProvider.AUTHORITY == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
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

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @see .isLocal
     * @see .getFile
     * @author paulburke
     */
    fun getPath(context: Context, uri: Uri): String? {
        // check here to KITKAT or new version
        var contentUri: Uri? = null
        val isKitKat = true
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // DocumentProvider
        if (isKitKat) {
            // ExternalStorageProvider
            if (RealPathUtil.isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                val fullPath: String = RealPathUtil.getPathFromExtSD(split)
                return if (fullPath !== "") {
                    fullPath
                } else {
                    null
                }
            }


            // DownloadsProvider
            if (RealPathUtil.isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.getContentResolver().query(
                            uri,
                            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val fileName = cursor.getString(0)
                            val path = Environment.getExternalStorageDirectory()
                                .toString() + "/Download/" + fileName
                            if (!TextUtils.isEmpty(path)) {
                                return path
                            }
                        }
                    } finally {
                        cursor?.close()
                    }
                    val id: String = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:".toRegex(), "")
                        }
                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                        )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            return try {
                                val contentUri =
                                    ContentUris.withAppendedId(
                                        Uri.parse(contentUriPrefix),
                                        java.lang.Long.valueOf(id)
                                    )
                                RealPathUtil.getDataColumn(context, contentUri, null, null)
                            } catch (e: NumberFormatException) {
                                //In Android 8 and Android P the id is not a number
                                uri.path!!.replaceFirst("^/document/raw:".toRegex(), "")
                                    .replaceFirst("^raw:".toRegex(), "")
                            }
                        }
                    }
                } else {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                    if (contentUri != null) {
                        return RealPathUtil.getDataColumn(context, contentUri, null, null)
                    }
                }
            }


            // MediaProvider
            if (RealPathUtil.isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }

                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
                return RealPathUtil.getDataColumn(
                    context, contentUri!!, selection,
                    selectionArgs
                )
            }
            if (RealPathUtil.isGoogleDriveUri(uri)) {
                return RealPathUtil.getDriveFilePath(uri, context)
            }
            if (RealPathUtil.isWhatsAppFile(uri)) {
                return RealPathUtil.getFilePathForWhatsApp(uri, context)
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                if (RealPathUtil.isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                if (RealPathUtil.isGoogleDriveUri(uri)) {
                    return RealPathUtil.getDriveFilePath(uri, context)
                }
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // return getFilePathFromURI(context,uri);
                    RealPathUtil.copyFileToInternalStorage(uri, "userfiles", context)
                    // return getRealPathFromURI(context,uri);
                } else {
                    RealPathUtil.getDataColumn(context, uri, null, null)
                }
            }
            if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } else {
            if (RealPathUtil.isWhatsAppFile(uri)) {
                return RealPathUtil.getFilePathForWhatsApp(uri, context)
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                val cursor: Cursor?
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val columnIndex =
                        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(columnIndex)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @see .getPath
     * @author paulburke
     */
    fun getFile(context: Context, uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(context, uri)
            if (isLocal(path)) {
                return File(path)
            }
        }
        return null
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    fun getReadableFileSize(size: Int): String {
        val BYTES_IN_KILOBYTES = 1024
        val dec = DecimalFormat("###.#")
        val KILOBYTES = " KB"
        val MEGABYTES = " MB"
        val GIGABYTES = " GB"
        var fileSize = 0f
        var suffix = KILOBYTES
        if (size > BYTES_IN_KILOBYTES) {
            fileSize = (size / BYTES_IN_KILOBYTES).toFloat()
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES
                    suffix = GIGABYTES
                } else {
                    suffix = MEGABYTES
                }
            }
        }
        return dec.format(fileSize.toDouble()) + suffix
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param file
     * @return
     * @author paulburke
     */
    fun getThumbnail(context: Context, file: File): Bitmap? {
        return getThumbnail(context, getUri(file), getMimeType(file))
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @return
     * @author paulburke
     */
    fun getThumbnail(context: Context, uri: Uri): Bitmap? {
        return getThumbnail(context, uri, getMimeType(context, uri))
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    fun getThumbnail(context: Context, uri: Uri?, mimeType: String?): Bitmap? {
        if (DEBUG) Log.d(TAG, "Attempting to get thumbnail")
        if (!isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.")
            return null
        }
        var bm: Bitmap? = null
        if (uri != null) {
            val resolver = context.contentResolver
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(uri, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val id = cursor.getInt(0)
                    if (DEBUG) Log.d(TAG, "Got thumb ID: $id")
                    if (mimeType!!.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                            resolver,
                            id.toLong(),
                            MediaStore.Video.Thumbnails.MINI_KIND,
                            null
                        )
                    } else if (mimeType.contains(MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                            resolver,
                            id.toLong(),
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                if (DEBUG) Log.e(TAG, "getThumbnail", e)
            } finally {
                cursor?.close()
            }
        }
        return bm
    }

    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    var sComparator =
        Comparator<File> { f1, f2 -> // Sort alphabetically by lower case, which is much cleaner
            f1.name.lowercase(Locale.getDefault()).compareTo(
                f2.name.lowercase(Locale.getDefault())
            )
        }

    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    var sFileFilter = FileFilter { file ->
        val fileName = file.name
        // Return files only (not directories) and skip hidden files
        file.isFile && !fileName.startsWith(HIDDEN_PREFIX)
    }

    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    var sDirFilter = FileFilter { file ->
        val fileName = file.name
        // Return directories only and skip hidden directories
        file.isDirectory && !fileName.startsWith(HIDDEN_PREFIX)
    }

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */
    fun createGetContentIntent(): Intent {
        // Implicitly allow the user to select a particular kind of data
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        intent.type = "*/*"
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }
}

private object RealPathUtil {

    fun getRealPath(context: Context, uri: Uri): String? {
        // check here to KITKAT or new version
        var contentUri: Uri? = null
        val isKitKat = true
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // DocumentProvider
        if (isKitKat) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                val fullPath: String = getPathFromExtSD(split)
                return if (fullPath !== "") {
                    fullPath
                } else {
                    null
                }
            }


            // DownloadsProvider
            if (isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.getContentResolver().query(uri,
                            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                            null,
                            null,
                            null)
                        if (cursor != null && cursor.moveToFirst()) {
                            val fileName = cursor.getString(0)
                            val path = Environment.getExternalStorageDirectory()
                                .toString() + "/Download/" + fileName
                            if (!TextUtils.isEmpty(path)) {
                                return path
                            }
                        }
                    } finally {
                        cursor?.close()
                    }
                    val id: String = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:".toRegex(), "")
                        }
                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                        )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            return try {
                                val contentUri =
                                    ContentUris.withAppendedId(Uri.parse(contentUriPrefix),
                                        java.lang.Long.valueOf(id))
                                getDataColumn(context, contentUri, null, null)
                            } catch (e: NumberFormatException) {
                                //In Android 8 and Android P the id is not a number
                                uri.path!!.replaceFirst("^/document/raw:".toRegex(), "")
                                    .replaceFirst("^raw:".toRegex(), "")
                            }
                        }
                    }
                } else {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id))
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null)
                    }
                }
            }


            // MediaProvider
            if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri!!, selection,
                    selectionArgs)
            }
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context)
            }
            if (isWhatsAppFile(uri)) {
                return getFilePathForWhatsApp(uri, context)
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri, context)
                }
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // return getFilePathFromURI(context,uri);
                    copyFileToInternalStorage(uri, "userfiles", context)
                    // return getRealPathFromURI(context,uri);
                } else {
                    getDataColumn(context, uri, null, null)
                }
            }
            if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } else {
            if (isWhatsAppFile(uri)) {
                return getFilePathForWhatsApp(uri, context)
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                val cursor: Cursor?
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val columnIndex =
                        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(columnIndex)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    fun getPathFromExtSD(pathData: Array<String>): String {
        val type = pathData[0]
        val relativePath = "/" + pathData[1]
        var fullPath: String

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equals(type, ignoreCase = true)) {
            fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
            if (fileExists(fullPath)) {
                return fullPath
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
        if (fileExists(fullPath)) {
            return fullPath
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
        return if (fileExists(fullPath)) {
            fullPath
        } else fullPath
    }

    fun getDriveFilePath(uri: Uri, context: Context): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }

            inputStream.close()
            outputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.getPath()
    }

    fun copyFileToInternalStorage(uri: Uri, newDirName: String, context: Context): String {
        val returnCursor = context.contentResolver.query(uri, arrayOf(
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        ), null, null, null)


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val output: File
        if (newDirName != "") {
            val dir = File(context.filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(context.filesDir.toString() + "/" + newDirName + "/" + name)
        } else {
            output = File(context.filesDir.toString() + "/" + name)
        }
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream!!.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return output.path
    }

    fun getFilePathForWhatsApp(uri: Uri, context: Context): String {
        return copyFileToInternalStorage(uri, "whatsapp", context)
    }

    fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection,
                selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isWhatsAppFile(uri: Uri): Boolean {
        return "com.whatsapp.provider.media" == uri.authority
    }

    fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }
}

fun saveImageToInternalStorage(
    context: Context,
    image: Bitmap,
): File {
    val stream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 50, stream)
    val byteArray = stream.toByteArray()
    image.recycle()

    val fileName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        "${FileNameBuilder.buildFileNameAPI26()}.jpg"
    } else {
        "${FileNameBuilder.buildFileName()}.jpg"
    }
    val file = File.createTempFile(fileName, null, createInternalFile(context))
    val uri = file.toUri()
    return context.contentResolver.openOutputStream(uri)?.let { os ->
        os.write(byteArray)
        os.flush()
        os.close()
        file
    } ?: throw Exception("Error save image to internal storage")
}

private object FileNameBuilder {

    /**
     * Build a string formatted like: YYYY_MM_DD_SS in the default time zone on device.
     */
    fun buildFileName(): String {
        val dateFormat = SimpleDateFormat("yyyy_MM_dd_ss.SSS")
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(Date(System.currentTimeMillis()))
    }

    /**
     * Build a string formatted like: YYYY_MM_DD_SS in the default time zone on device.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildFileNameAPI26(): String {
        val zonedDateTime = Date().toInstant().atZone(ZoneId.systemDefault())
        return "${zonedDateTime.year}_" +
                "${formatMonth(zonedDateTime.month.value)}_" +
                "${formatDay(zonedDateTime.dayOfMonth)}_" +
                "${zonedDateTime.second}_" +
                "${zonedDateTime.nano}"
    }

    private fun formatDay(day: Int): String {
        return if (day < 10) {
            "0$day"
        } else {
            "$day"
        }
    }

    private fun formatMonth(month: Int): String {
        return if (month < 10) {
            "0$month"
        } else {
            "$month"
        }
    }
}

private fun createInternalFile(context: Context): File {
    val file = File("${context.cacheDir.path}/temp_images")
    if (!file.exists()) {
        file.mkdir()
    }
    return file
}
