package com.vjoon.se.core.repository;

import com.vjoon.se.core.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by guido on 08.09.15.
 */
public interface PersonRepository extends JpaRepository<Person,Long> {
    Person findByFirstNameEquals(String username);
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Page<Person> findAll(Pageable pageable);

    @Override
    @PostAuthorize("returnObject.firstName == principal.username or hasRole('ROLE_ADMIN')")
    Person findOne(Long aLong);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<Person> findByFirstNameLike(@Param("firstName") String firstName);
}
