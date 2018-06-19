package com.sohu.mp.sharingplan.service;

import com.sohu.mp.sharingplan.model.MpProfile;

import java.util.Date;

public interface MulctService {

    boolean canLockMulct(String passport);

    void dealMulct(MpProfile mpProfile, String operator, Date periodDay, String reason);

}
