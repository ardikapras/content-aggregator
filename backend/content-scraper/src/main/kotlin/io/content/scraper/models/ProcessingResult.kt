package io.content.scraper.models

class ProcessingResult<out SUCCESS, out FAILURE> private constructor(
    private val _successValue: SUCCESS?,
    private val _failureValue: FAILURE?,
) {
    val success: Boolean
        get() = _failureValue == null

    val failure: Boolean
        get() = _failureValue != null

    @Suppress("UNCHECKED_CAST")
    val successValue: SUCCESS
        get() =
            if (success) {
                _successValue as SUCCESS
            } else {
                error("Can't get a success value from a failed result")
            }

    @Suppress("UNCHECKED_CAST")
    val failureValue: FAILURE
        get() =
            if (success) {
                error("Can't get a failure value from a successful result")
            } else {
                _failureValue as FAILURE
            }

    override fun toString(): String =
        if (success) {
            "success: $_successValue"
        } else {
            "failure: $_failureValue"
        }

    companion object {
        fun <S, F> success(value: S): ProcessingResult<S, F> = ProcessingResult(value, null)

        fun <F> success(): ProcessingResult<Unit, F> = ProcessingResult(Unit, null)

        fun <S, F : Any> failure(value: F): ProcessingResult<S, F> = ProcessingResult(null, value)
    }
}
