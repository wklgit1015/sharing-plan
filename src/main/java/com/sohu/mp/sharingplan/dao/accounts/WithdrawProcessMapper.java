package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.model.WithdrawProgress;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WithdrawProcessMapper {

    @Insert({
            "insert into withdraw_progress",
            "(withdraw_id,user_id,type,detail,create_time,operator,reason)values",
            "(#{progress.withdrawId},#{progress.userId},#{progress.type},#{progress.detail},",
            "#{progress.createTime},#{progress.operator},#{progress.reason})"
    })
    void add(@Param("progress") WithdrawProgress progress);
}
