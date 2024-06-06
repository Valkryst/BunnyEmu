package bunnyEmu.main.db;

import bunnyEmu.main.Server;
import bunnyEmu.main.misc.Logger;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

	private static BoneCP connectionPool = null;

	public static void initConnectionPool(Properties prop) {
		try {
			Class.forName("org.mariadb.jdbc.Driver");

			BoneCPConfig config = new BoneCPConfig();
			
			// jdbc:mysql://127.0.0.1:3306/"
			String JdbcURL = "jdbc:mysql://" + Server.realmlist + ":" + prop.getProperty("databasePort");

			config.setJdbcUrl(JdbcURL);
			config.setUsername(prop.getProperty("user"));
			config.setPassword(prop.getProperty("password"));

			config.setMinConnectionsPerPartition(3);
			config.setMaxConnectionsPerPartition(5);
			config.setPartitionCount(1); // 1*2 = 2 connections
			// config.setLazyInit(true); // depends on the application usage
			connectionPool = new BoneCP(config); // setup the connection pool

			Logger.writeLog("Database connection succeeded!", Logger.LOG_TYPE_VERBOSE);

			// Log.log("This many active physical connections: " + connectionPool.getTotalCreatedConnections());
			DatabaseConnection.setConnectionPool(connectionPool);
			
			Boolean exists = DatabaseHandler.databasesExist(prop.getProperty("authDB").toLowerCase(), 
															prop.getProperty("charactersDB").toLowerCase(), 
															prop.getProperty("worldDB").toLowerCase());
			
			if (!exists) {
				Logger.writeLog("One or more databases (authDB, charactersDB, or worldDB) do not exist.", Logger.LOG_TYPE_ERROR);
				System.exit(0);
			}

		} catch (Exception e) {
			e.printStackTrace(); // Fix this.. exception wrapping.
			System.exit(0);
		}
	}

	// call at end of program to close all physical threads
	public static void shutdownConnectionPool() {
		try {
			BoneCP connectionPool = DatabaseConnection.getConnectionPool();
			Logger.writeLog("Shutting down connection pool.", Logger.LOG_TYPE_VERBOSE);
			if (connectionPool != null) {
				connectionPool.shutdown();
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

	public static BoneCP getConnectionPool() {
		return connectionPool;
	}

	public static void setConnectionPool(BoneCP connectionPool) {
		DatabaseConnection.connectionPool = connectionPool;
	}
}