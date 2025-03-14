package com.ms_seguridad.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ms_seguridad.entity.UsersEntity;

import reactor.core.publisher.Mono;

@Repository
public interface UsersRepository extends ReactiveCrudRepository<UsersEntity, Integer> {    
    @Query(Queries.USERS.GET_USERS_BY_USERNAME)
    Mono<UsersEntity> findUserByUserName(@Param("USERNAME") String USERNAME);
}
