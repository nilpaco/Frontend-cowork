package com.cowork.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cowork.domain.Fav;
import com.cowork.repository.FavRepository;
import com.cowork.security.AuthoritiesConstants;
import com.cowork.security.SecurityUtils;
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
 * REST controller for managing Fav.
 */
@RestController
@RequestMapping("/api")
public class FavResource {

    private final Logger log = LoggerFactory.getLogger(FavResource.class);

    @Inject
    private FavRepository favRepository;

    /**
     * POST  /favs -> Create a new fav.
     */
    @RequestMapping(value = "/favs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fav> createFav(@RequestBody Fav fav) throws URISyntaxException {
        log.debug("REST request to save Fav : {}", fav);
        if (fav.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new fav cannot already have an ID").body(null);
        }
        Fav result = favRepository.save(fav);
        return ResponseEntity.created(new URI("/api/favs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("fav", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /favs -> Updates an existing fav.
     */
    @RequestMapping(value = "/favs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fav> updateFav(@RequestBody Fav fav) throws URISyntaxException {
        log.debug("REST request to update Fav : {}", fav);
        if (fav.getId() == null) {
            return createFav(fav);
        }
        Fav result = favRepository.save(fav);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("fav", fav.getId().toString()))
            .body(result);
    }

    /**
     * GET  /favs -> get all the favs.
     */
    @RequestMapping(value = "/favs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Fav>> getAllFavs(Pageable pageable)
        throws URISyntaxException {
        Page<Fav> page = favRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/favs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /favs/:id -> get the "id" fav.
     */
    @RequestMapping(value = "/favs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fav> getFav(@PathVariable Long id) {
        log.debug("REST request to get Fav : {}", id);
        return Optional.ofNullable(favRepository.findOne(id))
            .map(fav -> new ResponseEntity<>(
                fav,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /favs/:id -> delete the "id" fav.
     */
    @RequestMapping(value = "/favs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFav(@PathVariable Long id) {
        log.debug("REST request to delete Fav : {}", id);
        favRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("fav", id.toString())).build();
    }

    /**
     * GET  /spaceByUser -> get all favs from a user.
     */
    @RequestMapping(value = "/favsByUser",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Fav>> getAll(Pageable pageable)
        throws URISyntaxException {
        Page<Fav> page;
        if (SecurityUtils.isUserInRole(AuthoritiesConstants.ADMIN)) {
            page = favRepository.findAll(pageable);
        } else {
            page = favRepository.findAllForCurrentUser(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/favsByUser");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
