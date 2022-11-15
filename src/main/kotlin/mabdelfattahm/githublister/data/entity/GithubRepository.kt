package mabdelfattahm.githublister.data.entity

import com.jayway.jsonpath.JsonPath

/**
 * GitHub repository model.
 *
 * @param repo Map of attributes.
 */
class GithubRepository(private val repo: Map<String, Any>) {

    /**
     * Extracts branch name from the map of attributes.
     */
    fun name(): String = JsonPath.read(repo, "$.name")

    /**
     * Extracts repository fork status from the map of attributes.
     */
    fun isFork(): Boolean = JsonPath.read(repo, "$.fork")

    /**
     * Extracts owner login handler from the map of attributes.
     */
    fun owner(): String = JsonPath.read(repo, "$.owner.login")

}