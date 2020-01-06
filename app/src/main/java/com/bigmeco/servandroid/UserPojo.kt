package com.bigmeco.servandroid

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.koushikdutta.async.http.WebSocket


class UserPojo {

    @SerializedName("type")
    @Expose
    private var type: String = ""

    @SerializedName("name")
    @Expose
    private var name: String = ""

    @Expose(serialize = false)
    var socet: WebSocket? = null


}