package com.chrtc.dataImport.web;

import com.chrtc.dataImport.service.ImportDataServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

@RestController
public class ImportDataController {

    @Autowired
    private ImportDataServiceI importDataService;

    @RequestMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws SQLException {
         return  importDataService.importData(file).toJSONString();
    }

}
