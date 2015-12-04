package com.cowork.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cowork.domain.Review;
import com.cowork.repository.ReviewRepository;
import com.cowork.web.rest.util.HeaderUtil;
import com.cowork.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);

    @Inject
    private ReviewRepository reviewRepository;

    /**
     * POST  /reviews -> Create a new review.
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> createReview(@RequestBody Review review) throws URISyntaxException {
        log.debug("REST request to save Review : {}", review);
        if (review.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new review cannot already have an ID").body(null);
        }
        Review result = reviewRepository.save(review);
        return ResponseEntity.created(new URI("/api/reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("review", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reviews -> Updates an existing review.
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> updateReview(@RequestBody Review review) throws URISyntaxException {
        log.debug("REST request to update Review : {}", review);
        if (review.getId() == null) {
            return createReview(review);
        }
        Review result = reviewRepository.save(review);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("review", review.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reviews -> get all the reviews.
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Review>> getAllReviews(Pageable pageable)
        throws URISyntaxException {
        Page<Review> page = reviewRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reviews");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reviews/:id -> get the "id" review.
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        return Optional.ofNullable(reviewRepository.findOne(id))
            .map(review -> new ResponseEntity<>(
                review,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reviews/:id -> delete the "id" review.
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.debug("REST request to delete Review : {}", id);
        reviewRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("review", id.toString())).build();
    }
}
