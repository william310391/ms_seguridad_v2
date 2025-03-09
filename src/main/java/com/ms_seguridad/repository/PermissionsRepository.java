package com.ms_seguridad.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ms_seguridad.entity.PermissionsEntity;

import reactor.core.publisher.Flux;

@Repository
public interface PermissionsRepository extends ReactiveCrudRepository<PermissionsEntity, Integer>  {
    @Query(Queries.PERMISSION.GET_PERMISSION_BY_IDROLE)
    Flux<PermissionsEntity> findPermissionByIdRole(@Param("role_id") List<Integer> idRole);
}
