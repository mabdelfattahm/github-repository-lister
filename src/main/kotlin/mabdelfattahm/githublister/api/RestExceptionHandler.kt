package mabdelfattahm.githublister.api

import mabdelfattahm.githublister.core.error.GenericDomainError
import mabdelfattahm.githublister.core.error.UserNotFoundError
import org.springframework.http.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * Controller advice implementation of ResponseEntityExceptionHandler
 *  to override handling rest endpoint exceptions.
 */
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * Error response DTO.
     *
     * @param status Http status code.
     * @param message Error message.
     */
    data class ErrorResponse(val status: Int, val message: String)

    override fun handleNotAcceptableStatusException(
        ex: NotAcceptableStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse(406, "Response type not supported"))
        )
    }

    /**
     * Handler for domain exception UserNotFound.
     *
     * @param ex Exception.
     */
    @ExceptionHandler
    fun handle(ex: UserNotFoundError): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(404, ex.message ?: "Unknown user"))

    /**
     * Handler for generic domain exceptions.
     *
     * @param ex Exception.
     */
    @ExceptionHandler
    fun handle(ex: GenericDomainError): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse(500, ex.message ?: ex.cause?.message ?: "Unknown error"))
}