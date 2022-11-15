package mabdelfattahm.githublister.data.source

import org.springframework.core.ParameterizedTypeReference
import reactor.core.publisher.Flux
import reactor.util.context.Context

/**
 * Base class for data sources using GitHub APIs.
 */
abstract class GithubRemoteSource {

    /**
     * Companion object for static constants.
     */
    companion object {
        /**
         * Key to use for pages in Reactor contextual view.
         */
        const val PAGE_KEY = "page"

        /**
         * Type reference implementation to map JSON to a generic map of String keys.
         */
        @JvmStatic
        protected val MAP_TYPE_REFERENCE = object : ParameterizedTypeReference<Map<String, Any>>() {}
    }


    /**
     * Converts GitHub paginated endpoints to a reactive stream
     *  based on the count of items in every response.
     * @param retriever Mapper from page number to a flux of type T
     * @param T Inlined type.
     */
    protected inline fun <reified T> pagesToFlux(crossinline retriever: (page: Int) -> Flux<T>) =
        Flux
            .deferContextual { retriever(it.getOrEmpty<Int>(PAGE_KEY).orElse(1)) }
            .repeatWhen {
                it.handle { count, sink ->
                    val page = sink.contextView().getOrEmpty<Int>(PAGE_KEY).orElse(1)
                    if (count == 0L) sink.complete() else sink.next(Context.of(PAGE_KEY, page + 1))
                }
            }

}