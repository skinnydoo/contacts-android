package com.vestrel00.contacts.entities.cursor

import android.database.Cursor
import android.net.Uri
import com.vestrel00.contacts.Fields
import com.vestrel00.contacts.entities.INVALID_ID

/**
 * Retrieves [Fields.Contacts] data from the given [cursor].
 *
 * This does not modify the [cursor] position. Moving the cursor may result in different attribute
 * values.
 */
internal class ContactsCursor(private val cursor: Cursor) {

    val id: Long
        get() = cursor.getLong(Fields.Contacts.Id) ?: INVALID_ID

    val photoUri: Uri?
        get() = cursor.getUri(Fields.Contacts.PhotoUri)

    val photoThumbnailUri: Uri?
        get() = cursor.getUri(Fields.Contacts.PhotoThumbnailUri)

    val photoFileId: Long?
        get() {
            val value = cursor.getLong(Fields.Contacts.PhotoFileId)
            // Sometimes the value will be zero instead of null but 0 is not a valid photo file id.
            return if (value != null && value > 0) value else null
        }
}