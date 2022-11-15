package mabdelfattahm.githublister.core.interactor

import mabdelfattahm.githublister.core.entity.Repository
import mabdelfattahm.githublister.core.error.UserNotFoundError
import reactor.core.publisher.Flux

/**
 * Abstraction for repositories datasource.
 */
interface Repositories {
    /**
     * Find repositories by username.
     *
     * @return Repositories reactive stream.
     */
    @Throws(UserNotFoundError::class)
    fun byUsername(username: String): Flux<Repository>
}