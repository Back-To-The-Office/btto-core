package com.btto.core.dao;

import com.btto.core.domain.Participant;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDaoImpl extends AbstractJpaDaoImpl<Participant> implements AbstractJpaDao<Participant> {

    public ParticipantDaoImpl() {
        super(Participant.class);
    }

}
