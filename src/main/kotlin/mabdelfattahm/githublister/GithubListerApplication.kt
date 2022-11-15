package mabdelfattahm.githublister

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GithubListerApplication

fun main(args: Array<String>) {
    runApplication<GithubListerApplication>(*args)
}
