package mabdelfattahm.githublister.core.entity

/**
 * Git Repository entity.
 * @param name Repository name.
 * @param owner Repository owner.
 * @param isFork Is the repository a fork or not.
 */
data class Repository(val name: String, val owner: String, val isFork: Boolean)
