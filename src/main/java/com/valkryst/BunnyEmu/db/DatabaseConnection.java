package com.valkryst.BunnyEmu.db;

import com.valkryst.BunnyEmu.Server;
import com.valkryst.BunnyEmu.misc.Logger;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

	private static HikariDataSource connectionPool = null;

	public static void initConnectionPool(Properties prop) {
		try {
			HikariDataSource ds = new HikariDataSource();
			ds.setJdbcUrl("jdbc:mariadb://" + Server.realmlist + ":" + prop.getProperty("databasePort") + "/");
			ds.setUsername("root");
			ds.setPassword("mariadb");

			connectionPool = ds;

			Logger.writeLog("Database connection succeeded!", Logger.LOG_TYPE_VERBOSE);

			for (final String propertyName : new String[] {"authDB", "charactersDB", "worldDB"}) {
				if (prop.getProperty(propertyName) == null) {
					Logger.writeLog("The " + propertyName + " database does not exist.", Logger.LOG_TYPE_ERROR);
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // Fix this.. exception wrapping.
			System.exit(0);
		}
	}

	// call at end of program to close all physical threads
	public static void shutdownConnectionPool() {
		try {
			Logger.writeLog("Shutting down connection pool.", Logger.LOG_TYPE_VERBOSE);
			if (connectionPool != null) {
				connectionPool.close();
				Logger.writeLog("Connection pooling is destroyed successfully.", Logger.LOG_TYPE_VERBOSE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// use to get a logical connection of a free physical connection
	public static Connection getConnection() {
		Connection conn = null;
		try {
			// thread-safe
			conn = getConnectionPool().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// simple close statement.. important dont forget to do this
	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// dont forget this either
	public static void closeResultSet(ResultSet rSet) {
		try {
			if (rSet != null)
				rSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DO NOT FORGET TO CLOSE LOGICAL CONNECTIONS
	public static void closeConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close(); // release the connection
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static HikariDataSource getConnectionPool() {
		return connectionPool;
	}
}