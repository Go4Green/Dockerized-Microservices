package com.thecrafter.ecosnap.data

data class Incident(
    val uid: String,
    val citizenUid: String,
    val timestamp: Long,
    val snap: String,
    val lat: Float,
    val lng: Float,
    val address: String,
    val description: String,
    val category: String,
    val status: String
)

// TODO: Remove
val mockIncidents: List<Incident> = listOf(
    Incident(
        "asdf1", "zxcv1", 1234, "", 2.0f, 3.0f, "addr1",
        "desc", "Trash", "Pending"),
    Incident(
        "asdf2", "zxcv2", 1234, "", 2.0f, 3.0f, "addr2",
        "desc", "Trash", "Uploading"),
    Incident(
        "asdf3", "zxcv3", 1234, "", 2.0f, 3.0f, "addr3",
        "desc", "Trash", "Failed"),
    Incident(
        "asdf4", "zxcv4", 1234, "", 2.0f, 3.0f, "addr4",
        "desc", "Trash", "Approved")

)