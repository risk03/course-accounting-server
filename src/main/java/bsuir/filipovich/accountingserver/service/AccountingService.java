package bsuir.filipovich.accountingserver.service;

import bsuir.filipovich.accountingserver.entities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountingService implements IService {

    private LoginBean loggedUser;

    @Autowired
    public AccountingService(LoginBean loggedUser) {
        this.loggedUser = loggedUser;
    }

    private static Session getSession() {
        return HibernateUtil.getHibernateSession();
    }

    private String getHash(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }
        assert md != null;
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

    public boolean login(String login, String password) {
        if (this.loggedUser.getUser() != null) {
            return false;
        } else {
            Query query = getSession().createQuery("from UserEntity where login = :login");
            query.setParameter("login", login);
            List l = query.list();
            if (l.size() == 0) {
                return false;
            } else {
                UserEntity user = (UserEntity) l.get(0);
                String hash = this.getHash(password + user.getSalt());
                if (user.getPassword().equals(hash)) {
                    this.loggedUser.setUser(String.valueOf(user.getUserId()));
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public void logout() {
        this.loggedUser.setUser(null);
    }

    @Override
    public String[] getLoggedUser() {
        String id = this.loggedUser.getUser();
        if (id == null) {
            return null;
        } else {
            UserEntity user = (UserEntity) getSession().load(UserEntity.class, Integer.parseInt(id));
            return new String[]{String.valueOf(user.getUserId()), user.getSurname(), user.getForename(), user.getPatronymic(), user.getRole(), user.getLogin()};
        }
    }

    @Override
    public String getMessage() {
        return "zankoku no tenshi";
    }

    @Override
    public boolean create(String type, String[] strings) {
        switch (type) {
            case "user":
                if (getLoggedUser()[4].equals("Заведующий"))
                    creoUser(strings);
                else
                    return false;
                break;
            case "store":
                if (getLoggedUser()[4].equals("Заведующий"))
                    creoStore(strings);
                else
                    return false;
                break;
            case "transaction":
                creoTransaction(strings);
                break;
            case "product":
                if (getLoggedUser()[4].equals("Заведующий"))
                    creoProduct(strings);
                else
                    return false;
                break;
        }
        return false;
    }

    @Override
    public ArrayList<String[]> readAll(String type) {
        if (getLoggedUser() == null)
            return null;
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
                    arr = intellegoTransactions(session);
                    break;
            }
        }
        return arr;
    }

    @Override
    public ArrayList<String[]> readAll(String type, String id) {
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
    public String[] readOne(String type, String id) {
        String[] arr = null;
        try (Session session = getSession()) {
            switch (type) {
                case "product":
                    arr = intellegoProduct(session, id);
                    break;
                case "user":
                    arr = intellegoUser(session, id);
                    break;
                case "store":
                    arr = intellegoStore(session, id);
                    break;
                case "transaction":
                    arr = intellegoTransactions(session, id);
                    break;
            }
        }
        return arr;
    }

    @Override
    public boolean update(String type, String[] strings) {
        switch (type) {
            case "user":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    mutoUser(strings);
                    return true;
                }
            case "store":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    mutoStore(strings);
                    return true;
                }
            case "transaction":
                if (getLoggedUser() == null)
                    return false;
                else {
                    mutoTransaction(strings);
                    return true;
                }
            case "product":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    mutoProduct(strings);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean remove(String type, String id) {
        switch (type) {
            case "user":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    perdoUser(id);
                    return true;
                }
            case "store":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    perdoStore(id);
                    return true;
                }
            case "transaction":
                if (getLoggedUser() == null)
                    return false;
                else {
                    perdoTransaction(id);
                    return true;
                }
            case "product":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    perdoProduct(id);
                    return false;
                }
        }
        return false;
    }

    @Override
    public boolean setManyToMany(String type, String root, String product, String quantity) {
        switch (type) {
            case "assortment":
                if (getLoggedUser() != null && getLoggedUser()[4].equals("Заведующий")) {
                    setAssortment(root, product, quantity);
                    return true;
                } else
                    return false;
            case "entry":
                if (getLoggedUser() != null) {
                    setEntry(root, product, quantity);
                    return true;
                } else
                    return false;
        }
        return false;
    }

    private void setAssortment(String store, String product, String quantity) {
        Session session = getSession();
        StoreEntity storeEntity = (StoreEntity) session.load(StoreEntity.class, Integer.parseInt(store));
        ProductEntity productEntity = (ProductEntity) session.load(ProductEntity.class, Integer.parseInt(product));
        Query query = session.createQuery("from StoreProductEntity where storeByStoreId = :store and productByProductId = :product");
        query.setParameter("store", storeEntity);
        query.setParameter("product", productEntity);
        for (Object o : query.list()) {
            session.remove(o);
        }
        double dQuantity = Double.parseDouble(quantity);
        if (dQuantity != 0) {
            StoreProductEntity newE = new StoreProductEntity();
            newE.setStoreByStoreId(storeEntity);
            newE.setProductByProductId(productEntity);
            newE.setQuantity(BigDecimal.valueOf(dQuantity));
            session.beginTransaction();
            session.persist(newE);
            session.getTransaction().commit();
        }
    }

    private void setEntry(String transaction, String product, String quantity) {
        Session session = getSession();
        TransactionEntity transactionEntity = session.load(TransactionEntity.class, transaction);
        ProductEntity productEntity = session.load(ProductEntity.class, product);
        final Query query = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :transaction and productByProductId = :product");
        query.setParameter("transaction", transactionEntity);
        query.setParameter("product", productEntity);
        for (Object o : query.list()) {
            session.remove(o);
        }
        double dQuantity = Double.parseDouble(quantity);
        if (dQuantity != 0) {
            ProductTransactionEntity newE = new ProductTransactionEntity();
            newE.setTransactionByTransactionId(transactionEntity);
            newE.setProductByProductId(productEntity);
            newE.setQuantity(BigDecimal.valueOf(dQuantity));
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
        String salt = getHash((new Date()).toString());
        user.setSalt(salt);
        user.setPassword(getHash(strings[6] + salt));
        Session session = getSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }

    private void creoStore(String[] strings) {
        StoreEntity store = new StoreEntity();
        if (strings[0] != null) {
            store.setStoreId(Integer.parseInt(strings[0]));
        }
        if (strings[1] != null) {
            store.setRegion(strings[1]);
        } else {
            store.setRegion("");
        }
        store.setCity(strings[2]);
        store.setStreet(strings[3]);
        store.setNumber(strings[4]);
        if (strings[5] != null) {
            store.setBuilding(strings[5]);
        } else {
            store.setBuilding("");
        }
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

    private String[] intellegoProduct(Session session, String id) {
        String[] arr = null;
        Query query = session.createQuery("from ProductEntity where productId = :id");
        query.setParameter("id", Integer.parseInt(id));
        ProductEntity product;
        for (Object o : query.list()) {
            product = (ProductEntity) o;
            arr = new String[]{String.valueOf(product.getProductId()), product.getName(), String.valueOf(product.getSellingPrice()), product.getDescription()};
        }
        return arr;
    }

    private String[] intellegoUser(Session session, String id) {
        String[] arr = null;
        Query query = session.createQuery("from UserEntity where userId = :id");
        query.setParameter("id", Integer.parseInt(id));
        UserEntity user;
        for (Object o : query.list()) {
            user = (UserEntity) o;
            arr = new String[]{String.valueOf(user.getUserId()), user.getSurname(), user.getForename(), user.getPatronymic(), user.getRole(), user.getLogin(), user.getSalt(), user.getPassword()};
        }
        return arr;
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
            arr.add(new String[]{String.valueOf(store.getStoreId()), store.getRegion().equals("") ? null : store.getRegion(), store.getCity(), store.getStreet(), store.getNumber(), store.getBuilding().equals("") ? null : store.getBuilding()});
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

    private ArrayList<String[]> intellegoTransactions(Session session) {
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

    private ArrayList<String[]> intellegoAssortment(Session session, String id) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from StoreProductEntity where storeByStoreId = :id");
        query.setParameter("id", session.load(StoreEntity.class, Integer.parseInt(id)));
        for (Object o : query.list()) {
            StoreProductEntity entry = (StoreProductEntity) o;
            arr.add(new String[]{
                    entry.getProductByProductId().getProductId() + " - " + entry.getProductByProductId().getName(),
                    String.valueOf(entry.getQuantity())
            });
        }
        return arr;
    }

    private ArrayList<String[]> intellegoEntry(Session session, String id) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :id");
        query.setParameter("id", session.load(TransactionEntity.class, Integer.parseInt(id)));
        for (Object o : query.list()) {
            ProductTransactionEntity entry = (ProductTransactionEntity) o;
            arr.add(new String[]{
                    entry.getProductByProductId().getProductId() + " - " + entry.getProductByProductId().getName(),
                    String.valueOf(entry.getQuantity())
            });
        }
        return arr;
    }

    private String[] intellegoStore(Session session, String id) {
        String[] arr = null;
        final Query query = session.createQuery("from StoreEntity where storeId = :id");
        query.setParameter("id", Integer.parseInt(id));
        for (Object o : query.list()) {
            StoreEntity store = (StoreEntity) o;
            arr = new String[]{String.valueOf(store.getStoreId()), store.getRegion().equals("") ? null : store.getRegion(), store.getCity(), store.getStreet(), store.getNumber(), store.getBuilding().equals("") ? null : store.getBuilding()};
        }
        return arr;
    }

    private String[] intellegoTransactions(Session session, String id) {
        String[] arr = null;
        final Query query = session.createQuery("from TransactionEntity where transactionId = :id");
        query.setParameter("id", Integer.parseInt(id));
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
        UserEntity user = (UserEntity) session.load(UserEntity.class, Integer.parseInt(strings[0]));
        if (strings[1] != null) {
            user.setSurname(strings[1]);
        }
        if (strings[2] != null) {
            user.setForename(strings[2]);
        }
        if (strings[3] != null) {
            user.setPatronymic(strings[3]);
        }
        if (strings[4] != null) {
            user.setRole(strings[4]);
        }
        if (strings[5] != null) {
            user.setLogin(strings[5]);
        }
        if (strings[6] != null) {
            user.setPassword(this.getHash(strings[6] + user.getSalt()));
        }
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
