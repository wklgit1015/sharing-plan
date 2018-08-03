package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.annotation.WriteDatasource;
import com.sohu.mp.sharingplan.model.Withdraw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WithdrawMapper {


    @Select({
            "select * from withdraw where user_id = #{userId} and create_time like CONCAT(#{date},'%')"
    })
    @WriteDatasource
    Withdraw getByUserIdAndDate(@Param("userId") long userId, @Param("date") String date);

    @Update({
            "update withdraw set status = #{status} where user_id = #{userId} and create_time like CONCAT(#{date},'%')"
    })
    void updateWithdrawStatus(@Param("userId") long userId, @Param("date") String date, @Param("status") int status);


}
