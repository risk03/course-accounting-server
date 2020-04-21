package bsuir.filipovich.accountingserver.service;

import bsuir.filipovich.accountingserver.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

@org.springframework.stereotype.Service
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

    public boolean login(String login, String password) {
        if (login.equals("login") && password.equals("password")) {
        }
        return true;
    }

    public void logout() {
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
            case "store":
                creoStore(strings);
                break;
            case "transaction":
                creoTransaction(strings);
                break;
            case "product":
                creoProduct(strings);
                break;
        }
    }

    @Override
    public ArrayList<String[]> readAll(String type) {
        ArrayList<String[]> arr = null;
        try (Session session = getSession()) {
            switch (type) {
                case "user":
                    arr = intellegoUsers(session);
                    break;
                case "store":
                    arr = intellegoStores(session);
                    break;
                case "product":
                    arr = intellegoProducts(session);
                    break;
                case "transaction":
                    arr = intellegoTransaction(session);
                    break;
            }
        }
        return arr;
    }

    @Override
    public ArrayList<String[]> readAll(String type, int id) {
        ArrayList<String[]> arr = null;
        try (Session session = getSession()) {
            switch (type) {
                case "assortment":
                    arr = intellegoAssortment(session, id);
                    break;
                case "transactionentry":
                    arr = intellegoEntry(session, id);
                    break;
            }
        }
        return arr;
    }

    @Override
    public String[] readOne(String type, int id) {
        String[] arr = null;
        try (Session session = getSession()) {
            switch (type) {
                case "store":
                    arr = intellegoStore(session, id);
                case "transaction":
                    arr = intellegoTransaction(session, id);
            }
        }
        return arr;
    }

    @Override
    public void update(String type, String[] strings) {
        switch (type) {
            case "user":
                mutoUser(strings);
                break;
            case "store":
                mutoStore(strings);
                break;
            case "transaction":
                mutoTransaction(strings);
                break;
            case "product":
                mutoProduct(strings);
                break;
        }
    }

    @Override
    public void remove(String type, String id) {
        switch (type) {
            case "user":
                perdoUser(id);
                break;
            case "store":
                perdoStore(id);
                break;
            case "transaction":
                perdoTransaction(id);
                break;
            case "product":
                perdoProduct(id);
        }
    }

    @Override
    public void setAssortment(String type, int root, int product, Double quantity) {
        switch (type) {
            case "store":
                setAssortmentStore(root, product, quantity);
                break;
            case "transaction":
                setAssortmentTransaction(root, product, quantity);
                break;
        }
    }

    private void setAssortmentStore(int store, int product, Double quantity) {
        Session session = getSession();
        StoreEntity storeEntity = session.load(StoreEntity.class, store);
        ProductEntity productEntity = session.load(ProductEntity.class, product);
        final Query query = session.createQuery("from StoreProductEntity where storeByStoreId = :store and productByProductId = :product");
        query.setParameter("store", storeEntity);
        query.setParameter("product", productEntity);
        for (Object o : query.list()) {
            session.remove(o);
        }
        if (quantity != 0) {
            StoreProductEntity newE = new StoreProductEntity();
            newE.setStoreByStoreId(storeEntity);
            newE.setProductByProductId(productEntity);
            newE.setQuantity(BigDecimal.valueOf(quantity));
            session.beginTransaction();
            session.persist(newE);
            session.getTransaction().commit();
        }
    }

    private void setAssortmentTransaction(int transaction, int product, Double quantity) {
        Session session = getSession();
        TransactionEntity transactionEntity = session.load(TransactionEntity.class, transaction);
        ProductEntity productEntity = session.load(ProductEntity.class, product);
        final Query query = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :transaction and productByProductId = :product");
        query.setParameter("transaction", transactionEntity);
        query.setParameter("product", productEntity);
        for (Object o : query.list()) {
            session.remove(o);
        }
        if (quantity != 0) {
            ProductTransactionEntity newE = new ProductTransactionEntity();
            newE.setTransactionByTransactionId(transactionEntity);
            newE.setProductByProductId(productEntity);
            newE.setQuantity(BigDecimal.valueOf(quantity));
            session.beginTransaction();
            session.persist(newE);
            session.getTransaction().commit();
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

    private void creoStore(String[] strings) {
        StoreEntity store = new StoreEntity();
        store.setStoreId(Integer.parseInt(strings[0]));
        store.setRegion(strings[1]);
        store.setCity(strings[2]);
        store.setStreet(strings[3]);
        store.setNumber(strings[4]);
        store.setBuilding(strings[5]);
        Session session = getSession();
        session.beginTransaction();
        session.save(store);
        session.getTransaction().commit();
    }

    private void creoTransaction(String[] strings) {
        Session session = getSession();
        TransactionEntity store = new TransactionEntity();
        store.setTransactionId(Integer.parseInt(strings[0]));
        store.setStoreByStoreId(session.load(StoreEntity.class, Integer.parseInt(strings[1])));
        store.setUserByUserId(session.load(UserEntity.class, Integer.parseInt(strings[2])));
        store.setDate(Timestamp.valueOf(strings[3]));
        session.beginTransaction();
        session.save(store);
        session.getTransaction().commit();
    }

    private void creoProduct(String[] strings) {
        ProductEntity product = new ProductEntity();
        product.setProductId(Integer.parseInt(strings[0]));
        product.setName(strings[1]);
        product.setSellingPrice(BigDecimal.valueOf(Float.parseFloat(strings[2])));
        product.setDescription(strings[3]);
        Session session = getSession();
        session.beginTransaction();
        session.save(product);
        session.getTransaction().commit();
    }

    private ArrayList<String[]> intellegoUsers(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from UserEntity");
        for (Object o : query.list()) {
            UserEntity user = (UserEntity) o;
            arr.add(new String[]{String.valueOf(user.getUserId()), user.getSurname(), user.getForename(), user.getPatronymic(), user.getRole(), user.getLogin()});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoStores(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from StoreEntity");
        for (Object o : query.list()) {
            StoreEntity store = (StoreEntity) o;
            arr.add(new String[]{String.valueOf(store.getStoreId()), store.getRegion(), store.getCity(), store.getStreet(), store.getNumber(), store.getBuilding()});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoProducts(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from ProductEntity");
        for (Object o : query.list()) {
            ProductEntity product = (ProductEntity) o;
            arr.add(new String[]{String.valueOf(product.getProductId()), product.getName(), product.getSellingPrice().toString(), product.getDescription()});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoTransaction(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from TransactionEntity");
        for (Object o : query.list()) {
            TransactionEntity product = (TransactionEntity) o;
            StringBuilder strbldr = new StringBuilder();
            UserEntity user = product.getUserByUserId();
            strbldr.append(user.getUserId());
            strbldr.append(" - ");
            strbldr.append(user.getSurname());
            strbldr.append(' ');
            strbldr.append(user.getForename().charAt(0));
            strbldr.append(". ");
            strbldr.append(user.getPatronymic().charAt(0));
            strbldr.append(". ");
            String nameStr = strbldr.toString();
            strbldr.setLength(0);
            StoreEntity store = product.getStoreByStoreId();
            strbldr.append(store.getStoreId());
            strbldr.append(" - ");
            strbldr.append(store.getCity());
            strbldr.append(' ');
            strbldr.append(store.getStreet());
            strbldr.append(' ');
            strbldr.append(store.getNumber());
            if (store.getBuilding() != null) {
                strbldr.append('/');
                strbldr.append(store.getBuilding());
            }
            String strdate = String.valueOf(product.getDate());
            arr.add(new String[]{String.valueOf(product.getTransactionId()),
                    strbldr.toString(),
                    nameStr,
                    strdate.substring(0, strdate.length() - 2)});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoAssortment(Session session, int id) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from StoreProductEntity where storeByStoreId = :id");
        query.setParameter("id", session.load(StoreEntity.class, id));
        for (Object o : query.list()) {
            StoreProductEntity entry = (StoreProductEntity) o;
            arr.add(new String[]{
                    entry.getProductByProductId().getProductId() + " - " + entry.getProductByProductId().getName(),
                    String.valueOf(entry.getQuantity())
            });
        }
        return arr;
    }

    private ArrayList<String[]> intellegoEntry(Session session, int id) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :id");
        query.setParameter("id", session.load(TransactionEntity.class, id));
        for (Object o : query.list()) {
            ProductTransactionEntity entry = (ProductTransactionEntity) o;
            arr.add(new String[]{
                    entry.getProductByProductId().getProductId() + " - " + entry.getProductByProductId().getName(),
                    String.valueOf(entry.getQuantity())
            });
        }
        return arr;
    }

    private String[] intellegoStore(Session session, int id) {
        String[] arr = null;
        final Query query = session.createQuery("from StoreEntity where storeId = :id");
        query.setParameter("id", id);
        for (Object o : query.list()) {
            StoreEntity store = (StoreEntity) o;
            arr = new String[]{
                    String.valueOf(store.getStoreId()),
                    store.getRegion(),
                    store.getCity(),
                    store.getStreet(),
                    store.getNumber(),
                    store.getBuilding()
            };
        }
        return arr;
    }

    private String[] intellegoTransaction(Session session, int id) {
        String[] arr = null;
        final Query query = session.createQuery("from TransactionEntity where transactionId = :id");
        query.setParameter("id", id);
        for (Object o : query.list()) {
            TransactionEntity transaction = (TransactionEntity) o;
            StoreEntity store = transaction.getStoreByStoreId();
            StringBuilder strbldr = new StringBuilder();
            strbldr.append("Магазин №");
            strbldr.append(store.getNumber());
            strbldr.append(" - ");
            strbldr.append(store.getCity());
            strbldr.append(' ');
            strbldr.append(store.getNumber());
            if (store.getBuilding() != null) {
                strbldr.append('/');
                strbldr.append(store.getBuilding());
            }
            String storestr = strbldr.toString();
            UserEntity user = transaction.getUserByUserId();
            strbldr.setLength(0);
            strbldr.append(user.getUserId());
            strbldr.append(" - ");
            strbldr.append(user.getSurname());
            strbldr.append(' ');
            strbldr.append(user.getForename().charAt(0));
            strbldr.append(". ");
            strbldr.append(user.getPatronymic().charAt(0));
            strbldr.append(". ");
            String nameStr = strbldr.toString();
            arr = new String[]{
                    String.valueOf(transaction.getTransactionId()),
                    storestr,
                    nameStr,
                    String.valueOf(transaction.getDate())
            };
        }
        return arr;
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

    private void mutoStore(String[] strings) {
        Session session = getSession();
        StoreEntity store = session.load(StoreEntity.class, Integer.parseInt(strings[0]));
        if (strings[1] != null)
            store.setRegion(strings[1]);
        if (strings[2] != null)
            store.setCity(strings[2]);
        if (strings[3] != null)
            store.setStreet(strings[3]);
        if (strings[4] != null)
            store.setNumber(strings[4]);
        if (strings[5] != null)
            store.setBuilding(strings[5]);
        session.beginTransaction();
        session.save(store);
        session.getTransaction().commit();
    }

    private void mutoTransaction(String[] strings) {
        Session session = getSession();
        TransactionEntity store = session.load(TransactionEntity.class, Integer.parseInt(strings[0]));
        if (strings[1] != null)
            store.setStoreByStoreId(session.load(StoreEntity.class, Integer.parseInt(strings[1])));
        if (strings[2] != null)
            store.setUserByUserId(session.load(UserEntity.class, Integer.parseInt(strings[2])));
        if (strings[3] != null)
            store.setDate(Timestamp.valueOf(strings[3]));
        session.beginTransaction();
        session.save(store);
        session.getTransaction().commit();
    }

    private void mutoProduct(String[] strings) {
        Session session = getSession();
        ProductEntity product = session.load(ProductEntity.class, Integer.parseInt(strings[0]));
        if (strings[1] != null)
            product.setName(strings[1]);
        if (strings[2] != null)
            product.setSellingPrice(BigDecimal.valueOf(Float.parseFloat(strings[2])));
        if (strings[3] != null)
            product.setDescription(strings[3]);
        session.beginTransaction();
        session.save(product);
        session.getTransaction().commit();
    }

    private void perdoUser(String id) {
        Session session = getSession();
        session.beginTransaction();
        UserEntity user = session.load(UserEntity.class, Integer.parseInt(id));
        session.delete(user);
        session.getTransaction().commit();
    }

    private void perdoStore(String id) {
        Session session = getSession();
        session.beginTransaction();
        StoreEntity store = session.load(StoreEntity.class, Integer.parseInt(id));
        session.delete(store);
        session.getTransaction().commit();
    }

    private void perdoTransaction(String id) {
        Session session = getSession();
        session.beginTransaction();
        TransactionEntity store = session.load(TransactionEntity.class, Integer.parseInt(id));
        session.delete(store);
        session.getTransaction().commit();
    }

    private void perdoProduct(String id) {
        Session session = getSession();
        session.beginTransaction();
        ProductEntity product = session.load(ProductEntity.class, Integer.parseInt(id));
        session.delete(product);
        session.getTransaction().commit();
    }

}
