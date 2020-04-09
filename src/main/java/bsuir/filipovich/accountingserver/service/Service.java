package bsuir.filipovich.accountingserver.service;

import bsuir.filipovich.accountingserver.entities.UserEntity;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;

public class Service implements IService {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    @Override
    public ArrayList<String[]> getUserList() {
        ArrayList<String[]> arr = new ArrayList<>();
        try (Session session = getSession()) {
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            final Query query = session.createQuery("from UserEntity");
            for (Object o : query.list()) {
                UserEntity user = (UserEntity) o;
                arr.add(new String[]{String.valueOf(user.getUserId()), user.getSurname(), user.getForename(), user.getPatronymic(), user.getRole(), user.getLogin()});
            }
        }
        return arr;
    }

    @Override
    public String getMessage() {
        return "zankoku no tenshi";
    }
}
