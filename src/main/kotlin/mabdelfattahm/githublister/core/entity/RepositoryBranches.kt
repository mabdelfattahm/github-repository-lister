package mabdelfattahm.githublister.core.entity

/**
 * Complete repository entity with a list of its branches.
 * @param name Repository name.
 * @param owner Repository owner.
 * @param branches List of repository branches.
 */
data class RepositoryBranches(val name: String, val owner: String, val branches: List<Branch>)