package mabdelfattahm.githublister.data.entity

import com.jayway.jsonpath.JsonPath

/**
 * GitHub branch model.
 *
 * @param branch Map of attributes.
 */
class GithubBranch(private val branch: Map<String, Any>) {

    /**
     * Extracts branch name from the map of attributes.
     */
    fun name(): String = JsonPath.read(branch, "$.name")

    /**
     * Extracts last commit SHA from the map of attributes.
     */

    fun lastCommitSHA(): String = JsonPath.read(branch, "$.commit.sha")
}