package com.btto.core.dao;

import com.btto.core.domain.Desk;
import org.springframework.stereotype.Component;

@Component
public class DeskDaoImpl extends AbstractJpaDaoImpl<Desk> implements DeskDao {

    public DeskDaoImpl() {
        super(Desk.class);
    }
}
