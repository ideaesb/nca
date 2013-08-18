package org.pdc.nca.dao;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

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

    public EntryDao()
    {
      setSessionFactory();
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
	
}
