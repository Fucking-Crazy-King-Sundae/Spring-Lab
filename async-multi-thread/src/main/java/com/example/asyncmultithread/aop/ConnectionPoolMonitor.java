package com.example.asyncmultithread.aop;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectionPoolMonitor {

    @Autowired
    private HikariDataSource dataSource;

    //    @Scheduled(fixedRate = 100) // Every 0.1 seconds
    public void logConnectionPoolStatus() {
        int activeConnections = dataSource.getHikariPoolMXBean().getActiveConnections();
        int idleConnections = dataSource.getHikariPoolMXBean().getIdleConnections();
        int totalConnections = dataSource.getHikariPoolMXBean().getTotalConnections();

        System.out.println("Active Connections: " + activeConnections +
                ", Idle Connections: " + idleConnections +
                ", Total Connections: " + totalConnections);
    }
}
