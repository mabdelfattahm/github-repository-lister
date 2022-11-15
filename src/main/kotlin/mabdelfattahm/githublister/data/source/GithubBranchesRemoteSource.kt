package mabdelfattahm.githublister.data.source

import mabdelfattahm.githublister.core.entity.Branch
import mabdelfattahm.githublister.core.interactor.Branches
import mabdelfattahm.githublister.data.entity.GithubBranch
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

/**
 * Implementation of branches datasource using GitHub APIs.

 * @param webclient Pre-configured webclient to call GitHub's endpoints.
 */
class GithubBranchesRemoteSource(private val webclient: WebClient) : Branches, GithubRemoteSource() {

    override fun byRepository(owner: String, name: String): Flux<Branch> =
        pagesToFlux { fetchRepositoryBranches(owner, name, it) }.map { it.toDomainEntity() }

    /**
     * Extension function that maps GitHub branch to domain branch.
     */
    private fun GithubBranch.toDomainEntity(): Branch =
        Branch(this.name(), this.lastCommitSHA())

    /**
     * Fetches repository branches from GitHub.
     * @param owner Repository owner.
     * @param name Repository name.
     * @param page Page number.
     */
    private fun fetchRepositoryBranches(owner: String, name: String, page: Int = 1): Flux<GithubBranch> =
        webclient
            .get()
            .uri("/repos/{owner}/{repo}/branches?page={page}", owner, name, page)
            .retrieve()
            .bodyToFlux(MAP_TYPE_REFERENCE)
            .map(::GithubBranch)

}