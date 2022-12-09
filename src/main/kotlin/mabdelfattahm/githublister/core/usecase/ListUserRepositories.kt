package mabdelfattahm.githublister.core.usecase

import mabdelfattahm.githublister.core.entity.RepositoryBranches
import mabdelfattahm.githublister.core.error.BranchRetrievalError
import mabdelfattahm.githublister.core.error.RepositoryRetrievalError
import mabdelfattahm.githublister.core.error.UserNotFoundError
import mabdelfattahm.githublister.core.interactor.Branches
import mabdelfattahm.githublister.core.interactor.Repositories
import reactor.core.publisher.Flux

/**
 * List user repositories (which are not forks) with branches usecase.

 * @param repositories Repositories datasource.
 * @param branches Branches datasource.
 */
class ListUserRepositories(private val repositories: Repositories, private val branches: Branches) {

    /**
     * Run the use case
     * @param username Git username.
     * @return RepositoryWithBranches reactive stream.
     */
    fun execute(username: String): Flux<RepositoryBranches> =
        repositories.byUsername(username)
            .filter { !it.isFork }
            .onErrorMap({ it !is UserNotFoundError }, { RepositoryRetrievalError(username, it.message, it) })
            .flatMap { repo ->
                branches.byRepository(repo.owner, repo.name)
                    .collectList()
                    .onErrorMap { BranchRetrievalError(repo.name, it.message, it) }
                    .map { branches -> RepositoryBranches(repo.name, repo.owner, branches) }
            }
}