package com.chrtc.dataImport.service.impl;

import com.chrtc.dataImport.common.RandomUtil;
import com.chrtc.dataImport.service.ExportService;
import com.chrtc.dataImport.utils.FileEncAndDec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {
    @Value("${filePath}")
    private String prefilePath;
    @Value("${dbname}")
    private String schema;//库名
    @Value("#{'${tablename}'.split(',')}")
    private  String[] table;//表名
    @Value("${jdbc.url}")
     private String url;
    @Value("${jdbc.username}")
            private String username;
    @Value("${jdbc.password}")
            private String password;
    private static Connection conn = null;
    private static Statement sm = null;
    private static String select = "SELECT * FROM";//查询sql
    private static String insert = "INSERT INTO";//插入sql
    private static String values = "VALUES";//values关键字
    private String filePath;

    private static List<String> insertList = new ArrayList<String>();//全局存放insertsql文件的数据

    public void exportFile() throws SQLException {
        insertList.clear();

        filePath=prefilePath+ RandomUtil.getRandomFileName()+".sql";
        List<String> listSQL = new ArrayList<String>();
        connectSQL("com.mysql.cj.jdbc.Driver", url, username, password);//连接数据库
        listSQL = createSQL();//创建查询语句
        executeSQL(conn, sm, listSQL);//执行sql并拼装
        createFile();//创建文件
    }
    public void exportStream(HttpServletResponse response){
        InputStream is = null;
        OutputStream os = null;

        File file1 = new File(filePath);
        File file2 = new File(filePath+".zip");
        FileEncAndDec.encFile(file1,file2);
        try {
            is=new FileInputStream(file2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //最后返回输出流
        if (is != null) {
            int len = 0;
            byte[] data = new byte[1024];
            ServletOutputStream out = null;
            try {
                String fileName=RandomUtil.getRandomFileName()+".zip";
                //对中文名称进行编码，否则下载后文件名有问题
                fileName = URLEncoder.encode(fileName, "UTF-8");
                //设置header
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                out = response.getOutputStream();
                while ((len = is.read(data)) != -1) {
                    out.write(data, 0, len);
                }
            } catch (IOException e) {

            } finally {
                if (out != null) try {
                    out.flush();
                    out.close();
                    //file1.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 创建insertsql.txt并导出数据
     */
    public   void createFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("创建文件名失败！！");
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            //添加去掉主外键限制
            bw.append("SET FOREIGN_KEY_CHECKS = 0;");
            bw.append("\n");
            //添加delete语句
            if(table.length>0){
                for(String s:table){
                    bw.append("DELETE FROM "+s+";");
                    bw.append("\n");
                }
            }
                    //
            if (insertList.size() > 0) {
                for (int i = 0; i < insertList.size(); i++) {
                    bw.append(insertList.get(i));
                    bw.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拼装查询语句
     *
     * @return返回 select集合
     */
    public   List<String> createSQL() {
        List<String> listSQL = new ArrayList<String>();
        for (int i = 0; i < table.length; i++) {
            StringBuffer sb = new StringBuffer();
            sb.append(select).append(" ").append(schema).append(".").append(table[i]);
            listSQL.add(sb.toString());
        }
        return listSQL;
    }

    /**
     * 连接数据库创建statement对象
     * *@paramdriver
     * *@paramurl
     * *@paramUserName
     * *@paramPassword
     */
    public  void connectSQL(String driver, String url, String UserName, String Password) {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, UserName, Password);
            sm = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行sql并返回插入sql
     *
     * @paramconn
     * @paramsm
     * @paramlistSQL *
     * @throwsSQLException
     */
    public  void executeSQL(Connection conn, Statement sm, List listSQL) throws SQLException {
        List<String> insertSQL = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = getColumnNameAndColumeValue(sm, listSQL, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            sm.close();
            conn.close();
        }
    }

    /**
     * 获取列名和列值
     *
     * @return
     * @paramsm
     * @paramlistSQL
     * @paramrs
     * @throwsSQLException
     */
    public   ResultSet getColumnNameAndColumeValue(Statement sm, List listSQL, ResultSet rs) throws SQLException {
        if (listSQL.size() > 0) {
            for (int j = 0; j < listSQL.size(); j++) {
                String sql = String.valueOf(listSQL.get(j));
                rs = sm.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                while (rs.next()) {
                    StringBuffer ColumnName = new StringBuffer();
                    StringBuffer ColumnValue = new StringBuffer();
                    for (int i = 1; i <= columnCount; i++) {
                        /*String ss = rs.getString(i)==null?"":rs.getString(i);
                        String value = ss.trim();*/
                      String value=rs.getString(i)==null?null:rs.getString(i).trim().replace("\'","\\'");
                        if ("".equals(value)) {
                            value = "";
                        }
                        if (i == 1 || i == columnCount) {
                            if(i==columnCount){
                                ColumnName.append(",");
                            }
                            ColumnName.append(rsmd.getColumnName(i));
                            if( i== 1){
                                //拼接开始
                                if(value==null){ColumnValue.append(value).append(",");}else
                                if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i) || Types.LONGVARCHAR == rsmd.getColumnType(i)) {
                                    ColumnValue.append("'").append(value).append("',");
                                } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i) || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i) || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i) || Types.DECIMAL == rsmd.getColumnType(i)|| Types.TINYINT == rsmd.getColumnType(i)) {
                                    ColumnValue.append(value).append(",");
                                }
                                else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i) || Types.TIMESTAMP == rsmd.getColumnType(i)) {
                                    ColumnValue.append("'").append(value).append("',");
                                } else {
                                    ColumnValue.append(value).append(",");

                                }
                            }else{
                                //拼接最后
                                if(value==null){ColumnValue.append(value);}else
                                if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i) || Types.LONGVARCHAR == rsmd.getColumnType(i)) {
                                    ColumnValue.append("'").append(value).append("'");
                                } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i) || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i) || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i) || Types.DECIMAL == rsmd.getColumnType(i)|| Types.TINYINT == rsmd.getColumnType(i)) {
                                    ColumnValue.append(value);
                                }else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i) || Types.TIMESTAMP == rsmd.getColumnType(i)) {
                                    ColumnValue.append("'").append(value).append("'");;
                                } else {
                                    ColumnValue.append(value);

                                }
                            }

                        } else {
                            ColumnName.append("," + rsmd.getColumnName(i));
                            if(value==null){ColumnValue.append(value).append(",");}else
                            if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i) || Types.LONGVARCHAR == rsmd.getColumnType(i)) {
                                ColumnValue.append("'").append(value).append("'").append(",");
                            } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i) || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i) || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i) || Types.DECIMAL == rsmd.getColumnType(i)|| Types.TINYINT == rsmd.getColumnType(i)) {
                                ColumnValue.append(value).append(",");
                            } else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i) || Types.TIMESTAMP == rsmd.getColumnType(i)) {
                                ColumnValue.append("'").append(value).append("',");
                            } else {
                                ColumnValue.append(value).append(",");
                            }
                        }
                    }
                    System.out.println(ColumnName.toString());
                    System.out.println(ColumnValue.toString());
                    insertSQL(ColumnName, ColumnValue,table[j]);
                }
            }
        }
        return rs;
    }

    /**
     * 拼装insertsql放到全局list里面
     * @paramColumnName
     * @paramColumnValue
     */
    public   void insertSQL(StringBuffer ColumnName, StringBuffer ColumnValue,String tableName) {
        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append(insert).append(" ").append(schema).append(".")
                .append(tableName).append("(").append(ColumnName.toString()).append(")").append(values).append("(").append(ColumnValue.toString()).append(");");
        insertList.add(insertSQL.toString());
        System.out.println(insertSQL.toString());

    }


}
