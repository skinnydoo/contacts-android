package com.vestrel00.contacts.entities.operation

import com.vestrel00.contacts.Field
import com.vestrel00.contacts.Fields
import com.vestrel00.contacts.entities.MimeType
import com.vestrel00.contacts.entities.MutableNickname

internal class NicknameOperation(isProfile: Boolean) :
    AbstractCommonDataOperation<MutableNickname>(isProfile) {

    override val mimeType = MimeType.NICKNAME

    override fun setData(
        data: MutableNickname, setValue: (field: Field, dataValue: Any?) -> Unit
    ) {
        setValue(Fields.Nickname.Name, data.name)
    }
}