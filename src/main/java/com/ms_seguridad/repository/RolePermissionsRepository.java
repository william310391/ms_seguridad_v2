package com.ms_seguridad.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionsRepository extends ReactiveCrudRepository<RolePermissionsRepository, Integer>  {
    
}
