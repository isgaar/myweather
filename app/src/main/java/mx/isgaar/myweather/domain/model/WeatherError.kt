// Location: app/.../domain/model/WeatherError.kt
// Function: Typed domain error hierarchy with ERR_* identifiers ensuring every
// network, HTTP, parsing, or input failure is explicit and traceable.
// All Rights Reserved isgaar

package mx.isgaar.myweather.domain.model

sealed class WeatherError(
    val code: String,
    override val message: String
) : Exception(message) {

    class NetworkUnreachable(detail: String) : WeatherError(
        code = "ERR_NET_UNREACHABLE",
        message = detail
    )

    class HttpFailure(val status: Int, detail: String) : WeatherError(
        code = "ERR_HTTP_STATUS",
        message = "HTTP $status: $detail"
    )

    class InvalidPayload(detail: String) : WeatherError(
        code = "ERR_PARSE_INVALID",
        message = detail
    )

    class InvalidLocation(detail: String) : WeatherError(
        code = "ERR_LOCATION_INVALID",
        message = detail
    )
}
