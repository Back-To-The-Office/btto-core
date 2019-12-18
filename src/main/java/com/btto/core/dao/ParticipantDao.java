package com.btto.core.dao;

import com.btto.core.domain.Participant;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDao extends AbstractJpaDao<Participant> {

    public ParticipantDao() {
        super(Participant.class);
    }

}
