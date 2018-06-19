package com.sohu.mp.sharingplan.dao.accounts;

import com.sohu.mp.sharingplan.model.MpProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MpProfileMapper {

    @Select("select * from mp_profile where passport = #{passport}")
    MpProfile getByPassport(@Param("passport") String passport);

}
