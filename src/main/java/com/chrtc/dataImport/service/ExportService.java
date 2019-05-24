package com.chrtc.dataImport.service;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface ExportService {
    public void exportStream(HttpServletResponse response);
    public void exportFile() throws SQLException;
}
