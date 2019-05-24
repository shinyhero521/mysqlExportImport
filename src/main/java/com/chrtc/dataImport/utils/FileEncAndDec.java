package com.chrtc.dataImport.utils;

import java.io.*;

public class FileEncAndDec {
    private static final int numOfEncAndDec = 123456;
    private static int dataOfFile = 0;

    /**
     * 压缩
     */
    public static void encFile (File srcFile, File encFile) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            if (!srcFile.exists()) {
                return;
            }
            if (!encFile.exists()) {
                encFile.createNewFile();
            }
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(encFile);
            while (((dataOfFile = fis.read()) > -1)) {
                fos.write(dataOfFile ^ numOfEncAndDec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 解压
     */
    public static void decFile (File encFile, File decFile) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            if (!encFile.exists()) {
                return;
            }
            if (!decFile.exists()) {
                decFile.createNewFile();
            }
            fis = new FileInputStream(encFile);
            fos = new FileOutputStream(decFile);
            while ((dataOfFile = fis.read()) > -1) {
                fos.write(dataOfFile ^ numOfEncAndDec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        File a = new File("F:\\zip\\demo.sql");
        File b = new File("F:\\zip\\sql.zip");
        File c = new File("F:\\zip\\456.txt");

         encFile(a, b);
        //decFile(b, c);
    }
}
