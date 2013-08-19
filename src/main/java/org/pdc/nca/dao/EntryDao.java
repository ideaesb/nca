package org.pdc.nca.dao;


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pdc.nca.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryDao {

	private Logger logger = LoggerFactory.getLogger(EntryDao.class);
	
	private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    
    private String DB_DRIVER = "org.postgresql.Driver";
	private String DB_CONNECTION = "jdbc:postgresql:nca";
	private String DB_USER = "postgres";
	private String DB_PASSWORD = "admin123";
    

    public EntryDao(boolean useHibernate)
    {
      if (useHibernate) setSessionFactory();
      // else database just using plain JDBC 
    }
	
    
    public void addEntry(Entry entry) {
        Transaction trns = null;
        Session session = sessionFactory.openSession();
        try {
            trns = session.beginTransaction();
            session.save(entry);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            logger.error("DB Err Add Entry " + e.toString());
        } finally {
            session.flush();
            session.close();
        }
    }

    public void deleteEntry(String key) {
        Transaction trns = null;
        Session session = sessionFactory.openSession();
        try {
            trns = session.beginTransaction();
            Entry entry = (Entry) session.load(Entry.class, key);
            session.delete(entry);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            logger.error("DB Err Delete Entry " + e.toString());
        } finally {
            session.flush();
            session.close();
        }
    }

    public void updateEntry(Entry entry) {
        Transaction trns = null;
        Session session = sessionFactory.openSession();
        try {
            trns = session.beginTransaction();
            session.update(entry);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            logger.error("DB Err Update Entry " + e.toString());

        } finally {
            session.flush();
            session.close();
        }
    }

    public List<Entry> getAllEntrys() {
        List<Entry> entrys = new ArrayList<Entry>();
        Transaction trns = null;
        Session session = sessionFactory.openSession();
        try {
            trns = session.beginTransaction();
            entrys = session.createQuery("from Entry").list();
        } catch (RuntimeException e) {
            logger.error("DB Err Getting List " + e.toString());

        } finally {
            session.flush();
            session.close();
        }
        return entrys;
    }

    public Entry getEntryById(String entryid) {
        Entry entry = null;
        Transaction trns = null;
        Session session = sessionFactory.openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "from Entry where key = :key";
            Query query = session.createQuery(queryString);
            query.setString("key", entryid);
            entry = (Entry) query.uniqueResult();
        } catch (RuntimeException e) {
            logger.error("DB Err retreiving Entry.key " + entryid);
        } finally {
            session.flush();
            session.close();
        }
        return entry;
    }
    
	
	
	
	
	private void  setSessionFactory() 
	{
		try
		{
		  Configuration configuration = new Configuration().configure();
          serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
          sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		catch (Exception e)
		{
			logger.error("Error in connecting to database " + e.toString());
		}
    }
	
	
	
	public boolean  tryAdd(Entry entry)  {
		 
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		boolean result = false; 
 
		String insertTableSQL = "INSERT INTO ENTRIES (entryKeyHash) VALUES (?)";
 
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);
 
			preparedStatement.setString(1, entry.getKey());
 
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
 
			logger.info(entry.getKey() + " is inserted into ENTRIES table!");
			result = true;
 
		} catch (SQLException e) 
		{
			if (StringUtils.containsIgnoreCase(e.getMessage(), "duplicate key"))
			{
				// do nothing - exactly expected 
			}
			else
			{
			  logger.error(e.getMessage());
			}
 
		} finally {
 
			try
			{
			  if (preparedStatement != null) preparedStatement.close();
  			  if (dbConnection != null) dbConnection.close();
			}
			catch(SQLException e) {
				 
				logger.error(e.getMessage());
	 
			}
 
		}
        return result; 
	}
	
	
	public int getCount()
	{
		int count = 0;
		
		Connection dbConnection = null;
		Statement stmt = null;
		
		String query = "SELECT COUNT(*) FROM ENTRIES";

		try {
			dbConnection = getDBConnection();
			stmt = dbConnection.createStatement();
			ResultSet rs=stmt.executeQuery(query);
 
			 while(rs.next()){
                 count=rs.getInt(1);                              
               }
             // close ResultSet rs
             rs.close(); 
             logger.info("SELECT COUNT(*) FROM ENTRIES = " + count);
 
		} catch (SQLException e) {
 
			logger.error(e.getMessage());
 
		} finally {
 
			try
			{
			  if (stmt != null) stmt.close();
  			  if (dbConnection != null) dbConnection.close();
			}
			catch(SQLException e) {
				 
				logger.error(e.getMessage());
	 
			}
 
		}
		
		
		return count;
	}
	
	private Connection getDBConnection() {
		 
		Connection dbConnection = null;
 
		loadConfiguration();
		
		try {
 
			Class.forName(DB_DRIVER);
 
		} catch (ClassNotFoundException e) {
 
			logger.error(e.getMessage());
 
		}
 
		try {
 
			dbConnection = DriverManager.getConnection(
                            DB_CONNECTION, DB_USER,DB_PASSWORD);
			return dbConnection;
 
		} catch (SQLException e) {
 
			logger.error(e.getMessage());
 
		}
 
		return dbConnection;
 
	}
	
	private boolean loadConfiguration()
	{
		PropertiesConfiguration config = null;
		try
		{
		    config = new PropertiesConfiguration("db.properties"); 

		    DB_DRIVER = config.getString("driver","org.postgresql.Driver");
			DB_CONNECTION = config.getString("url","jdbc:postgresql:nca");
			DB_USER = config.getString("username","postgres");
			DB_PASSWORD = config.getString("password","admin123");

		   logger.info("Loaded reader properties from db.properties");
		   return true;
	    }
		catch (org.apache.commons.configuration.ConfigurationException e)
		{
		  // do nothing just log and use defaults 
		  logger.warn("Unable to load the configuration db.properties file " + e.toString());
		  return false;
		}
	}
}
