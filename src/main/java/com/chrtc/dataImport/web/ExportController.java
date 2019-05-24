package com.chrtc.dataImport.web;

import com.chrtc.dataImport.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;

@RestController
public class ExportController {
    @Autowired
    private ExportService exportService;
    @Value("${filePath}")
    private String filePath;
    @RequestMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response){
        //执行写入sql
        try {
            exportService.exportFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //导出输出流
        exportService.exportStream(response);

    };


}
