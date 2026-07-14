package tj.relax.data

class MemoryCache<T>(private val ttlMs: Long = 5 * 60_000L) {
    private var data: T? = null
    private var timestamp = 0L

    fun get(): T? = data?.takeIf { System.currentTimeMillis() - timestamp < ttlMs }
    fun set(value: T) { data = value; timestamp = System.currentTimeMillis() }
    fun invalidate() { data = null; timestamp = 0L }
}
