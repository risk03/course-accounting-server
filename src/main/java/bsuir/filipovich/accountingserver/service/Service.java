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

    private static Session getSession() throws HibernateException {
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

    @Override
    public void create(String type, String[] strings) {
        switch (type) {
            case "user":
                creoUser(strings);
                break;
        }
    }

    @Override
    public void update(String type, String[] strings) {
        switch (type) {
            case "user":
                mutoUser(strings);
                break;

        }
    }

    @Override
    public void remove(String type, String id) {
        switch (type) {
            case "user":
                perdoUser(id);
                break;
        }
    }

    private void creoUser(String[] strings) {
        UserEntity user = new UserEntity();
        user.setUserId(Integer.parseInt(strings[0]));
        user.setSurname(strings[1]);
        user.setForename(strings[2]);
        user.setPatronymic(strings[3]);
        user.setRole(strings[4]);
        user.setLogin(strings[5]);
        user.setSalt(strings[6]);
        user.setPassword(strings[7]);
        Session session = getSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }

    private void mutoUser(String[] strings) {
        Session session = getSession();
        UserEntity user = session.load(UserEntity.class, Integer.parseInt(strings[0]));
        if (strings[1] != null)
            user.setSurname(strings[1]);
        if (strings[2] != null)
            user.setForename(strings[2]);
        if (strings[3] != null)
            user.setPatronymic(strings[3]);
        if (strings[4] != null)
            user.setRole(strings[4]);
        if (strings[5] != null)
            user.setLogin(strings[5]);
        if (strings[6] != null)
            user.setSalt(strings[6]);
        if (strings[7] != null)
            user.setPassword(strings[7]);
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }

    private void perdoUser(String id) {
        Session session = getSession();
        session.beginTransaction();
        UserEntity myObject = session.load(UserEntity.class, Integer.parseInt(id));
        session.delete(myObject);
        session.getTransaction().commit();
    }
}
