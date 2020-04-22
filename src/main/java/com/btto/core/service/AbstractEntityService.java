package com.btto.core.service;


import java.util.Optional;

public interface AbstractEntityService<Entity> {
    Optional<Entity> find(Integer id);
}
