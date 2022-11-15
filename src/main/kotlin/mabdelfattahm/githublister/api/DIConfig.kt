package mabdelfattahm.githublister.api

import mabdelfattahm.githublister.core.interactor.Branches
import mabdelfattahm.githublister.core.interactor.Repositories
import mabdelfattahm.githublister.core.usecase.ListUserRepositories
import mabdelfattahm.githublister.data.source.GithubBranchesRemoteSource
import mabdelfattahm.githublister.data.source.GithubRepositoriesRemoteSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

/**
 * Contains bean configurations for Spring application.
 */
@Configuration
class DIConfig {

    /**
     * Initializes GitHub webclient.
     *
     * @param baseUrl Base URL for GitHub API. Configurable using github.api.baseUrl
     * @param token Access token for GitHub API. Configurable using github.api.accessToken
     * @param mediaType Default media type to use for all requests. Configurable using github.api.mediaType
     */
    @Bean
    fun client(
        @Value("\${github.api.baseUrl}") baseUrl: String,
        @Value("\${github.api.accessToken}") token: String,
        @Value("\${github.api.mediaType}") mediaType: String
    ): WebClient =
        WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, mediaType)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .build()

    /**
     * Initializes GitHub remote repositories source.
     *
     * @param webClient Preconfigured Webclient instance.
     */
    @Bean
    fun repositories(webClient: WebClient): Repositories = GithubRepositoriesRemoteSource(webClient)

    /**
     * Initializes GitHub remote branches source.
     *
     * @param webClient Preconfigured Webclient instance.
     */
    @Bean
    fun branches(webClient: WebClient): Branches = GithubBranchesRemoteSource(webClient)

    /**
     * Initializes ListUserRepositories usecase to be used in web controllers.
     *
     * @param repositories Repositories datasource implementation.
     * @param branches Branches datasource implementation.
     */
    @Bean
    fun listRepositoriesUsecase(repositories: Repositories, branches: Branches): ListUserRepositories =
        ListUserRepositories(repositories, branches)
}