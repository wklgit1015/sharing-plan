package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.annotation.WriteDatasource;
import com.sohu.mp.sharingplan.model.Profit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface ProfitMapper {

    @Select("select * from profit_#{index} where user_id = #{userId} and period_day = #{periodDay}")
    @WriteDatasource
    Profit getByUserDayWriteSource(@Param("index") int index, @Param("userId") long userId, @Param("periodDay") Date periodDay);

    @Update({
            "update profit_#{index}",
            "set amount = amount - #{mulct}, mulct = mulct + #{mulct}",
            "where id = #{id}"
    })
    void addMulct(@Param("index") int index, @Param("id") int id, @Param("mulct") BigDecimal mulct);

}
