package com.cowork.web.rest;

import com.cowork.Application;
import com.cowork.domain.Fav;
import com.cowork.repository.FavRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FavResource REST controller.
 *
 * @see FavResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FavResourceTest {


    @Inject
    private FavRepository favRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFavMockMvc;

    private Fav fav;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FavResource favResource = new FavResource();
        ReflectionTestUtils.setField(favResource, "favRepository", favRepository);
        this.restFavMockMvc = MockMvcBuilders.standaloneSetup(favResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        fav = new Fav();
    }

    @Test
    @Transactional
    public void createFav() throws Exception {
        int databaseSizeBeforeCreate = favRepository.findAll().size();

        // Create the Fav

        restFavMockMvc.perform(post("/api/favs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fav)))
                .andExpect(status().isCreated());

        // Validate the Fav in the database
        List<Fav> favs = favRepository.findAll();
        assertThat(favs).hasSize(databaseSizeBeforeCreate + 1);
        Fav testFav = favs.get(favs.size() - 1);
    }

    @Test
    @Transactional
    public void getAllFavs() throws Exception {
        // Initialize the database
        favRepository.saveAndFlush(fav);

        // Get all the favs
        restFavMockMvc.perform(get("/api/favs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(fav.getId().intValue())));
    }

    @Test
    @Transactional
    public void getFav() throws Exception {
        // Initialize the database
        favRepository.saveAndFlush(fav);

        // Get the fav
        restFavMockMvc.perform(get("/api/favs/{id}", fav.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(fav.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFav() throws Exception {
        // Get the fav
        restFavMockMvc.perform(get("/api/favs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFav() throws Exception {
        // Initialize the database
        favRepository.saveAndFlush(fav);

		int databaseSizeBeforeUpdate = favRepository.findAll().size();

        // Update the fav

        restFavMockMvc.perform(put("/api/favs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fav)))
                .andExpect(status().isOk());

        // Validate the Fav in the database
        List<Fav> favs = favRepository.findAll();
        assertThat(favs).hasSize(databaseSizeBeforeUpdate);
        Fav testFav = favs.get(favs.size() - 1);
    }

    @Test
    @Transactional
    public void deleteFav() throws Exception {
        // Initialize the database
        favRepository.saveAndFlush(fav);

		int databaseSizeBeforeDelete = favRepository.findAll().size();

        // Get the fav
        restFavMockMvc.perform(delete("/api/favs/{id}", fav.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Fav> favs = favRepository.findAll();
        assertThat(favs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
