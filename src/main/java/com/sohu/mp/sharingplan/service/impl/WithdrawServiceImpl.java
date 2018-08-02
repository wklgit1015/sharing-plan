package com.sohu.mp.sharingplan.service.impl;

import com.sohu.mp.common.enums.WithdrawProgressEnum;
import com.sohu.mp.common.enums.WithdrawStatusEnum;
import com.sohu.mp.sharingplan.dao.accounts.WithdrawMapper;
import com.sohu.mp.sharingplan.dao.redis.impl.RedisLockDao;
import com.sohu.mp.sharingplan.enums.exception.WithdrawStatusCheckException;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.model.Withdraw;
import com.sohu.mp.sharingplan.model.WithdrawProgress;
import com.sohu.mp.sharingplan.service.WithdrawService;
import com.sohu.mp.sharingplan.transaction.SharingPlanTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    private static final Logger log = LoggerFactory.getLogger(WithdrawServiceImpl.class);

    @Resource
    private WithdrawMapper withdrawMapper;

    @Resource
    private SharingPlanTransaction sharingPlanTransaction;

    @Resource
    private RedisLockDao redisLockDao;

    private static final String WITHDRAW_LOCK_PREFIX = "withdraw";

    @Override
    public boolean canLockWithdraw(String passport) {
        return redisLockDao.lock(WITHDRAW_LOCK_PREFIX,passport);
    }

    @Override
    public void withdrawRollback(MpProfile mpProfile, String date,String reason) {
        long userId = mpProfile.getId();
        Withdraw withdraw = withdrawMapper.getByUserIdAndDate(userId,date);
        if(withdraw.getStatus()!=WithdrawStatusEnum.PENDING.getValue()){
            throw new WithdrawStatusCheckException("withdraw status is not pending");
        }

        WithdrawProgress withdrawProgress = new WithdrawProgress(withdraw.getId(),new Long(userId).intValue(),
                WithdrawProgressEnum.ROLLBACK.getType(),String.format(WithdrawProgressEnum.ROLLBACK.getDetail(),reason),new Date());
        sharingPlanTransaction.dealWithdrawRollback(userId,date,withdraw,withdrawProgress);
        redisLockDao.unLock(WITHDRAW_LOCK_PREFIX,mpProfile.getPassport());
    }
}
