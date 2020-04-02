package com.btto.core.service;

import java.util.Optional;

public interface AbstractEntityService<T> {
    Optional<T> find(Integer id);
}
