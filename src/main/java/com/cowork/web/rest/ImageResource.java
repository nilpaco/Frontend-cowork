package com.cowork.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cowork.domain.Image;
import com.cowork.repository.ImageRepository;
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
 * REST controller for managing Image.
 */
@RestController
@RequestMapping("/api")
public class ImageResource {

    private final Logger log = LoggerFactory.getLogger(ImageResource.class);

    @Inject
    private ImageRepository imageRepository;

    /**
     * POST  /images -> Create a new image.
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> createImage(@RequestBody Image image) throws URISyntaxException {
        log.debug("REST request to save Image : {}", image);
        if (image.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new image cannot already have an ID").body(null);
        }
        Image result = imageRepository.save(image);
        return ResponseEntity.created(new URI("/api/images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("image", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /images -> Updates an existing image.
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> updateImage(@RequestBody Image image) throws URISyntaxException {
        log.debug("REST request to update Image : {}", image);
        if (image.getId() == null) {
            return createImage(image);
        }
        Image result = imageRepository.save(image);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("image", image.getId().toString()))
            .body(result);
    }

    /**
     * GET  /images -> get all the images.
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Image>> getAllImages(Pageable pageable)
        throws URISyntaxException {
        Page<Image> page = imageRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/images");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /images/:id -> get the "id" image.
     */
    @RequestMapping(value = "/images/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> getImage(@PathVariable Long id) {
        log.debug("REST request to get Image : {}", id);
        return Optional.ofNullable(imageRepository.findOne(id))
            .map(image -> new ResponseEntity<>(
                image,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /images/:id -> delete the "id" image.
     */
    @RequestMapping(value = "/images/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        log.debug("REST request to delete Image : {}", id);
        imageRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("image", id.toString())).build();
    }
}
