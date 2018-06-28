package com.sohu.mp.sharingplan.service;

import com.sohu.mp.sharingplan.model.MpProfile;

import java.util.Date;

public interface MulctService {

    boolean canLockBaseMulct(String passport);

    boolean canLockBonusMulct(String passport);

    void dealBaseMulct(MpProfile mpProfile, String operator, Date periodDay, String reason);

    void dealBonusMulct(MpProfile mpProfile, String operator, String code, String reason);

}
