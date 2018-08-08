package com.sohu.mp.sharingplan.service.impl;

import com.sohu.mp.common.enums.StagedRightsInterestsEnum;
import com.sohu.mp.common.enums.WithdrawProgressEnum;
import com.sohu.mp.common.enums.WithdrawStatusEnum;
import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.sharingplan.dao.accounts.AssetMapper;
import com.sohu.mp.sharingplan.dao.accounts.WithdrawMapper;
import com.sohu.mp.sharingplan.dao.redis.impl.RedisLockDao;
import com.sohu.mp.sharingplan.enums.exception.WithdrawCheckException;
import com.sohu.mp.sharingplan.model.Asset;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.model.Withdraw;
import com.sohu.mp.sharingplan.model.WithdrawProgress;
import com.sohu.mp.sharingplan.service.WithdrawService;
import com.sohu.mp.sharingplan.transaction.SharingPlanTransaction;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    @Resource
    private WithdrawMapper withdrawMapper;

    @Resource
    private SharingPlanTransaction sharingPlanTransaction;

    @Resource
    private RedisLockDao redisLockDao;

    @Resource
    private WithdrawService withdrawService;

    @Resource
    private AssetMapper assetMapper;

    private static final String WITHDRAW_LOCK_PREFIX = "withdraw";

    @Override
    public boolean canLockWithdraw(String withdrawId) {
        return redisLockDao.lock(WITHDRAW_LOCK_PREFIX, withdrawId);

    }

    @Override
    public void withdrawRollback(MpProfile mpProfile, String date, String reason, String operator) {
        long userId = mpProfile.getId();
        Withdraw withdraw = withdrawMapper.getByUserIdAndDate(userId, date);
        if (withdraw == null) {
            throw new WithdrawCheckException("the withdraw record does't exist");
        }
        if (withdraw.getStatus() != WithdrawStatusEnum.PENDING.getValue()) {
            throw new WithdrawCheckException("withdraw is not on pending status");
        }
        if (!withdrawService.canLockWithdraw(String.valueOf(withdraw.getId()))) {
            throw new InvalidParameterException("withdraw is rolling back, please wait");
        }
        Asset asset = assetMapper.getByUserSourceWriteSource(userId, StagedRightsInterestsEnum.FLOW_RIGHTS_INTEREST.getSource());
        if (asset.getWithdrawingAmount().subtract(withdraw.getAmount()).compareTo(BigDecimal.valueOf(0)) < 0) {
            // 用户资产中的withdrawing amount小于正在提现的amount
            throw new WithdrawCheckException("asset withdrawing amount less than this withdrawing amount");
        }
        WithdrawProgress withdrawProgress = new WithdrawProgress(withdraw.getId(), new Long(userId).intValue(),
                WithdrawProgressEnum.ROLLBACK.getType(), WithdrawProgressEnum.ROLLBACK.getDetail(),
                new Date(), operator, reason);
        sharingPlanTransaction.dealWithdrawRollback(userId, date, withdraw, withdrawProgress);
        redisLockDao.unLock(WITHDRAW_LOCK_PREFIX, String.valueOf(withdraw.getId()));
    }
}
