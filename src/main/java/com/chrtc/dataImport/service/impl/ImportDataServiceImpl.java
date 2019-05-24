package com.chrtc.dataImport.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chrtc.dataImport.common.RandomUtil;
import com.chrtc.dataImport.service.ImportDataServiceI;
import com.chrtc.dataImport.utils.FileEncAndDec;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class ImportDataServiceImpl implements ImportDataServiceI {
    private static Logger logger = LoggerFactory.getLogger(ImportDataServiceImpl.class);

    @Value("${jiwei.driver}")
    private String driver;
    @Value("${jiwei.jdbcUrl}")
    private String jdbcUrl;
    @Value("${jiwei.userName}")
    private String userName;
    @Value("${jiwei.userPassword}")
    private String userPassword;
    @Value("${jiwei.path}")
    private  String path;

    @Override
    public JSONObject importData(MultipartFile file) throws SQLException {
        JSONObject json = new JSONObject();
        if (file.isEmpty()) {
            json.put("code",-1);
            json.put("message","请选择上传文件");
            return json;
        }
        String fileName = file.getOriginalFilename();
        String filePath = path+fileName;
        File dest = new File(filePath);
        try {
            file.transferTo(dest);

        } catch (IOException e) {
            logger.info("上传失败");
            json.put("code",-1);
            json.put("message","上传失败");
            return  json;
        }
        String zgfilePath = path+ RandomUtil.getRandomFileName()+".sql";
        try {
            File bSql = new File(zgfilePath);
            FileEncAndDec.decFile(dest,bSql);
            dest.delete();
        } catch (Exception e) {
            logger.info("文件解压失败");
            json.put("code",-2);
            json.put("message","文件解压失败");
            return  json;
        }

        Boolean flag =  this.scriptRunnerSql(zgfilePath);

        if (!flag){
            json.put("code",-3);
            json.put("message","数据上传异常");
            return  json;
        }

        json.put("code",0);
        json.put("message","上传成功");
        return  json;
    }

    /**
     * 执行sql 脚本
     * @param path
     * @return
     * @throws SQLException
     */
    private   boolean scriptRunnerSql(String path) throws SQLException {
        Connection conn = null;
        Boolean flag = true;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(jdbcUrl,userName,userPassword);
            //设置不自动提交
            conn.setAutoCommit(false);
            ScriptRunner runner = new ScriptRunner(conn);
            // 设置不自动提交
            runner.setAutoCommit(false);
            /** setStopOnError参数作用：遇见错误是否停止；
             * （1）false，遇见错误不会停止，会继续执行，会打印异常信息，并不会抛出异常，当前方法无法捕捉异常无法进行回滚操作，无法保证在一个事务内执行；
             * （2）true，遇见错误会停止执行，打印并抛出异常，捕捉异常，并进行回滚，保证在一个事务内执行；
             */
            runner.setStopOnError(true);
            Resources.setCharset(Charset.forName("UTF-8")); //设置字符集,不然中文乱码插入错误
            runner.setLogWriter(null);//设置是否输出日志
            // 绝对路径读取
            Reader read = new FileReader(new File(path));
            runner.runScript(read);
            conn.commit();
            logger.info("sql脚本执行完毕");
        } catch (Exception e) {
            flag = false;
            conn.rollback();
            logger.info("sql脚本执行发生异常");
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                if (conn != null) {
                    conn = null;
                }
            }
        }
        return  flag;
    }
}
