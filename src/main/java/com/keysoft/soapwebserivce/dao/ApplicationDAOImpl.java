package com.keysoft.soapwebserivce.dao;

import com.keysoft.soapwebserivce.Application;
import com.keysoft.soapwebserivce.generated.PropertiesReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ApplicationDAOImpl implements ApplicationDAO {

    private String host;
    private String user;
    private String pass;

    public ApplicationDAOImpl() {
        initConfig(new PropertiesReader("db-config"));
    }

    private void initConfig(PropertiesReader properties) {
        host = properties.getProperty("host");
        user = properties.getProperty("username");
        pass = properties.getProperty("password");
    }
    
    @Override
    public List<Application> findAll() {
        List<Application> applications = new LinkedList<>();

        try (Connection connection = DriverManager.getConnection(host, user, pass);) {
            String sql = "select * from tza_application";
            PreparedStatement select = connection.prepareStatement(sql);
            ResultSet res = select.executeQuery();
            while (res.next()) {
                fillApplication(applications, res);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return applications;
    }

    @Override
    public Application findById(int id) {
        Application application;
        try (Connection connection = DriverManager.getConnection(host, user, pass);) {
            String sql = "select * from tza_application where id =?";
            PreparedStatement select = connection.prepareStatement(sql);
            select.setInt(1, id);
            application = getApplication(select);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return application;
    }

    @Override
    public Application findByIdAndName(int id, String name) {
        Application application;
        try (Connection connection = DriverManager.getConnection(host, user, pass);) {
            String sql = "select * from tza_application where id =? and name=?";
            PreparedStatement select = connection.prepareStatement(sql);
            select.setInt(1, id);
            select.setString(2, name);
            application = getApplication(select);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return application;
    }

    @Override
    public void addApplication(Application application) {

        try (Connection connection = DriverManager.getConnection(host, user, pass);) {
            String sql = "insert into tza_application values (?,?,?)";
            PreparedStatement insert = connection.prepareStatement(sql);
            insert.setInt(1, application.getId());
            insert.setString(2, application.getName());
            insert.setString(3, application.getDescription());
            insert.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeSQLIntegrityException(e);
        }catch (SQLException e){
            throw new RuntimeSQLException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = DriverManager.getConnection(host, user, pass);) {
            String sql = "delete from tza_application where id=?";
            PreparedStatement delete = connection.prepareStatement(sql);
            delete.setInt(1, id);
            delete.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeSQLException(e);
        }
    }

    @Override
    public void updateApplication(Application application) {
        try (Connection connection = DriverManager.getConnection(host, user, pass);) {
            String sql = "update tza_application set name=?,description=? where id=?";
            PreparedStatement update = connection.prepareStatement(sql);
            update.setString(1, application.getName());
            update.setString(2, application.getDescription());
            update.setInt(3, application.getId());
            update.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeSQLException(e);
        }
    }

    private Application getApplication(PreparedStatement statement) throws SQLException {
        Application result = null;
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            int t_id = res.getInt("id");
            String t_name = res.getString("name");
            String description = res.getString("description");
            result = new Application(t_id, t_name, description);
        }
        return result;
    }

    private static void fillApplication(List<Application> applications, ResultSet res) throws SQLException {
        int id = res.getInt("id");
        String name = res.getString("name");
        String description = res.getString("description");
        applications.add(new Application(id, name, description));
    }
}
