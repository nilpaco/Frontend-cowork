package com.cowork.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cowork.domain.Fav;
import com.cowork.domain.Space;
import com.cowork.domain.User;
import com.cowork.repository.FavRepository;
import com.cowork.repository.SpaceRepository;
import com.cowork.repository.UserRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * REST controller for managing Space.
 */
@RestController
@RequestMapping("/api")
public class SpaceResource {

    private final Logger log = LoggerFactory.getLogger(SpaceResource.class);

    @Inject
    private SpaceRepository spaceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private FavRepository favRepository;

    /**
     * POST  /spaces -> Create a new space.
     */
    @RequestMapping(value = "/spaces",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> createSpace(@RequestBody Space space) throws URISyntaxException {
        log.debug("REST request to save Space : {}", space);
        if (space.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new space cannot already have an ID").body(null);
        }
        Space result = spaceRepository.save(space);
        return ResponseEntity.created(new URI("/api/spaces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("space", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /spaces -> Updates an existing space.
     */
    @RequestMapping(value = "/spaces",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> updateSpace(@RequestBody Space space) throws URISyntaxException {
        log.debug("REST request to update Space : {}", space);
        if (space.getId() == null) {
            return createSpace(space);
        }
        Space result = spaceRepository.save(space);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("space", space.getId().toString()))
            .body(result);
    }

    /**
     * GET  /spaces -> get all the spaces.
     */
    @RequestMapping(value = "/spaces",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Space>> getAllSpaces(Pageable pageable)
        throws URISyntaxException {
        Page<Space> page = spaceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spaces");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /spaceByUser -> get all the spaces from a user.
     */
    @RequestMapping(value = "/spaceByUser",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Space>> getAll(Pageable pageable)
        throws URISyntaxException {
        Page<Space> page;
        if (SecurityUtils.isUserInRole(AuthoritiesConstants.ADMIN)) {
            page = spaceRepository.findAll(pageable);
        } else {
            page = spaceRepository.findAllForCurrentUser(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spacesUser");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * GET  /spaces/:id -> get the "id" space.
     */
    @RequestMapping(value = "/spaces/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> getSpace(@PathVariable Long id) {
        log.debug("REST request to get Space : {}", id);
        return Optional.ofNullable(spaceRepository.findOneWithEagerRelationships(id))
            .map(space -> new ResponseEntity<>(
                space,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /spaces/:id -> delete the "id" space.
     */
    @RequestMapping(value = "/spaces/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        log.debug("REST request to delete Space : {}", id);
        spaceRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("space", id.toString())).build();
    }

    @RequestMapping(value = "/favspace/{id}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> createSpace(@PathVariable Long idSpace) throws URISyntaxException {
        log.debug("REST request to add a FavSpace : {}", idSpace);

        Space space = spaceRepository.findOne(idSpace);

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        Fav fav = new Fav();
        fav.setSpace(space);
        fav.setUser(user);

        favRepository.save(fav);


        return ResponseEntity.created(new URI("/api/favs/" + fav.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("fav", fav.getId().toString()))
            .body(space);
    }

}
