package mabdelfattahm.githublister.api

import mabdelfattahm.githublister.core.entity.RepositoryBranches
import mabdelfattahm.githublister.core.usecase.ListUserRepositories
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.util.*

/**
 * Web controller exposing the application endpoints.
 */
@RestController
@RequestMapping("/github-lister/v1")
class ListerApi(@Autowired private val listRepoUsecase: ListUserRepositories) {

    /**
     * Lists repositories of a given username.
     *
     * @param username Username.
     * @return JSON mapping of user repositories that are not forks with branches.
     */
    @GetMapping("/repositories/{username}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRepositories(@PathVariable("username") username: String): Flux<RepositoryBranches> =
        listRepoUsecase.execute(username)
}