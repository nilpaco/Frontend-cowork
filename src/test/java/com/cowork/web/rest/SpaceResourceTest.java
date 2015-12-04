package com.cowork.web.rest;

import com.cowork.Application;
import com.cowork.domain.Space;
import com.cowork.repository.SpaceRepository;

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
 * Test class for the SpaceResource REST controller.
 *
 * @see SpaceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SpaceResourceTest {

    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private SpaceRepository spaceRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSpaceMockMvc;

    private Space space;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SpaceResource spaceResource = new SpaceResource();
        ReflectionTestUtils.setField(spaceResource, "spaceRepository", spaceRepository);
        this.restSpaceMockMvc = MockMvcBuilders.standaloneSetup(spaceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        space = new Space();
        space.setTitle(DEFAULT_TITLE);
        space.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createSpace() throws Exception {
        int databaseSizeBeforeCreate = spaceRepository.findAll().size();

        // Create the Space

        restSpaceMockMvc.perform(post("/api/spaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(space)))
                .andExpect(status().isCreated());

        // Validate the Space in the database
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeCreate + 1);
        Space testSpace = spaces.get(spaces.size() - 1);
        assertThat(testSpace.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSpace.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSpaces() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

        // Get all the spaces
        restSpaceMockMvc.perform(get("/api/spaces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(space.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

        // Get the space
        restSpaceMockMvc.perform(get("/api/spaces/{id}", space.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(space.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSpace() throws Exception {
        // Get the space
        restSpaceMockMvc.perform(get("/api/spaces/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

		int databaseSizeBeforeUpdate = spaceRepository.findAll().size();

        // Update the space
        space.setTitle(UPDATED_TITLE);
        space.setDescription(UPDATED_DESCRIPTION);

        restSpaceMockMvc.perform(put("/api/spaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(space)))
                .andExpect(status().isOk());

        // Validate the Space in the database
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeUpdate);
        Space testSpace = spaces.get(spaces.size() - 1);
        assertThat(testSpace.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSpace.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

		int databaseSizeBeforeDelete = spaceRepository.findAll().size();

        // Get the space
        restSpaceMockMvc.perform(delete("/api/spaces/{id}", space.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeDelete - 1);
    }
}
