package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.model.MulctDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MulctDetailMapper {

    @Insert({
            "insert into mulct_detail",
            "(user_id, passport, right_interest_code, source, period_day, amount, status, type, operator, reason, create_time)",
            "values",
            "(#{mulctDetail.userId}, #{mulctDetail.passport}, #{mulctDetail.rightInterestCode}, #{mulctDetail.source},",
            "#{mulctDetail.periodDay}, #{mulctDetail.amount}, #{mulctDetail.status}, #{mulctDetail.type}, #{mulctDetail.operator},",
            "#{mulctDetail.reason}, now())"
    })
    void addMulct(@Param("mulctDetail") MulctDetail mulctDetail);

}
