package com.chrtc.dataImport.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface ImportDataServiceI {
     JSONObject importData(MultipartFile file) throws SQLException;
}
