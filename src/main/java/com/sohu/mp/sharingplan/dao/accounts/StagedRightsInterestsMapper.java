package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.model.StagedRightsInterests;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StagedRightsInterestsMapper {

    @Select({
            "select * from staged_rights_interests where code = #{code}"
    })
    StagedRightsInterests getByCode(@Param("code") String code);

}
