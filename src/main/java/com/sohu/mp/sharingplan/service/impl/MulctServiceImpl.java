package com.sohu.mp.sharingplan.service.impl;

import com.sohu.mp.common.enums.StagedRightsInterestsEnum;
import com.sohu.mp.common.util.CodecUtil;
import com.sohu.mp.common.util.DateUtil;
import com.sohu.mp.sharingplan.dao.accounts.AssetMapper;
import com.sohu.mp.sharingplan.dao.accounts.ProfitMapper;
import com.sohu.mp.sharingplan.dao.redis.impl.RedisLockDao;
import com.sohu.mp.sharingplan.enums.exception.AmountCheckException;
import com.sohu.mp.sharingplan.model.Asset;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.model.MulctDetail;
import com.sohu.mp.sharingplan.model.Profit;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class MulctServiceImpl implements MulctService {

    private static final Logger logger = LoggerFactory.getLogger(MulctServiceImpl.class);

    private static final String LOCK_PREFIX = "mulct";

    @Resource
    private AssetMapper assetMapper;

    @Resource
    private ProfitMapper profitMapper;

    @Resource
    private RedisLockDao redisLockDao;

    @Resource
    private SharingPlanTransaction sharingPlanTransaction;

    @Resource
    private CommonApiService commonApiService;

    @Override
    public boolean canLockMulct(String passport) {
        return redisLockDao.lock(LOCK_PREFIX, passport);
    }

    @Override
    public void dealMulct(MpProfile mpProfile, String operator, Date periodDay, String reason) {
        long userId = mpProfile.getId();
        Profit profit = profitMapper.getByUserDayWriteSource(TableHashUtil.getProfitTableIndex(userId), userId, periodDay);
        if (profit == null) {
            throw new AmountCheckException("day profit is null");
        }
        if (profit.getAmount().compareTo(BigDecimal.valueOf(0)) == 0) {
            throw new AmountCheckException("day profit is zero");
        }
        Asset asset = assetMapper.getByUserSourceWriteSource(userId, StagedRightsInterestsEnum.FLOW_RIGHTS_INTEREST.getSource());
        if (asset == null) {
            throw new AmountCheckException("asset is null");
        }
        if (asset.getValidAmount().subtract(profit.getAmount()).compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new AmountCheckException("after mulct, asset valid amount less than zero");
        }
        MulctDetail mulctDetail = new MulctDetail(userId, mpProfile.getPassport(), profit.getRightInterestCode(),
                StagedRightsInterestsEnum.FLOW_RIGHTS_INTEREST.getSource(), periodDay, profit.getAmount(), 1, operator, reason);
        sharingPlanTransaction.dealMulct(asset.getId(), profit.getId(), mulctDetail);
        logger.info("[mulct success]: passport={}, periodDay={}, operator={}", mpProfile.getPassport(), periodDay, operator);
        redisLockDao.unLock(LOCK_PREFIX, mpProfile.getPassport());
        try {
            String content = generateMulctVM(mpProfile, mulctDetail, reason, operator);
            commonApiService.sendEmail("【处罚操作结果通知】", content,operator);
        } catch (Exception e) {
            logger.error("[send mulct result email error]: {}", e.getMessage());
        }
    }

    private static String generateMulctVM(MpProfile mpProfile, MulctDetail mulctDetail, String reason, String operator) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("mpProfile", mpProfile);
        context.put("amount", mulctDetail.getAmount());
        context.put("periodDay", DateUtil.parseDate2DayStr(mulctDetail.getPeriodDay()));
        context.put("reason", reason);
        context.put("operator", operator);
        InputStream inputStream = MulctServiceImpl.class.getResourceAsStream("/template/MulctResult.vm");
        Reader reader = new InputStreamReader(inputStream, "UTf-8");
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "", reader);
        String result = writer.toString();
        inputStream.close();
        reader.close();
        writer.close();
        return result;
    }

    public static void main(String[] args) {
        System.out.println(CodecUtil.hmacSha1("jinwanglv213697@sohu-inc.com"));
    }
}
