package dev.michaelfarrant.simplestore.cataloguemgmt.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.ItemNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStateException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps catalogue management domain errors to RFC 9457 problem responses.
 */
@RestControllerAdvice(basePackages = "dev.michaelfarrant.simplestore.cataloguemgmt")
public class CatalogueManagementExceptionHandler {

    @ExceptionHandler(ListingNotFoundException.class)
    public ProblemDetail listingNotFound(ListingNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    // A referenced item is part of the request body rather than the resource path, so 422 rather than 404
    @ExceptionHandler(ItemNotFoundException.class)
    public ProblemDetail itemNotFound(ItemNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage());
    }

    @ExceptionHandler(ListingStateException.class)
    public ProblemDetail invalidListingState(ListingStateException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail concurrentModification(OptimisticLockingFailureException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The resource was modified concurrently, please retry");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail duplicate(DuplicateKeyException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "A resource with the same unique key already exists");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail invalidArgument(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
