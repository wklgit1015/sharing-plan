package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.annotation.WriteDatasource;
import com.sohu.mp.sharingplan.model.Bonus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BonusMapper {

    @WriteDatasource
    @Select({
            "select sum(amount) as amount, user_id, status from bonus",
            "where right_interest_code = #{code} and user_id = #{userId}"
    })
    Bonus getByCodeUser(@Param("code") String code, @Param("userId") long userId);

    @Update({
            "update bonus",
            "set mulct = amount, amount = 0",
            "where right_interest_code = #{code} and user_id = #{userId}"
    })
    void addMulct(@Param("code") String code, @Param("userId") long userId);

}
