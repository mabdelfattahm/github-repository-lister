package mabdelfattahm.githublister.core

import io.mockk.every
import io.mockk.mockk
import mabdelfattahm.githublister.core.entity.Branch
import mabdelfattahm.githublister.core.entity.Repository
import mabdelfattahm.githublister.core.error.BranchRetrievalError
import mabdelfattahm.githublister.core.error.RepositoryRetrievalError
import mabdelfattahm.githublister.core.error.UserNotFoundError
import mabdelfattahm.githublister.core.interactor.Branches
import mabdelfattahm.githublister.core.interactor.Repositories
import mabdelfattahm.githublister.core.usecase.ListUserRepositories
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@Suppress("ReactiveStreamsUnusedPublisher")
class ListUserRepositoriesTests {
    private lateinit var usecase: ListUserRepositories
    private lateinit var repositories: Repositories
    private lateinit var branches: Branches

    @BeforeEach
    fun setup() {
        repositories = mockk(relaxed = true)
        branches = mockk(relaxed = true)
        usecase = ListUserRepositories(repositories, branches)
    }

    @Test
    fun userWithNoRepositoriesReturnsEmptyFlux() {
        every { repositories.byUsername(any()) } returns Flux.empty()
        StepVerifier
            .create(usecase.execute(""))
            .expectComplete()
            .verify()
    }

    @Test
    fun userWithOnlyForkRepositoriesReturnsEmptyFlux() {
        every { repositories.byUsername(any()) } returns Flux.just(Repository("repo", "owner", true))
        StepVerifier
            .create(usecase.execute(""))
            .expectComplete()
            .verify()
    }

    @Test
    fun unknownUsernameProducesUserNotFoundError() {
        val username = "username"
        every { repositories.byUsername(username) } returns Flux.error(UserNotFoundError(username))
        StepVerifier
            .create(usecase.execute(username))
            .expectErrorMatches { it is UserNotFoundError }
            .verify()
    }

    @Test
    fun errorDuringBranchInfoRetrievalReturnsCorrectErrorMessage() {
        val username = "username"
        val repo = Repository("repo", "owner", false)
        val branch = Branch("main", "sha")
        every { repositories.byUsername(username) } returns Flux.just(repo)
        every { branches.byRepository(any(), any()) } returns Flux.just(branch).startWith(Flux.error(Exception("error")))
        StepVerifier
            .create(usecase.execute(username).log())
            .expectErrorMatches { it is BranchRetrievalError }
            .verify()
    }

    @Test
    fun errorDuringRepositoryInfoRetrievalReturnsCorrectErrorMessage() {
        val username = "user"
        val repo = Repository("repo", "owner", false)
        val branch = Branch("main", "sha")
        every { repositories.byUsername(username) } returns Flux.just(repo).startWith(Flux.error(Exception("error")))
        every { branches.byRepository(any(), any()) } returns Flux.just(branch)
        StepVerifier
            .create(usecase.execute(username).log())
            .expectErrorMatches { it is RepositoryRetrievalError }
            .verify()
    }
}