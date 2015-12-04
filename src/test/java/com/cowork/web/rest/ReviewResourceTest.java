package com.cowork.web.rest;

import com.cowork.Application;
import com.cowork.domain.Review;
import com.cowork.repository.ReviewRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ReviewResource REST controller.
 *
 * @see ReviewResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ReviewResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = dateTimeFormatter.format(DEFAULT_DATE);

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restReviewMockMvc;

    private Review review;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReviewResource reviewResource = new ReviewResource();
        ReflectionTestUtils.setField(reviewResource, "reviewRepository", reviewRepository);
        this.restReviewMockMvc = MockMvcBuilders.standaloneSetup(reviewResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        review = new Review();
        review.setDescription(DEFAULT_DESCRIPTION);
        review.setDate(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createReview() throws Exception {
        int databaseSizeBeforeCreate = reviewRepository.findAll().size();

        // Create the Review

        restReviewMockMvc.perform(post("/api/reviews")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review)))
                .andExpect(status().isCreated());

        // Validate the Review in the database
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(databaseSizeBeforeCreate + 1);
        Review testReview = reviews.get(reviews.size() - 1);
        assertThat(testReview.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReview.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void getAllReviews() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Get all the reviews
        restReviewMockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)));
    }

    @Test
    @Transactional
    public void getReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc.perform(get("/api/reviews/{id}", review.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(review.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get("/api/reviews/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

		int databaseSizeBeforeUpdate = reviewRepository.findAll().size();

        // Update the review
        review.setDescription(UPDATED_DESCRIPTION);
        review.setDate(UPDATED_DATE);

        restReviewMockMvc.perform(put("/api/reviews")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review)))
                .andExpect(status().isOk());

        // Validate the Review in the database
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(databaseSizeBeforeUpdate);
        Review testReview = reviews.get(reviews.size() - 1);
        assertThat(testReview.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReview.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void deleteReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

		int databaseSizeBeforeDelete = reviewRepository.findAll().size();

        // Get the review
        restReviewMockMvc.perform(delete("/api/reviews/{id}", review.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(databaseSizeBeforeDelete - 1);
    }
}
