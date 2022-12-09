package mabdelfattahm.githublister.core.error

/**
 * Branch Retrieval error.
 */
open class BranchRetrievalError(name: String, reason: String? = null, ex: Throwable) :
    GenericDomainError("Retrieving branches for repository $name failed. Cause: $reason", ex)