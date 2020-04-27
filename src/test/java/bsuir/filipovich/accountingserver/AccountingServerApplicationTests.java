package bsuir.filipovich.accountingserver;

import bsuir.filipovich.accountingserver.service.IService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("unused")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class AccountingServerApplicationTests {
    @Autowired
    private IService service;

    private final String[] DEFAULT_ADMIN = new String[]{"1", "Фамилия", "Имя", "Отчество", "Заведующий", "admin", "password"};
    private final String ADMIN_LOGIN = "risk03";
    private final String ADMIN_PASSWORD = "wasd";
    private final String[][] STORE = new String[][]{{"1", null, "г. Минск", "ул. Платонова", "39", null}, {"2", null, "г. Минск", "ул. Берута", "9", "3"}, {"3", "Могилёвская обл.", "г. Бобруск", "ул. Чайковского", "2", "1"}};
    private final String[][] PRODUCT = new String[][]{{"1", "Ручка шариковая синяя \"Classic Stick\" (1,0 мм)", "1.30", "Шариковая ручка с прозрачным корпусом и колпачком. Цвет стержня: синий."}, {"2", "Ручка шариковая чёрная \"Classic Stick\" (1,0 мм)", "1.35", "Шариковая ручка с прозрачным корпусом и колпачком. Цвет стержня: чёрный."}, {"3", "Диск с учебной программой \"Правила дорожного движения 2020\"", "15.50", "Учебная программа предназначена для изучения Правил дорожного движения Республики Беларусь и подготовки будущих водителей транспортных средств категорий «B» и «C» к теоретическому экзамену ГАИ."}, {"4", "Ластик \"Elephant 300/80\"", "0.75", "Ластик Elephant 300/80 изготовлен из натурального материала. Применяется для стирания карандашных записей и рисунков. Эластичный и мягкий ластик не пачкает бумагу и не оставляет следов."}, {"5", "Тетрадь полуобщая в клетку \"Паттерн\" (48 листов)", "1.80", "Товар представлен в ассортименте, отгрузка осуществляется произвольно."}};
    private final String[] CASHIER = new String[]{"2", "Омаров", "Леонид", "Сергеевич", "Кассир", "Oules", "wasd"};
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            System.exit(-1);
        }
    };
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Ignore
    @Order(1)
    void loginTest() {
        Assert.assertNull(service.getLoggedUser());
        service.login(DEFAULT_ADMIN[5], DEFAULT_ADMIN[6]);
        Assert.assertEquals(service.getLoggedUser()[5], DEFAULT_ADMIN[5]);
        service.logout();
    }

    @Ignore
    @Order(2)
    void changeAdmin() {
        System.out.println(service.getLoggedUser() != null);
        Assert.assertTrue(service.login(DEFAULT_ADMIN[5], DEFAULT_ADMIN[6]));
        String adminId = service.getLoggedUser()[0];
        String ADMIN_PATRONYMIC = "Викторович";
        String ADMIN_FORENAME = "Виктор";
        String ADMIN_SURNAME = "Филиппович";
        Assert.assertTrue(service.update("user", new String[]{adminId, ADMIN_SURNAME, ADMIN_FORENAME, ADMIN_PATRONYMIC, null, ADMIN_LOGIN, ADMIN_PASSWORD}));
        String[] user = service.readOne("user", adminId);
        Assert.assertEquals(ADMIN_SURNAME, user[1]);
        Assert.assertEquals(ADMIN_FORENAME, user[2]);
        Assert.assertEquals(ADMIN_PATRONYMIC, user[3]);
        Assert.assertEquals(ADMIN_LOGIN, user[5]);
        service.logout();
    }

    @Ignore
    @Order(3)
    void createStore() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        for (String[] store : STORE) {
            service.create("store", store);
        }
        ArrayList<String[]> stores = new ArrayList<>();
        for (int i = 0; i < STORE.length; ++i) {
            stores.add(service.readOne("store", String.valueOf(i + 1)));
        }
        for (int i = 0; i < STORE.length; ++i) {
            for (int j = 0; j < STORE[0].length; ++j) {
                Assert.assertEquals(STORE[i][j], stores.get(i)[j]);
            }
        }
        service.logout();
    }

    @Ignore
    @Order(4)
    void createProduct() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        for (String[] product : PRODUCT) {
            service.create("product", product);
        }
        ArrayList<String[]> product = new ArrayList<>();
        for (int i = 0; i < PRODUCT.length; ++i) {
            product.add(service.readOne("product", String.valueOf(i + 1)));
        }
        for (int i = 0; i < PRODUCT.length; ++i) {
            for (int j = 0; j < PRODUCT[0].length; ++j) {
                Assert.assertEquals(PRODUCT[i][j], product.get(i)[j]);
            }
        }
        service.logout();
    }

    @Ignore
    @Order(5)
    void fillStore() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        for (int store = 0; store < STORE.length; ++store) {
            for (int product = 0; product < PRODUCT.length; ++product) {
                service.setManyToMany("assortment", String.valueOf(store + 1), String.valueOf(product + 1), String.valueOf(2 * store + product));
            }
        }
        service.logout();
    }

    @Ignore
    @Order(6)
    void createCashier() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        service.create("user", CASHIER);
        String[] user = service.readOne("user", CASHIER[0]);
        for (int i = 0; i < 6; ++i) {
            Assert.assertEquals(CASHIER[i], user[i]);
        }
        service.logout();
    }

    @Ignore
    @Order(7)
    void logInLogOut() {
        Assert.assertNull(service.getLoggedUser());
        Assert.assertTrue(service.login("risk03", "wasd"));
        Assert.assertFalse(service.login(CASHIER[5], CASHIER[6]));
        service.logout();
        Assert.assertTrue(service.login("Oules", "wasd"));
        service.logout();
    }

    @Ignore
    @Order(8)
    void someTransactions() {
        Assert.assertTrue(service.login(CASHIER[5], CASHIER[6]));
        int j = 1;
        int NUM_OF_TRANSACTIONS = 9;
        for (int i = 0; i < NUM_OF_TRANSACTIONS; i++) {
            if (j > STORE.length) {
                j = 1;
            }
            Date date = new Date();
            Assert.assertTrue(service.create("transaction", new String[]{String.valueOf(i + 1), String.valueOf(j), service.getLoggedUser()[0], dateFormat.format(date)}));
            j++;
        }
        for (int i = 0; i < STORE.length; i++) {
            for (int k = 0; k < PRODUCT.length; k++) {
                Assert.assertTrue(service.setManyToMany("entry", String.valueOf(i + 1), String.valueOf(k + 1), String.valueOf(i + k)));
            }
        }
        service.logout();
    }

    @Ignore
    @Order(9)
    void showAll() {
        Assert.assertTrue(service.login("risk03", "wasd"));
        String[] tables = new String[]{"user", "store", "product", "transaction", "assortment", "entry"};
        for (String what : tables) {
            System.out.println(what + ':');
            ArrayList<String[]> rs = service.readAll(what);
            Assert.assertNotNull(rs);
            for (String[] tr : rs) {
                for (String td : tr) {
                    System.out.print(td + ' ');
                }
                System.out.println();
            }
        }
        service.logout();
    }

    @Ignore
    @Order(10)
    void showWhere() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        for (String[] strings : PRODUCT) {
            for (String id : service.showWhere(strings[0])) {
                String[] store = service.readOne("store", id);
                System.out.println(store[2] + ' ' + store[3] + ' ' + store[4]);
            }
        }
        service.logout();
    }

    @Ignore
    @Order(11)
    void getSalesByStoreReport() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = c.getTime();
        ArrayList<String[]> rs = service.getSalesByStoreReport(dateFormat.format(yesterday), dateFormat.format(today));
        Assert.assertNotNull(rs);
        for (String[] row : rs) {
            for (String td : row) {
                System.out.print(td);
                System.out.print(' ');
            }
            System.out.println();
        }
        service.logout();
    }

    @Ignore
    @Order(12)
    void getSalesByProductReport() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = c.getTime();
        ArrayList<String[]> rs = service.getSalesByProductReport(dateFormat.format(yesterday), dateFormat.format(today));
        Assert.assertNotNull(rs);
        for (String[] row : rs) {
            for (String td : row) {
                System.out.print(td);
                System.out.print(' ');
            }
            System.out.println();
        }
        service.logout();
    }

    @Ignore
    @Order(13)
    void getSalesByCashierReport() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = c.getTime();
        ArrayList<String[]> rs = service.getSalesByCashierReport(dateFormat.format(yesterday), dateFormat.format(today));
        Assert.assertNotNull(rs);
        for (String[] row : rs) {
            for (String td : row) {
                System.out.print(td);
                System.out.print(' ');
            }
            System.out.println();
        }
        service.logout();
    }

    @Test
    @Order(14)
    void safelyDelete() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        Assert.assertFalse(service.remove("user", service.getLoggedUser()[0]));
        service.logout();
    }

    @Ignore
    @Order(15)
    void rest() {
        String[] curriencesCodes = new String[]{"USD", "EUR"};
        boolean br = false;
        for (String[] product : PRODUCT) {
            for (String currency : curriencesCodes) {
                String result = service.toCur(product[2], currency);
                if (result == null) {
                    System.out.println("Сервис недоступен");
                    br = true;
                    break;
                } else
                    System.out.println(result);
            }
            if (br)
                break;
        }
    }

    @Ignore
    @Order(20)
    void clearAll() {
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        String[] sequence = {"entry", "transaction", "assortment", "product", "store"};
        for (String type : sequence) {
            ArrayList<String[]> arr = service.readAll(type);
            for (String[] row : arr) {
                service.remove(type, row[0]);
            }
        }
        ArrayList<String[]> arr = service.readAll("user");
        for (int i = 1; i < arr.size(); i++) {
            service.remove("user", arr.get(i)[0]);
        }
        service.update("user", DEFAULT_ADMIN);
    }

}
