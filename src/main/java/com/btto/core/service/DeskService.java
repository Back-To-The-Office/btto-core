package com.btto.core.service;

import com.btto.core.domain.Desk;

import javax.annotation.Nullable;

public interface DeskService extends AbstractEntityService<Desk> {

    Desk create(int roomId, String name, Integer capacity);

    Desk update(int id, @Nullable String name, int capacity);

    void delete(int id);
}
