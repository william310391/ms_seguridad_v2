package com.ms_seguridad.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ms_seguridad.entity.RolesEntity;

@Repository
public interface RolesRepository extends ReactiveCrudRepository<RolesEntity, Integer>  {
    
}
