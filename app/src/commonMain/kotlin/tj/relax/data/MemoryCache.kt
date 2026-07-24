package tj.relax.data

import kotlinx.datetime.Clock

class MemoryCache<T>(private val ttlMs: Long = 5 * 60_000L) {
    private var data: T? = null
    private var timestamp = 0L

    private fun nowMs() = Clock.System.now().toEpochMilliseconds()

    fun get(): T? = data?.takeIf { nowMs() - timestamp < ttlMs }
    fun set(value: T) { data = value; timestamp = nowMs() }
    fun invalidate() { data = null; timestamp = 0L }
}
