package com.ms_seguridad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms_seguridad.entity.UsersEntity;
import com.ms_seguridad.repository.UsersRepository;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/prueba")
public class PruebaController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/data")
    public Flux<UsersEntity> data() {

        // UsersEntity aa= UsersEntity.builder()
        // .username("")
        // .build();
        return usersRepository.findAll();
    }

}
