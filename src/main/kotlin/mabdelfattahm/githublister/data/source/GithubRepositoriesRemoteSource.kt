package mabdelfattahm.githublister.data.source

import mabdelfattahm.githublister.core.entity.Repository
import mabdelfattahm.githublister.core.error.UserNotFoundError
import mabdelfattahm.githublister.core.interactor.Repositories
import mabdelfattahm.githublister.data.entity.GithubRepository
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Implementation of repositories datasource using GitHub APIs.

 * @param webclient Pre-configured webclient to call GitHub's endpoints.
 */
class GithubRepositoriesRemoteSource(private val webclient: WebClient) : Repositories, GithubRemoteSource() {

    override fun byUsername(username: String): Flux<Repository> =
        pagesToFlux { fetchUserRepositories(username, it) }.map { it.toDomainEntity() }

    /**
     * Extension function that maps GitHub repository to domain repository.
     */
    private fun GithubRepository.toDomainEntity(): Repository =
        Repository(this.name(), this.owner(), this.isFork())

    /**
     * Fetches user repositories from GitHub.
     * @param username GitHub username.
     * @param page Page number.
     */
    private fun fetchUserRepositories(username: String, page: Int): Flux<GithubRepository> =
        webclient
            .get()
            .uri("/users/{username}/repos?page={page}&type=all", username, page)
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }, { Mono.error(UserNotFoundError(username)) })
            .bodyToFlux(MAP_TYPE_REFERENCE)
            .map(::GithubRepository)
}