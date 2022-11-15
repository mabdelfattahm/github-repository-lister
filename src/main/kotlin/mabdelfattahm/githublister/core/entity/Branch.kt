package mabdelfattahm.githublister.core.entity

/**
 * Git Branch entity.
 * @param name Branch name.
 * @param lastCommitSHA Last commit SHA.
 */
data class Branch(val name: String, val lastCommitSHA: String)
