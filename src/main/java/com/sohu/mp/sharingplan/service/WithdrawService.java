package com.sohu.mp.sharingplan.service;

import com.sohu.mp.sharingplan.model.MpProfile;

public interface WithdrawService {

    boolean canLockWithdraw(String passport);

    void withdrawRollback(MpProfile mpProfile, String date, String reason);
}
