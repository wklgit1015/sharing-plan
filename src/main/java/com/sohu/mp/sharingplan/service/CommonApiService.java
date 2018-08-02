package com.sohu.mp.sharingplan.service;

import com.sohu.mp.sharingplan.model.MpProfile;

import java.io.File;
import java.util.List;

public interface CommonApiService {

    void sendEmail(String title, String content, String toEmail, File... files);

    MpProfile checkParam(String sign, String reason, String passport, String operator);
}
