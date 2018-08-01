package com.sohu.mp.sharingplan.service.impl;

import com.sohu.mp.common.enums.MulctEnum;
import com.sohu.mp.common.enums.ProfitStatusEnum;
import com.sohu.mp.common.enums.StagedRightsInterestsEnum;
import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.common.exception.ServerErrorException;
import com.sohu.mp.common.util.CodecUtil;
import com.sohu.mp.common.util.DateUtil;
import com.sohu.mp.sharingplan.dao.accounts.AssetMapper;
import com.sohu.mp.sharingplan.dao.accounts.BonusMapper;
import com.sohu.mp.sharingplan.dao.accounts.ProfitMapper;
import com.sohu.mp.sharingplan.dao.accounts.StagedRightsInterestsMapper;
import com.sohu.mp.sharingplan.dao.redis.impl.RedisLockDao;
import com.sohu.mp.sharingplan.enums.exception.AmountCheckException;
import com.sohu.mp.sharingplan.model.*;
import com.sohu.mp.sharingplan.service.CommonApiService;
import com.sohu.mp.sharingplan.service.MulctService;
import com.sohu.mp.sharingplan.transaction.SharingPlanTransaction;
import com.sohu.mp.sharingplan.util.TableHashUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class MulctServiceImpl implements MulctService {

    private static final Logger logger = LoggerFactory.getLogger(MulctServiceImpl.class);

    private static final String BASE_LOCK_PREFIX = "base_mulct";
    private static final String BONUS_LOCK_PREFIX = "bonus_mulct";
    private static final String ERROR_EMAIL = "jinwanglv213697@sohu-inc.com";

    @Resource
    private AssetMapper assetMapper;

    @Resource
    private ProfitMapper profitMapper;

    @Resource
    private BonusMapper bonusMapper;

    @Resource
    private RedisLockDao redisLockDao;

    @Resource
    private SharingPlanTransaction sharingPlanTransaction;

    @Resource
    private CommonApiService commonApiService;

    @Resource
    private StagedRightsInterestsMapper stagedRightsInterestsMapper;

    @Override
    public boolean canLockBaseMulct(String passport) {
        return redisLockDao.lock(BASE_LOCK_PREFIX, passport);
    }

    @Override
    public boolean canLockBonusMulct(String passport) {
        return redisLockDao.lock(BONUS_LOCK_PREFIX, passport);
    }

    /**
     * 处理base罚金
      * @param mpProfile
     * @param operator
     * @param periodDay
     * @param reason
     */
    @Override
    public void dealBaseMulct(MpProfile mpProfile, String operator, Date periodDay, String reason) {
        long userId = mpProfile.getId();
        Profit profit = profitMapper.getByUserDayWriteSource(
                TableHashUtil.getProfitTableIndex(userId), userId, periodDay);
        if (profit == null) {
            throw new AmountCheckException("day profit is null");
        }
        if (profit.getAmount().compareTo(BigDecimal.valueOf(0)) == 0) {
            throw new AmountCheckException("day profit is zero");
        }
        Asset asset = checkAsset(userId, profit.getAmount());
        MulctDetail mulctDetail = new MulctDetail(userId, mpProfile.getPassport(),
                profit.getRightInterestCode(),
                StagedRightsInterestsEnum.FLOW_RIGHTS_INTEREST.getSource(), periodDay,
                profit.getAmount(), 1, MulctEnum.FLOW_BASE_MULCT.getType(), operator, reason);

        sharingPlanTransaction.dealBaseMulct(asset.getId(), profit.getId(), mulctDetail);
        logger.info("[base mulct success]: passport={}, periodDay={}, operator={}",
                mpProfile.getPassport(), periodDay, operator);

        redisLockDao.unLock(BASE_LOCK_PREFIX, mpProfile.getPassport());
        String content = generateMulctVM(mpProfile, mulctDetail, reason, operator);
        commonApiService.sendEmail("【base处罚操作结果通知】", content, operator);
    }

    /**
     * 处理bonus罚金
     * @param mpProfile
     * @param operator
     * @param code
     * @param reason
     */
    @Override
    public void dealBonusMulct(MpProfile mpProfile, String operator, String code, String reason){
        StagedRightsInterests sharingPlan = stagedRightsInterestsMapper.getByCode(code);
        if (sharingPlan == null || sharingPlan.getStartTime().compareTo(DateUtil.getPreFirstDayOfMonth(1)) != 0) {
            throw new InvalidParameterException("无效code, 只能处罚上月bonus");
        }
        long userId = mpProfile.getId();
        Bonus bonus = bonusMapper.getByCodeUser(sharingPlan.getCode(), userId);
        if (bonus == null) {
            throw new AmountCheckException("bonuses is empty");
        }
        if (bonus.getAmount().compareTo(BigDecimal.valueOf(0)) == 0) {
            throw new AmountCheckException("bonuses amount is zero");
        }
        MulctDetail mulctDetail = new MulctDetail(userId, mpProfile.getPassport(), code,
                StagedRightsInterestsEnum.FLOW_RIGHTS_INTEREST.getSource(), null,
                bonus.getAmount(), 1, MulctEnum.FLOW_BONUS_MULCT.getType(), operator, reason);
        //由于奖金都是批量更新状态, 所以取第一个的状态
        Integer assetId = null;
        if (bonus.getStatus() == ProfitStatusEnum.VALID.getValue()) {
            assetId = checkAsset(userId, bonus.getAmount()).getId();
        }
        sharingPlanTransaction.dealBonusMulct(assetId, userId, code, mulctDetail);
        logger.info("[bonus mulct success]: passport={}, code={}, operator={}", mpProfile.getPassport(), code, operator);
        redisLockDao.unLock(BONUS_LOCK_PREFIX, mpProfile.getPassport());
        String content = generateMulctVM(mpProfile, mulctDetail, reason, operator);
        commonApiService.sendEmail("【bonus处罚操作结果通知】", content, operator);
    }

    /**
     * 检查资产
     * @param userId
     * @param mulct
     * @return
     */
    private Asset checkAsset(long userId, BigDecimal mulct) {
        Asset asset = assetMapper.getByUserSourceWriteSource(userId,
                StagedRightsInterestsEnum.FLOW_RIGHTS_INTEREST.getSource());
        if (asset == null) {
            throw new AmountCheckException("asset is null");
        }
        if (asset.getValidAmount().subtract(mulct).compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new AmountCheckException("after mulct, asset valid amount less than zero");
        }
        return asset;
    }

    /**
     *  生成罚金VM
     * @param mpProfile
     * @param mulctDetail
     * @param reason
     * @param operator
     * @return
     */

    private static String generateMulctVM(MpProfile mpProfile, MulctDetail mulctDetail,
                                          String reason, String operator){
        VelocityContext context = new VelocityContext();
        context.put("mpProfile", mpProfile);
        context.put("amount", mulctDetail.getAmount());
        context.put("periodDay", mulctDetail.getPeriodDay() == null ? "" : DateUtil.parseDate2DayStr(mulctDetail.getPeriodDay()));
        context.put("reason", reason);
        context.put("operator", operator);
        InputStream inputStream = MulctServiceImpl.class.getResourceAsStream("/template/MulctResult.vm");
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream, "UTf-8");
            StringWriter writer = new StringWriter();
            Velocity.evaluate(context, writer, "", reader);
            String result =writer.toString();
            inputStream.close();
            reader.close();
            writer.close();
            return result;
        } catch (IOException e) {
            logger.error("Velocity模板填充失败",e);
            throw new ServerErrorException();
        }
    }

    public static void main(String[] args) {
        System.out.println(CodecUtil.hmacSha1("jinwanglv213697@sohu-inc.com"));
    }
}
