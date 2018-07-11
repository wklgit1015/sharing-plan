package com.sohu.mp.sharingplan.transaction;

import com.sohu.mp.sharingplan.dao.accounts.AssetMapper;
import com.sohu.mp.sharingplan.dao.accounts.BonusMapper;
import com.sohu.mp.sharingplan.dao.accounts.MulctDetailMapper;
import com.sohu.mp.sharingplan.dao.accounts.ProfitMapper;
import com.sohu.mp.sharingplan.model.MulctDetail;
import com.sohu.mp.sharingplan.util.TableHashUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
public class SharingPlanTransaction {

    @Resource
    private AssetMapper assetMapper;

    @Resource
    private BonusMapper bonusMapper;

    @Resource
    private ProfitMapper profitMapper;

    @Resource
    private MulctDetailMapper mulctDetailMapper;

    @Transactional(value = "mpTransaction", rollbackFor = Exception.class)
    public void dealBaseMulct(int assetId, int profitId, MulctDetail mulctDetail) {
        assetMapper.addMulct(assetId, mulctDetail.getAmount());
        profitMapper.addMulct(TableHashUtil.getProfitTableIndex(mulctDetail.getUserId()), profitId, mulctDetail.getAmount());
        mulctDetailMapper.addMulct(mulctDetail);
    }

    @Transactional(value = "mpTransaction", rollbackFor = Exception.class)
    public void dealBonusMulct(Integer assetId, long userId, String code, MulctDetail mulctDetail) {
        if (assetId != null) {
            assetMapper.addMulct(assetId, mulctDetail.getAmount());
        }
        //由于有多条bonus 所以不用加减操作处罚
        bonusMapper.addMulct(code, userId);
        mulctDetailMapper.addMulct(mulctDetail);
    }

}
