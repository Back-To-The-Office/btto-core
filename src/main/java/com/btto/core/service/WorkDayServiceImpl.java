package com.btto.core.service;

import com.btto.core.dao.WorkDayDao;
import com.btto.core.domain.WorkDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkDayServiceImpl extends AbstractEntityServiceImpl<WorkDay, WorkDayDao> implements WorkDayService {
    @Autowired
    public WorkDayServiceImpl(WorkDayDao dao) {
        super(dao);
    }
}
