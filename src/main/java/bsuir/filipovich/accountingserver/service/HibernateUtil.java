package bsuir.filipovich.accountingserver.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

class HibernateUtil {
    private final static SessionFactory sf = new Configuration()
            .configure("hibernate.cfg.xml").buildSessionFactory();

    static Session getHibernateSession() {
        return sf.openSession();
    }
}
