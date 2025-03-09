package com.ms_seguridad.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ms_seguridad.entity.RolesEntity;

import reactor.core.publisher.Flux;

@Repository
public interface RolesRepository extends ReactiveCrudRepository<RolesEntity, Integer>  {
    @Query(Queries.ROLE.GET_ROLE_BY_IDUSER)
    Flux<RolesEntity> findRoleByIdUser(@Param("user_id") Integer idUser);
}
