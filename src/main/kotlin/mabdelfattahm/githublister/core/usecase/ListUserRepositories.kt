package mabdelfattahm.githublister.core.usecase

import mabdelfattahm.githublister.core.entity.Branch
import mabdelfattahm.githublister.core.entity.RepositoryBranches
import mabdelfattahm.githublister.core.error.GenericDomainError
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
     * Empty branch instance to preserve repositories with no branches
     */
    private val emptyBranch = Branch("empty", "empty")

    /**
     * Run the use case
     * @param username Git username.
     * @return RepositoryWithBranches reactive stream.
     */
    fun execute(username: String): Flux<RepositoryBranches> =
        repositories.byUsername(username)
            .filter { !it.isFork }
            .onErrorMap (
                { it !is GenericDomainError },
                { GenericDomainError("Retrieving repositories for user $username failed. Cause: ${it.message}", it) }
            )
            .concatMap { repo ->
                branches.byRepository(repo.owner, repo.name)
                    .startWith(emptyBranch)
                    .map { branch -> Pair(repo, branch) }
                    .onErrorMap (
                        { it !is GenericDomainError },
                        { GenericDomainError("Retrieving branches for repository ${repo.name} failed. Cause: ${it.message}", it) }
                    )
            }
            .bufferUntilChanged { (repo, _) -> repo.name }
            .map { list ->
                val (repo, _) = list.first()
                RepositoryBranches(
                    repo.name,
                    repo.owner,
                    list.filterNot { (_, branch) -> branch == emptyBranch }.map { (_, branch) -> branch }
                )
            }

}