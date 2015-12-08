package com.cowork.repository;

import com.cowork.domain.Space;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Space entity.
 */
public interface SpaceRepository extends JpaRepository<Space,Long> {

    @Query("select space from Space space where space.user.login = ?#{principal.username}")
    List<Space> findByUserIsCurrentUser();

    @Query("select space from Space space where space.user.login = ?#{principal.username}")
    Page<Space> findAllForCurrentUser(Pageable pageable);

    @Query("select distinct space from Space space left join fetch space.services")
    List<Space> findAllWithEagerRelationships();

    @Query("select space from Space space left join fetch space.services where space.id =:id")
    Space findOneWithEagerRelationships(@Param("id") Long id);

}
