package com.rufino.server.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DatabaseConfig {

    Dotenv dotenv;
    String url, username, database_password;

    public DatabaseConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
        url = this.dotenv.get("DATABASE_URL");
        // DATABASE_URL=postgres://postgres:mysecretpassword@localhost:5432/order_management
    }

    @Bean
    public DataSource postgresDataSource() throws URISyntaxException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        URI dbUri = new URI(url);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}