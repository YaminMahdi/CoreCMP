@file:Suppress("unused", "CONTEXT_RECEIVERS_DEPRECATED")

package com.dora.user.utils

import com.dora.user.BuildKonfig
import com.dora.user.ioDispatcher
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


/**Coroutines Extension Function*/
@Suppress("FunctionName")
suspend fun <T, R> T.IO(block: suspend T.() -> R) = withContext(ioDispatcher()) {
    block()
}

@Suppress("FunctionName")
suspend fun <T, R> T.MAIN(block: suspend T.() -> R) = withContext(Dispatchers.Main) {
    block()
}

@Suppress("FunctionName")
suspend fun <T, R> T.DEFAULT(block: suspend T.() -> R) = withContext(Dispatchers.Default) {
    block()
}


inline fun <T, R> Flow<List<T>>.mapList(crossinline transform: suspend T.() -> R): Flow<List<R>> =
    this.map {
        it.map { value ->
            transform(value)
        }
    }


//fun Any?.logJson(tag: String = "TAG") {
//    if (BuildConfig.IS_INTERNAL_TESTING) {
//        Log.i("log> '$tag'", "${this?.javaClass?.name}")
//        if (this is String)
//            com.orhanobut.logger.Logger.json(this)
//        else
//            com.orhanobut.logger.Logger.json(Gson().toJson(this))
//    }
//}

fun Any?.log(tag: String = "TAG"): Any? {
    this ?: return null
    if (BuildKonfig.IS_DEBUG)
        Napier.i("$tag - $this : ${this::class.simpleName}", tag = "log> '$tag'")
    return this
}



// Helper for zero-padding hours/minutes, since String.format not allowed
private fun Int.pad2() = toString().padStart(2, '0')

// --- Extensions ---

fun Long.toDateTime(): String {
    val zone = TimeZone.currentSystemDefault()
    val now = Clock.System.now().toLocalDateTime(zone)
    val dateTime = Instant.fromEpochMilliseconds(this).toLocalDateTime(zone)

    val format = if (dateTime.date == now.date) {
        // same day: "hh:mm a"
        val hour12 = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
        val txt = if (dateTime.hour < 12) "AM" else "PM"
        "${hour12.pad2()}:${dateTime.minute.pad2()} $txt"
    } else {
        // different day: "dd MMM, hh:mm a"
        val day = dateTime.dayOfMonth
        val monthName = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3) // Jan, Feb, etc.
        val hour12 = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
        val txt = if (dateTime.hour < 12) "AM" else "PM"
        "$day $monthName, ${hour12.pad2()}:${dateTime.minute.pad2()} $txt"
    }

    return format
}

fun Long.toDayPassed(): String {
    val nowMs = Clock.System.now().toEpochMilliseconds()
    val msDiff = (nowMs - this).coerceAtLeast(0)
    val daysDiff = msDiff / 86_400_000L // milliseconds per day
    return "$daysDiff Days Ago"
}

typealias DateRange = String

fun Pair<Long, Long>.toDateRange(): DateRange {
    val (start, end) = this
    return "${start.toDateModern()} - ${end.toDateModern()}"
}

fun Pair<Long, Long>.toDayPassed(): Int {
    val (start, end) = this
    val diffDays = ((end - start).absoluteValue / 86_400_000L).toInt() + 1
    return diffDays
}

fun Long.toDuration(): Pair<String, Long> {
    val totalMinutes = this / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val formatted = if (hours > 0) "${hours}h, ${minutes}m" else "${minutes}m"
    return formatted to totalMinutes
}


fun Long.toTimeDate(): String {
    val localDateTime = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour12 = if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12
    val txt = if (localDateTime.hour < 12) "AM" else "PM"
    val timePart = "${hour12.pad2()}${txt}_${localDateTime.dayOfMonth.pad2()}-${localDateTime.monthNumber.pad2()}-${localDateTime.year}"
    return timePart
}

fun Long.toDate(): String {
    val date = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "%${date.dayOfMonth.pad2()}/${date.monthNumber.pad2()}/${date.year % 100}"
}

fun Long.toDateModern(): String {
    val date = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "${date.dayOfMonth} $monthName, ${date.year}"
}

private fun Int.pad(length: Int): String = this.toString().padStart(length, '0') // More general version

fun Long.toDateServer(): String {
    val date = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC).date
    return "${date.year}-${date.monthNumber.pad2()}-${date.dayOfMonth.pad2()}"
}

fun Long.toDateTimeServer(): String {
    val ldt = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
    return "${ldt.year}-${ldt.monthNumber.pad2()}-${ldt.dayOfMonth.pad2()}" +
            " ${ldt.hour.pad2()}:${ldt.minute.pad2()}:${ldt.second.pad2()}"
}

fun Long.toTime(): String {
    val ldt = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour12 = if (ldt.hour % 12 == 0) 12 else ldt.hour % 12
    val txt = if (ldt.hour < 12) "AM" else "PM"
    return "${hour12.pad2()}:${ldt.minute.pad2()} $txt"
}

fun Long.toTimeServer(): String {
    val ldt = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${ldt.hour.pad2()}:${ldt.minute.pad2()}"
}

fun Long.toTimeHyphen(): String {
    val ldt = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour12 = if (ldt.hour % 12 == 0) 12 else ldt.hour % 12
    val txt = if (ldt.hour < 12) "AM" else "PM"
    return "${hour12.pad2()}-${ldt.minute.pad2()}$txt"
}

fun Long.toClockTime(): Triple<Int, Int, Int> {
    val ldt = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
    return Triple(ldt.hour, ldt.minute, ldt.second)
}

fun Long.toFirstMonthDate(): Long {
    val ldt = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
    val firstDay = LocalDate(ldt.year, ldt.monthNumber, 1).atStartOfDayIn(TimeZone.currentSystemDefault())
    return firstDay.toEpochMilliseconds()
}

fun Long.toYear(): String {
    val year = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).year
    return year.toString()
}

fun String.toTimeStamp(): Long {
    // Parses yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
    return try {
        if (this.contains(" ")) {
            val ldt = LocalDateTime.parse(this)
            ldt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } else {
            val ld = LocalDate.parse(this)
            ld.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }
    } catch (e: Exception) {
        0L
    }
}

fun Long.toMonthDay(): Int {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth
}

fun Long.toMonthCount(): Int {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
}



fun String.to12HourFormat(): String {
    val parts = this.split(":").map { it.toInt() }
    val hours = parts[0]
    val minutes = parts[1]

    return when {
        hours == 0 -> "12:${minutes.pad2()}AM"
        hours < 12 -> "${hours}:${minutes.pad2()}AM"
        hours == 12 -> "${hours}:${minutes.pad2()}PM"
        else -> "${hours - 12}:${minutes.pad2()}PM"
    }
}


fun Int.toTimer(): Triple<Int, Int, Int> {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return Triple(hours, minutes, seconds)
}


fun String.toUppercaseType(): String {
    return replaceFirstChar(Char::uppercaseChar)
        .replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
        .split(Regex("[-_ ]"))
        .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
}

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit,
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}

fun <T> throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit,
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}

fun <T> debounce(
    waitMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit,
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

fun debounce(
    coroutineScope: CoroutineScope,
    waitMs: Long = 300L,
    desFun: () -> Unit,
): () -> Unit {
    var debounceJob: Job? = null
    return {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch(ioDispatcher()) {
            delay(waitMs)
            desFun.invoke()
        }
    }
}

/**
 * Returns 'true' if this string only has numbers else 'false'
 */
fun String.hasNumberOnly() = this.matches("-?\\d+(\\.\\d+)?".toRegex())


fun Boolean.toYesNo() = if (this) "YES" else "NO"
fun Boolean?.isTrue() = this == true
fun Boolean?.isFalse() = this == null || !this

inline fun Boolean?.isTrue(next: () -> Unit): Boolean? {
    if (isTrue()) next()
    return this
}

inline fun Boolean?.isFalse(next: () -> Unit): Boolean? {
    if (isFalse()) next()
    return this
}

inline fun String?.isNotEmpty(next: (String) -> Unit): String? {
    if (!isNullOrEmpty()) next(this)
    return this
}

inline fun <T> Collection<T>?.isEmpty(next: () -> Unit): Collection<T>? {
    if (isNullOrEmpty()) next()
    return this
}

inline fun <T> Collection<T>?.isNotEmpty(next: (List<T>) -> Unit): Collection<T>? {
    if (!isNullOrEmpty()) next(toList())
    return this
}

fun String?.isBanglaText() = this == null || matches("[\\u0980-\\u09FF\\s,।_/-]+".toRegex())

fun String?.isValidEmail(): Boolean {
    if (isNullOrBlank()) return false
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(this)
}


fun String.isValidPhoneBD(): Boolean {
    val phoneNumberRegexBd = "^(?:\\+?88)?01[3-9]\\d{8}$".toRegex()
    return matches(phoneNumberRegexBd)
}

fun String.isValidPhoneUS(): Boolean {
    val usPhoneNumberRegex = """^(?:\+?1[-. ]?)?\(?([2-9][0-8][0-9])\)?[-. ]?([2-9][0-9]{2})[-. ]?([0-9]{4})$""".toRegex()
    return matches(usPhoneNumberRegex)
}

fun String.isValidPassword(): Boolean {
    val hasUpper = any { it.isUpperCase() }
    val hasLower = any { it.isLowerCase() }
    val hasNumber = any { it.isDigit() }
    val isGraterThan8 = length >= 8
//    val hasSpecial = this.any { "!@#\$%^&*()-_=+[]{};:'\",.<>?/\\|`~".contains(it) }
    return hasUpper && hasLower && hasNumber && isGraterThan8
}

fun String?.isJson(): Boolean {
    if (this == null) return false
    // A regex pattern for valid JSON
    val jsonPattern = """^\s*(\{.*\}|\[.*])\s*$""".toRegex()
    // Check if the string matches the JSON pattern
    return jsonPattern.matches(this)
}

fun String.isValidURL(): Boolean {
    val urlRegex = Regex("^(https?|ftp)://[a-zA-Z0-9.-]+(:[0-9]+)?(/.*)?$", RegexOption.IGNORE_CASE)
    return urlRegex.matches(this)
}


fun <T> T.isNull() = this == null
fun <T> T.isNotNull() = this != null

inline fun <T> T?.isNull(next: () -> Unit): T? {
    if (this == null) next()
    return this
}

inline fun <T> T?.isNotNull(next: (T) -> Unit): T? {
    if (this != null) next(this)
    return this
}

fun Int?.isNullOrZero() = this == null || this == 0
fun Double?.toOneIfZero() = if (this == 0.0 || this == null) 1.0 else this
fun String?.toNAifEmpty() =
    if (isNullOrEmpty() || isBlank() || (this == "0" || this == "0.0")) "N/A" else this

fun String?.toNullifEmpty() =
    if (isNullOrEmpty() || isBlank() || (this == "0" || this == "0.0")) null else this

fun String?.toDoubleOrZero() =
    this?.replace("[^\\d.]".toRegex(), "")?.toDoubleOrNull().orZero().roundTo(2)

fun Int?.orMinusOne() = this ?: -1
fun Int?.orZero() = this ?: 0
fun Long?.orZero() = this ?: 0L
fun Double?.orZero() = this ?: 0.0
fun Float?.orZero() = this ?: 0.0F
fun String?.orZero() = this?.toIntOrNull() ?: 0
fun String?.orZeroD() = this?.toDoubleOrNull() ?: 0.0
fun Boolean?.orFalse() = this == true
fun Boolean.orNull() = if(!this) null else true

fun Int?.isZero() = this == null || this == 0
fun Int?.isMinusOne() = this == null || this == -1
fun Long?.isZero() = this == null || this == 0L
fun Double?.isZero() = this == null || this == 0.0
fun Float?.isZero() = this == null || this == 0.0F
fun Float?.isMinusOne() = this == null || this == -1.0F
fun Float?.isZeroOrMinusOne() = this == null || this == 0.0F || this == -1.0F
fun String?.isZero() = this == null || this.toIntOrNull() == 0
fun String?.isZeroD() = this == null || this.toDoubleOrNull() == 0.0


inline fun <T> tryGet(data: () -> T): T? =
    try {
        data()
    } catch (e: Exception) {
        null
    }

inline fun <T> tryInMain(crossinline data: suspend CoroutineScope.() -> T) =
    CoroutineScope(Dispatchers.Main.immediate).launch {
        try {
            data()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

inline fun <T> T.applyIf(ifTrue: Boolean, block: T.() -> Unit): T {
    if (ifTrue) block.invoke(this)
    return this
}
inline fun Boolean.ifTrue(block: Boolean.() -> Unit): Boolean {
    if (this) block.invoke(true)
    return this
}

fun String?.getPercentage(total: Int): Int {
    if (total == 0 || isNullOrEmpty() || toDoubleOrNull() == null) return 100
    return toDoubleOrZero().getPercentage(total)
}

fun Number.getPercentageFloat(total: Number): Double {
    if (total == 0) return 100.0
    return (toDouble().times(100.0).div(total.toDouble())).roundTo(2)
}

fun Number.getPercentage(total: Number): Int {
    if (total == 0) return 100
    return (toDouble().times(100.0).div(total.toDouble())).toInt()
}

//fun Int.getPercentageTxt(total: Int): String {
//    if (total == 0) return "100%"
//    return "${times(100).div(total)}%"
//}
fun Number.getPercentageTxt(total: Number, asInteger: Boolean = false): String {
    if (total == 0.0) return "100%"
    return toDouble().times(100).div(total.toDouble()).run {
        if (asInteger) roundToInt() else roundTo(2)
    }.toString() + "%"

}

fun Number.getGrowthTxt(lastValue: Number, decimalPoint: Int = 2): String {
    if (lastValue == 0.0) return "100%"
    return "${
        this.toDouble()
            .minus(lastValue.toDouble())
            .times(100.0)
            .div(lastValue.toDouble())
            .roundTo(decimalPoint)
    }%"
}

fun Number.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(toDouble() * factor) / factor
}

fun Number.addPercentage(decimals: Int = 2): String {
    return "${roundTo(decimals)}%"
}

fun Int.addPercentage(): String {
    return "$this%"
}

//fun Double?.toTK(): String {
//    if (this == null) return "৳0"
//    return (userNullable?.currencySymbol ?: "৳") + when {
//        (this / 100000).toInt() != 0 -> "%,.1f".format(this / 100000) + "Lac"
//        (this / 1000).toInt() != 0 -> "%,.1f".format(this / 1000) + "K"
//        else -> "%,.1f".format(this)
//    }
//}
//
//fun Int.toTK(): String {
//    return (userNullable?.currencySymbol ?: "৳") + when {
//        this / 100000 != 0 -> "%,d".format(this / 100000) + "Lac"
//        this / 1000 != 0 -> "%,d".format(this / 1000) + "K"
//        else -> "%,d".format(this)
//    }
//}



//fun Double?.addTK(decimalPoint: Int = 2): String {
//    if (this == null) return "৳0"
//    return (userNullable?.currencySymbol ?: "৳") + "%.${decimalPoint}f".format(this)
//}



fun String.getOptimizedPrintText(totalLength: Int): String {
    val newText: String = if (this.length > totalLength) {
        this.substring(0, totalLength)
    } else {
        this
    }
    return newText
}

/**
 * Deserializes a JSON string to an object of type T.
 * The class T must be annotated with @Serializable.
 */
inline fun <reified T> String?.fromJson(): T {
    requireNotNull(this) { "Cannot deserialize null string to ${T::class.simpleName}" }
    return Json.decodeFromString<T>(this)
}

// --- More Type-Safe and Recommended Alternatives for toJson and toObject ---

/**
 * Serializes an object of a known @Serializable type T to its JSON string representation.
 */
inline fun <reified T : Any> T?.toJson(): String {
    if (this == null) return "null"
    // This requires T to be @Serializable, which is good for type safety.
    // If T can be null, then T? and handle null separately or use a nullable serializer.
    return Json.encodeToString(this)
}

fun String?.toUppercaseAllWordRegex(): String? {
    return this?.replace(Regex("\\b\\w")) { it.value.uppercase() }
}


fun main() {
    data class Item(val id: Int = 1)
    data class YK(
        val name: String = "Ma:hd:i",
        val age: Int = 24,
        val list: List<Item> = listOf(Item(1), Item(2), Item(3), Item(4)),
        val list2: List<Item> = listOf(),
    )
}

/*fun Context.getGeoAddress(lat: Double, lon: Double): String? {
    return try {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address: Address = addresses[0]
            val addressLines = (0..address.maxAddressLineIndex).map { index ->
                address.getAddressLine(index)
            }
            addressLines.joinToString("\n")
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("Geocoder Error", "Error fetching address", e)
        null
    }
}*/

//fun Context?.getGeoAddress(location: Location, onSuccess: (String) -> Unit){
//    this ?: return
//    MainScope().launch {
//        getGeoAddress(location.latitude, location.longitude)?.let {
//            onSuccess(it)
//        }
//    }
//}
//
//@Suppress("DEPRECATION")
//suspend fun Context?.getGeoAddress(lat: Double, lon: Double): String? {
//    return withContext(Dispatchers.IO) {
//        this@getGeoAddress ?: return@withContext null
//        try {
//            val geocoder = Geocoder(this@getGeoAddress, Locale.getDefault())
//            val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)
//            if (!addresses.isNullOrEmpty()) {
//                val address: Address = addresses[0]
//                val addressLines = (0..address.maxAddressLineIndex).map { index ->
//                    address.getAddressLine(index)
//                }
//                addressLines.joinToString("\n")
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("Geocoder Error", "Error fetching address", e)
//            null
//        }
//    }
//}



