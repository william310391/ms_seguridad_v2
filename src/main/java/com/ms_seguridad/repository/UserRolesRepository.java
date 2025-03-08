package com.ms_seguridad.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ms_seguridad.entity.UserRolesEntity;

@Repository
public interface UserRolesRepository extends ReactiveCrudRepository<UserRolesEntity, Integer> {
    
}
