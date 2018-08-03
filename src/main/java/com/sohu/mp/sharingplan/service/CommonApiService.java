package com.sohu.mp.sharingplan.service;

import java.io.File;

public interface CommonApiService {

    void sendEmail(String title, String content, String toEmail, File... files);

}
