package com.cowork.repository;

import com.cowork.domain.Fav;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Fav entity.
 */
public interface FavRepository extends JpaRepository<Fav,Long> {

    @Query("select fav from Fav fav where fav.user.login = ?#{principal.username}")
    List<Fav> findByUserIsCurrentUser();


    @Query("select fav from Fav fav where fav.user.login = ?#{principal.username}")
    Page<Fav> findAllForCurrentUser(Pageable pageable);

}
