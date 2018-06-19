package com.sohu.mp.sharingplan.service.impl;

import com.sohu.mp.sharingplan.dao.accounts.MpProfileMapper;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.service.MpProfileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MpProfileServiceImpl implements MpProfileService{

    @Resource
    private MpProfileMapper mpProfileMapper;


    @Override
    public MpProfile getByPassport(String passport) {
        return mpProfileMapper.getByPassport(passport);
    }
}
