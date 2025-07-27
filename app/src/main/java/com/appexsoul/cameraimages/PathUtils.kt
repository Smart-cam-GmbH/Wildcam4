package com.appexsoul.cameraimages

fun buildRemotePath(folder: String, fileName: String): String {
    return if (folder.endsWith("/")) "$folder$fileName" else "$folder/$fileName"
}
