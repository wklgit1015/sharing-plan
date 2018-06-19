package com.sohu.mp.sharingplan.service.impl;

import com.sohu.mp.sharingplan.service.CommonApiService;
import com.sohu.mp.sharingplan.util.EnvironmentUtil;
import com.sohu.mp.sharingplan.util.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonApiServiceImpl implements CommonApiService {

    @Resource
    private EnvironmentUtil environmentUtil;

    @Override
    public void sendEmail(String title, String content, String toEmail, File... files) {
        Map<String, String> emailParams = new HashMap<>();
        emailParams.put("title", getEnvTitle(title));
        emailParams.put("content", StringUtils.isBlank(content) ? "附件:" : content);
        emailParams.put("toEmail", getEnvEmails(toEmail));
        HttpClientUtil.postWithFile("http://common-api-dev.mp.sohuno.com/email/noAuth", emailParams, "attachments", files);
    }

    private String getEnvTitle(String title) {
        if (environmentUtil.isProductionEnv()) {
            return title + "（线上）";
        }
        return title + "（测试）";
    }

    private String getEnvEmails(String emails) {
        if (environmentUtil.isProductionEnv()) {
            return emails;
        }
        return "jinwanglv213697@sohu-inc.com";
    }

}
