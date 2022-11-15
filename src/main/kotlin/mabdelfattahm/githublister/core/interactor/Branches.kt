package mabdelfattahm.githublister.core.interactor

import mabdelfattahm.githublister.core.entity.Branch
import reactor.core.publisher.Flux

/**
 * Abstraction for branches datasource.
 */
interface Branches {
    /**
     * Find branches of a repository.
     *
     * @param owner Repository owner.
     * @param name Repository name.
     * @return Branches reactive stream.
     */
    fun byRepository(owner: String, name: String): Flux<Branch>
}