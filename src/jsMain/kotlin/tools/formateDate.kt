package tools

fun formatDate(dateStr: String): String {
    val parts = dateStr.split("-")
    if (parts.size == 3) {
        return "${parts[2]}.${parts[1]}.${parts[0]}"  // ДД.ММ.ГГГГ
    }
    return dateStr
}