package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.annotation.WriteDatasource;
import com.sohu.mp.sharingplan.model.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface AssetMapper {

    @WriteDatasource
    @Select("select * from asset where user_id = #{userId} and source = #{source}")
    Asset getByUserSourceWriteSource(@Param("userId") long userId, @Param("source") int source);

    @Update({
            "update asset",
            "set valid_amount = valid_amount - #{mulct}, mulct = mulct + #{mulct}",
            "where id = #{id}"
    })
    void addMulct(@Param("id") int id, @Param("mulct") BigDecimal mulct);

}
