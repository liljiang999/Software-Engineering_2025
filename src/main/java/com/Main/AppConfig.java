package com.Main;

import java.io.File;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.multipart.MultipartResolver;



import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.Servlet5Loader;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import jakarta.servlet.ServletContext;

@Configuration
@ComponentScan
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:/jdbc.properties")
@PropertySource("classpath:/app.properties")
public class AppConfig  implements WebMvcConfigurer {

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.getInteger("port", 8080));
        tomcat.getConnector();
        Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
        ctx.setResources(resources);
        tomcat.start();
        tomcat.getServer().await();
        
    }

    // -- Mvc configuration ---------------------------------------------------

    @Bean
    WebMvcConfigurer createWebMvcConfigurer_Resource() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**").addResourceLocations("/static/");
                registry.addResourceHandler("/avatars/**").addResourceLocations("file:src/main/webapp/avatars/");
            }
        };
    }

    @Bean
    WebMvcConfigurer createWebMvcConfigurer_Interceptor(@Autowired HandlerInterceptor[] interceptors) {
        return new WebMvcConfigurer() {
            public void addInterceptors(InterceptorRegistry registry) {
                for (var interceptor : interceptors) {
                    registry.addInterceptor(interceptor);
                }
            }
        };
    }

    // -- pebble view configuration -------------------------------------------

    @Bean
    ViewResolver createViewResolver(@Autowired ServletContext servletContext) {
        var engine = new PebbleEngine.Builder().autoEscaping(true)
                // cache:
                .cacheActive(false)
                // loader:
                .loader(new Servlet5Loader(servletContext))
                // build:
                .build();
        var viewResolver = new PebbleViewResolver(engine);
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix("");
        return viewResolver;
    }

    // -- jdbc configuration --------------------------------------------------

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Value("${jdbc.username}")
    String jdbcUsername;

    @Value("${jdbc.password}")
    String jdbcPassword;

    @Bean
    DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        config.addDataSourceProperty("autoCommit", "false");
        config.addDataSourceProperty("connectionTimeout", "5");
        config.addDataSourceProperty("idleTimeout", "60");
        return new HikariDataSource(config);
    }

    @Bean
    JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // -- Cors Mapping --------------------------------------------------

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://localhost:5174") // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方式
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true);

    }

}
