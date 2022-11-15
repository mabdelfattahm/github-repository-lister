package mabdelfattahm.githublister.api

import mabdelfattahm.githublister.core.entity.Branch
import mabdelfattahm.githublister.core.entity.Repository
import mabdelfattahm.githublister.core.entity.RepositoryBranches
import mabdelfattahm.githublister.core.error.UserNotFoundError
import mabdelfattahm.githublister.core.interactor.Branches
import mabdelfattahm.githublister.core.interactor.Repositories
import mabdelfattahm.githublister.core.usecase.ListUserRepositories
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@WebFluxTest(ListerApi::class)
@Import(ListUserRepositories::class)
@Suppress("ReactiveStreamsUnusedPublisher")
class ListerApiTests(@Autowired val webclient: WebTestClient) {

    @MockkBean
    private lateinit var repositories: Repositories

    @MockkBean
    private lateinit var branches: Branches

    @Test
    fun userWithNoRepositoriesReturns200WithEmptyList() {
        every { repositories.byUsername(any()) } returns Flux.empty()
        webclient
            .get()
            .uri("/github-lister/v1/repositories/username")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(RepositoryBranches::class.java)
            .value<WebTestClient.ListBodySpec<RepositoryBranches>> { assertEquals(0, it.size) }
    }

    @Test
    fun userWithRepositoryThatHasNoBranchesReturns200WithRepositoryAndEmptyBranches() {
        every { repositories.byUsername(any()) } returns Flux.just(Repository("repo","owner", false))
        every { branches.byRepository(any(), any()) } returns Flux.empty()
        webclient
            .get()
            .uri("/github-lister/v1/repositories/username")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(RepositoryBranches::class.java)
            .value<WebTestClient.ListBodySpec<RepositoryBranches>> {
                assertEquals(1, it.size)
                val repo = it.first()
                assertEquals("repo", repo.name)
                assertEquals("owner", repo.owner)
                assertEquals(0, repo.branches.size)
            }
    }

    @Test
    fun userWithRepositoryThatHasBranchesReturns200WithRepositoryWithBranches() {
        every { repositories.byUsername(any()) } returns Flux.just(Repository("repo","owner", false))
        every { branches.byRepository(any(), any()) } returns Flux.just(Branch("main","sha"))
        webclient
            .get()
            .uri("/github-lister/v1/repositories/username")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(RepositoryBranches::class.java)
            .value<WebTestClient.ListBodySpec<RepositoryBranches>> {
                assertEquals(1, it.size)
                val repo = it.first()
                assertEquals("repo", repo.name)
                assertEquals("owner", repo.owner)
                assertEquals(1, repo.branches.size)
                val branch = repo.branches.first()
                assertEquals("main", branch.name)
                assertEquals("sha", branch.lastCommitSHA)
            }
    }

    @Test
    fun unknownUsernameReturns404WithErrorResponse() {
        val username = "username"
        every { repositories.byUsername(username) } throws UserNotFoundError(username)
        webclient
            .get()
            .uri("/github-lister/v1/repositories/$username")
            .exchange()
            .expectStatus()
            .is4xxClientError
            .expectStatus()
            .value { assertEquals(404, it) }
            .expectBody(RestExceptionHandler.ErrorResponse::class.java)
            .value {
                assertEquals(404, it.status)
                assertEquals("User $username not found", it.message)
            }
    }

    @Test
    fun unknownResponseTypeReturns406WithErrorResponse() {
        webclient
            .get()
            .uri("/github-lister/v1/repositories/any")
            .accept(MediaType.APPLICATION_XML)
            .exchange()
            .expectStatus()
            .is4xxClientError
            .expectStatus()
            .value { assertEquals(406, it) }
            .expectBody(RestExceptionHandler.ErrorResponse::class.java)
            .value {
                assertEquals(406, it.status)
                assertEquals("Response type not supported", it.message)
            }
    }
}