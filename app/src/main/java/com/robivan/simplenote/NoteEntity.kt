package com.robivan.simplenote

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.text.SimpleDateFormat
import java.util.*

open class NoteEntity : Parcelable {
    var id: String? = null
    var title: String?
    var noteText: String?
    val createDate: String?
    var date: Long

    constructor(title: String?, noteText: String?, date: Long) {
        this.title = title
        this.noteText = noteText
        this.date = date
        val dateFormat = SimpleDateFormat("d.MM.y, HH:mm", Locale.getDefault())
        createDate = dateFormat.format(date)
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readString()
        title = `in`.readString()
        noteText = `in`.readString()
        date = `in`.readLong()
        createDate = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(noteText)
        dest.writeLong(date)
        dest.writeString(createDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<NoteEntity> {
        override fun createFromParcel(parcel: Parcel): NoteEntity {
            return NoteEntity(parcel)
        }

        override fun newArray(size: Int): Array<NoteEntity?> {
            return arrayOfNulls(size)
        }
        val currentDate: Long
            get() = Calendar.getInstance().timeInMillis
    }
}