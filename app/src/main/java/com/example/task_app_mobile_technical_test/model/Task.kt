package com.example.task_app_mobile_technical_test.model

import android.os.Parcelable
import com.example.task_app_mobile_technical_test.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var description: String = "",
    var status: Int = 0
): Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key.toString()
    }
}
