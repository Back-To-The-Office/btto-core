package com.btto.core.service;

import com.btto.core.dao.AbstractJpaDao;
import com.btto.core.domain.WorkDay;
import org.springframework.stereotype.Service;

@Service
public class WorkDayServiceImpl extends AbstractEntityServiceImpl<WorkDay> implements WorkDayService {
    public WorkDayServiceImpl(AbstractJpaDao<WorkDay> dao) {
        super(dao);
    }
}
