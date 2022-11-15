package mabdelfattahm.githublister.core.error

/**
 * Base class for domain errors.
 */
open class GenericDomainError(override val message: String? = null, override val cause: Throwable? = null) : Throwable(message, cause)
