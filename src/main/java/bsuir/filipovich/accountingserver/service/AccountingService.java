package bsuir.filipovich.accountingserver.service;

import bsuir.filipovich.accountingserver.entities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
@Service
public class AccountingService implements IService {

    private final LoginBean loggedUser;

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
            UserEntity user = getSession().load(UserEntity.class, Integer.parseInt(id));
            return new String[]{String.valueOf(user.getUserId()), user.getSurname(), user.getForename(), user.getPatronymic(), user.getRole(), user.getLogin()};
        }
    }

    @Override
    public boolean create(String type, String[] strings) {
        switch (type) {
            case "user":
                if (getLoggedUser()[4].equals("Заведующий")) {
                    creoUser(strings);
                    return true;
                } else
                    return false;
            case "store":
                if (getLoggedUser()[4].equals("Заведующий")) {
                    creoStore(strings);
                    return true;
                } else
                    return false;
            case "transaction":
                creoTransaction(strings);
                return true;
            case "product":
                if (getLoggedUser()[4].equals("Заведующий")) {
                    creoProduct(strings);
                    return true;
                } else
                    return false;
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
                case "assortment":
                    arr = intellegoAssortments(session);
                    break;
                case "entry":
                    arr = intellegoEntries(session);
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
                    arr = intellegoTransaction(session, id);
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
        if (getLoggedUser() == null)
            return false;
        switch (type) {
            case "user":
                if (!getLoggedUser()[4].equals("Заведующий") || id.equals(getLoggedUser()[0]))
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
            case "entry":
                if (getLoggedUser() == null)
                    return false;
                else {
                    perdoEntry(id);
                    return false;
                }
            case "assortment":
                if (!getLoggedUser()[4].equals("Заведующий"))
                    return false;
                else {
                    perdoAssortment(id);
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
                    return setEntry(root, product, quantity);
                } else
                    return false;
        }
        return false;
    }

    @Override
    public String[] showWhere(String id) {
        if (getLoggedUser() == null)
            return null;
        Session session = getSession();
        Query query = session.createQuery("from StoreProductEntity where productByProductId = :product");
        query.setParameter("product", session.load(ProductEntity.class, Integer.parseInt(id)));
        List l = query.list();
        String[] arr = new String[l.size()];
        for (int i = 0; i < l.size(); i++) {
            arr[i] = String.valueOf(((StoreProductEntity) l.get(i)).getStoreByStoreId().getStoreId());
        }
        return arr;
    }

    @Override
    public ArrayList<String[]> getSalesByStoreReport(String from, String to) {
        Session session = getSession();
        if (getLoggedUser() == null || !getLoggedUser()[4].equals("Заведующий")) {
            return null;
        }
        ArrayList<String[]> arr = new ArrayList<>();
        double sum = 0;
        Query storeQuery = session.createQuery("from StoreEntity ");
        for (Object s : storeQuery.list()) {
            StoreEntity store = (StoreEntity) s;
            Query transactionQuery = session.createQuery("from TransactionEntity where date between :fr and :to and storeByStoreId = :store");
            transactionQuery.setParameter("store", session.load(StoreEntity.class, store.getStoreId()));
            transactionQuery.setParameter("fr", Timestamp.valueOf(from));
            transactionQuery.setParameter("to", Timestamp.valueOf(to));
            double storeSum = 0;
            for (Object t : transactionQuery.list()) {
                TransactionEntity transaction = (TransactionEntity) t;
                Query entryQuery = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :transaction");
                entryQuery.setParameter("transaction", transaction);
                for (Object e : entryQuery.list()) {
                    ProductTransactionEntity entry = (ProductTransactionEntity) e;
                    double entrySum = entry.getQuantity().multiply(entry.getProductByProductId().getSellingPrice()).doubleValue();
                    storeSum += entrySum;
                    sum += entrySum;
                }
            }
            arr.add(new String[]{String.valueOf(store.getStoreId()), String.valueOf(storeSum)});
        }
        for (int i = 0; i < arr.size(); i++) {
            arr.set(i, new String[]{arr.get(i)[0], arr.get(i)[1], String.valueOf(Double.parseDouble(arr.get(i)[1]) / sum)});
        }
        return arr;
    }

    @Override
    public ArrayList<String[]> getSalesByProductReport(String from, String to) {
        Session session = getSession();
        if (getLoggedUser() == null || !getLoggedUser()[4].equals("Заведующий")) {
            return null;
        }
        ArrayList<String[]> arr = new ArrayList<>();
        double sum = 0;
        Query transactionQuery = session.createQuery("from TransactionEntity where date between :fr and :to");
        transactionQuery.setParameter("fr", Timestamp.valueOf(from));
        transactionQuery.setParameter("to", Timestamp.valueOf(to));
        List transactions = transactionQuery.list();
        Query productQuery = session.createQuery("from ProductEntity");
        for (Object p : productQuery.list()) {
            ProductEntity product = (ProductEntity) p;
            Query entryQuery = session.createQuery("from ProductTransactionEntity where productByProductId = :product and transactionByTransactionId in :transactions");
            entryQuery.setParameter("product", product);
            entryQuery.setParameterList("transactions", transactions);
            double productSum = 0;
            for (Object e : entryQuery.list()) {
                ProductTransactionEntity entry = (ProductTransactionEntity) e;
                productSum += entry.getQuantity().multiply(entry.getProductByProductId().getSellingPrice()).doubleValue();
            }
            arr.add(new String[]{String.valueOf(product.getProductId()), String.valueOf(entryQuery.list().size()), String.valueOf(productSum)});
            sum += productSum;
        }
        for (int i = 0; i < arr.size(); i++) {
            arr.set(i, new String[]{arr.get(i)[0], arr.get(i)[1], arr.get(i)[2], String.valueOf(Double.parseDouble(arr.get(i)[2]) / sum)});
        }
        return arr;
    }

    @Override
    public ArrayList<String[]> getSalesByCashierReport(String from, String to) {
        Session session = getSession();
        if (getLoggedUser() == null || !getLoggedUser()[4].equals("Заведующий")) {
            return null;
        }
        ArrayList<String[]> arr = new ArrayList<>();
        Query cashierQuery = session.createQuery("from UserEntity");
        for (Object c : cashierQuery.list()) {
            UserEntity cashier = (UserEntity) c;
            Query transactionQuery = session.createQuery("from TransactionEntity where date between :fr and :to and userByUserId = :user");
            transactionQuery.setParameter("fr", Timestamp.valueOf(from));
            transactionQuery.setParameter("to", Timestamp.valueOf(to));
            transactionQuery.setParameter("user", cashier);
            List transactions = transactionQuery.list();
            double sum = 0;
            for (Object t : transactions) {
                TransactionEntity transaction = (TransactionEntity) t;
                Query entryQuery = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :transaction");
                entryQuery.setParameter("transaction", transaction);
                for (Object e : entryQuery.list()) {
                    ProductTransactionEntity entry = (ProductTransactionEntity) e;
                    sum += entry.getQuantity().multiply(entry.getProductByProductId().getSellingPrice()).doubleValue();
                }
            }
            arr.add(new String[]{String.valueOf(((UserEntity) c).getUserId()), String.valueOf(transactions.size()), String.valueOf(sum)});
        }
        return arr;
    }

    @Override
    public String toCur(String value, String currency) {
        String s = null;
        switch (currency) {
            case "USD":
                s = sendGETRequest("145");
                break;
            case "EUR":
                s = sendGETRequest("292");
                break;
        }
        if (s == null)
            return null;
        String rate = s.substring(s.lastIndexOf(":") + 1, s.lastIndexOf("}"));
        return String.valueOf(Double.parseDouble(value) / Double.parseDouble(rate));
    }

    private String sendGETRequest(String currCode) {
        try {
            URL obj = new URL("https://www.nbrb.by/api/exrates/rates/" + currCode);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void setAssortment(String store, String product, String quantity) {
        Session session = getSession();
        StoreEntity storeEntity = session.load(StoreEntity.class, Integer.parseInt(store));
        ProductEntity productEntity = session.load(ProductEntity.class, Integer.parseInt(product));
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

    private boolean setEntry(String transaction, String product, String quantity) {
        Session session = getSession();
        TransactionEntity transactionEntity = session.load(TransactionEntity.class, Integer.parseInt(transaction));
        ProductEntity productEntity = session.load(ProductEntity.class, Integer.parseInt(product));
        Query existingQuery = session.createQuery("from ProductTransactionEntity where productByProductId = :product and transactionByTransactionId = :transaction");
        existingQuery.setParameter("product", productEntity);
        existingQuery.setParameter("transaction", transactionEntity);
        BigDecimal productsInTransaction = new BigDecimal(0);
        for (Object o : existingQuery.list()) {
            ProductTransactionEntity entry = (ProductTransactionEntity) o;
            productsInTransaction = productsInTransaction.add(entry.getQuantity());
        }
        Query storeQuery = session.createQuery("from StoreProductEntity where storeByStoreId = :store and productByProductId = :product");
        storeQuery.setParameter("store", transactionEntity.getStoreByStoreId());
        storeQuery.setParameter("product", productEntity);
        BigDecimal productsInStore = new BigDecimal(0);
        for (Object o : storeQuery.list()) {
            StoreProductEntity entry = (StoreProductEntity) o;
            productsInStore = productsInStore.add(entry.getQuantity());
        }
        if (productsInTransaction.add(productsInStore).compareTo(BigDecimal.valueOf(Double.parseDouble(quantity))) < 0) {
            return false;
        } else {
            session.beginTransaction();
            for (Object o : existingQuery.list())
                session.remove(o);
        }
        session.getTransaction().commit();
        double lQuantity = Double.parseDouble(quantity);
        if (lQuantity != 0) {
            ProductTransactionEntity newE = new ProductTransactionEntity();
            newE.setTransactionByTransactionId(transactionEntity);
            newE.setProductByProductId(productEntity);
            newE.setQuantity(BigDecimal.valueOf(lQuantity));
            session.beginTransaction();
            session.persist(newE);
            session.getTransaction().commit();
        }
        this.setAssortment(String.valueOf(transactionEntity.getStoreByStoreId().getStoreId()), String.valueOf(productEntity.getProductId()), String.valueOf(productsInTransaction.add(productsInStore).subtract(BigDecimal.valueOf(Double.parseDouble(quantity)))));
        return true;
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

    private ArrayList<String[]> intellegoProducts(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from ProductEntity");
        for (Object o : query.list()) {
            ProductEntity product = (ProductEntity) o;
            arr.add(new String[]{String.valueOf(product.getProductId()), product.getName(), product.getSellingPrice().toString(), product.getDescription()});
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

    private ArrayList<String[]> intellegoStores(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from StoreEntity");
        for (Object o : query.list()) {
            StoreEntity store = (StoreEntity) o;
            arr.add(new String[]{String.valueOf(store.getStoreId()), store.getRegion().equals("") ? null : store.getRegion(), store.getCity(), store.getStreet(), store.getNumber(), store.getBuilding().equals("") ? null : store.getBuilding()});
        }
        return arr;
    }

    private String[] intellegoTransaction(Session session, String id) {
        String[] arr = null;
        final Query query = session.createQuery("from TransactionEntity where transactionId = :id");
        query.setParameter("id", Integer.parseInt(id));
        for (Object o : query.list()) {
            TransactionEntity transaction = (TransactionEntity) o;
            arr = new String[]{String.valueOf(transaction.getTransactionId()), String.valueOf(transaction.getStoreByStoreId().getStoreId()), String.valueOf(transaction.getUserByUserId().getUserId()), String.valueOf(transaction.getDate())};
        }
        return arr;
    }

    private ArrayList<String[]> intellegoTransactions(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from TransactionEntity");
        for (Object o : query.list()) {
            TransactionEntity transaction = (TransactionEntity) o;
            arr.add(new String[]{String.valueOf(transaction.getTransactionId()), String.valueOf(transaction.getStoreByStoreId().getStoreId()), String.valueOf(transaction.getUserByUserId().getUserId()), String.valueOf(transaction.getDate())});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoAssortment(Session session, String id) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from StoreProductEntity where storeByStoreId = :id");
        query.setParameter("id", session.load(StoreEntity.class, Integer.parseInt(id)));
        for (Object o : query.list()) {
            StoreProductEntity entry = (StoreProductEntity) o;
            arr.add(new String[]{String.valueOf(entry.getStoreProductId()), String.valueOf(entry.getStoreByStoreId()), String.valueOf(entry.getProductByProductId()), String.valueOf(entry.getQuantity())});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoAssortments(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        Query query = session.createQuery("from StoreProductEntity");
        for (Object o : query.list()) {
            StoreProductEntity entry = (StoreProductEntity) o;
            arr.add(new String[]{String.valueOf(entry.getStoreProductId()), String.valueOf(entry.getStoreByStoreId().getStoreId()), String.valueOf(entry.getProductByProductId().getProductId()), String.valueOf(entry.getQuantity())});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoEntry(Session session, String id) {
        ArrayList<String[]> arr = new ArrayList<>();
        final Query query = session.createQuery("from ProductTransactionEntity where transactionByTransactionId = :id");
        query.setParameter("id", session.load(TransactionEntity.class, Integer.parseInt(id)));
        for (Object o : query.list()) {
            ProductTransactionEntity entry = (ProductTransactionEntity) o;
            arr.add(new String[]{String.valueOf(entry.getProductTransactionId()), String.valueOf(entry.getTransactionByTransactionId().getTransactionId()), String.valueOf(entry.getProductByProductId().getProductId()), String.valueOf(entry.getQuantity())});
        }
        return arr;
    }

    private ArrayList<String[]> intellegoEntries(Session session) {
        ArrayList<String[]> arr = new ArrayList<>();
        Query query = session.createQuery("from ProductTransactionEntity");
        for (Object o : query.list()) {
            ProductTransactionEntity entry = (ProductTransactionEntity) o;
            arr.add(new String[]{String.valueOf(entry.getProductTransactionId()), String.valueOf(entry.getTransactionByTransactionId().getTransactionId()), String.valueOf(entry.getProductByProductId().getProductId()), String.valueOf(entry.getQuantity())});
        }
        return arr;
    }

    private void mutoUser(String[] strings) {
        Session session = getSession();
        UserEntity user = session.load(UserEntity.class, Integer.parseInt(strings[0]));
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

    private void perdoEntry(String id) {
        Session session = getSession();
        session.beginTransaction();
        ProductTransactionEntity product = session.load(ProductTransactionEntity.class, Integer.parseInt(id));
        session.delete(product);
        session.getTransaction().commit();
    }

    private void perdoAssortment(String id) {
        Session session = getSession();
        session.beginTransaction();
        StoreProductEntity product = session.load(StoreProductEntity.class, Integer.parseInt(id));
        session.delete(product);
        session.getTransaction().commit();
    }
}
