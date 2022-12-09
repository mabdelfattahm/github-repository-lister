package mabdelfattahm.githublister.core.error

/**
 * Repository Retrieval error.
 */
open class RepositoryRetrievalError(username: String, reason: String? = null, ex: Throwable) :
    GenericDomainError("Retrieving repositories for user $username failed. Cause: $reason", ex)