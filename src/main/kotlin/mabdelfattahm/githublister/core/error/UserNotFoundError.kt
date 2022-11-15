package mabdelfattahm.githublister.core.error

/**
 * User not found domain error.
 */
data class UserNotFoundError(val username: String) : GenericDomainError("User $username not found")