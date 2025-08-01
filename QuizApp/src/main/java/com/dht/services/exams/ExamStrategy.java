/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dht.services.exams;

import com.dht.pojo.Exam;
import com.dht.pojo.Question;
import com.dht.utils.JdbcConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author admin
 */
public abstract class ExamStrategy {
    public abstract List<Question> getQuestions() throws SQLException;
    
    public void saveExam(List<Question> questions) throws SQLException {
        Connection conn = JdbcConnector.getInstance().connect();
        
        conn.setAutoCommit(false);
        Exam ex = new Exam(questions);
        String sql = "INSERT INTO exam(title, created_date) VALUES(?, ?)";
        PreparedStatement stm = conn.prepareCall(sql);
        stm.setString(1, ex.getTitle());
        stm.setString(2, ex.getCreatedDate().toString());
        
        if (stm.executeUpdate() > 0) {
            int exId = -1;
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next())
                exId = rs.getInt(1);
            
            sql = "INSERT INTO exam_question(question_id, exam_id) VALUES(?, ?)";
            stm = conn.prepareCall(sql);
            
            for (var q: questions) {
                stm.setInt(1, q.getId());
                stm.setInt(2, exId);
                
                stm.executeUpdate();
            }
            
            conn.commit();
        } else
            conn.rollback();
    }
}
