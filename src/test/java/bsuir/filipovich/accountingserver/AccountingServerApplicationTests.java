package bsuir.filipovich.accountingserver;

import bsuir.filipovich.accountingserver.service.IService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class AccountingServerApplicationTests {
    @Autowired
    private IService service;

    private final String DEFAULT_ADMIN_LOGIN = "admin";
    private final String DEFAULT_ADMIN_PASSWORD = "password";
    private final String ADMIN_LOGIN = "risk03";
    private final String ADMIN_PASSWORD = "wasd";
    private final String ADMIN_SURNAME = "Филиппович";
    private final String ADMIN_FORENAME = "Виктор";
    private final String ADMIN_PATRONYMIC = "Викторович";
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
    private int NUM_OF_TRANSACTIONS = 9;

    @Ignore
    @Order(1)
    void loginTest() {
        System.out.println(service.getLoggedUser() != null);
        if (service.getLoggedUser() != null)
            Assert.fail();
        service.login(ADMIN_LOGIN, ADMIN_PASSWORD);
        if (!service.getLoggedUser()[5].equals(ADMIN_LOGIN)) {
            Assert.fail();
        }
        service.logout();
    }

    @Ignore
    @Order(2)
    void changeAdmin() {
        System.out.println(service.getLoggedUser() != null);
        Assert.assertTrue(service.login(ADMIN_LOGIN, ADMIN_PASSWORD));
        String adminId = service.getLoggedUser()[0];
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
        if (!this.service.login(ADMIN_LOGIN, ADMIN_PASSWORD)) {
            Assert.fail();
        }
        for (String[] store : STORE) {
            this.service.create("store", store);
        }
        ArrayList<String[]> stores = new ArrayList<>();
        for (int i = 0; i < this.STORE.length; ++i) {
            stores.add(this.service.readOne("store", String.valueOf(i + 1)));
        }
        for (int i = 0; i < this.STORE.length; ++i) {
            for (int j = 0; j < this.STORE[0].length; ++j) {
                Assert.assertEquals(this.STORE[i][j], ((String[]) stores.get(i))[j]);
            }
        }
        service.logout();
    }

    @Ignore
    @Order(4)
    void createProduct() {
        if (!service.login(ADMIN_LOGIN, ADMIN_PASSWORD))
            Assert.fail();
        for (String[] product : PRODUCT) {
            service.create("product", product);
        }
        ArrayList<String[]> product = new ArrayList<>();
        for (int i = 0; i < PRODUCT.length; ++i) {
            product.add(service.readOne("product", String.valueOf(i + 1)));
        }
        for (int i = 0; i < PRODUCT.length; ++i) {
            for (int j = 0; j < PRODUCT[0].length; ++j) {
                Assert.assertEquals(PRODUCT[i][j], ((String[]) product.get(i))[j]);
            }
        }
        service.logout();
    }

    @Ignore
    @Order(5)
    void fillStore() {
        if (!service.login(ADMIN_LOGIN, ADMIN_PASSWORD)) {
            Assert.fail();
        }
        for (int store = 0; store < STORE.length; ++store) {
            for (int product = 0; product < PRODUCT.length; ++product) {
                service.setManyToMany("assortment", String.valueOf(store + 1), String.valueOf(product + 1), String.valueOf(2 * store + product));
            }
        }
        service.logout();
    }

    @Test
    @Order(6)
    void createCashier() {
        if (!this.service.login(ADMIN_LOGIN, ADMIN_PASSWORD)) {
            Assert.fail();
        }
        this.service.create("user", CASHIER);
        String[] user = service.readOne("user", CASHIER[0]);
        for (int i = 0; i < 6; ++i) {
            Assert.assertEquals(CASHIER[i], user[i]);
        }

    }

    @Ignore
    @Order(7)
    void logInLogOut() {
        this.service.getLoggedUser();
        Assert.fail();

        Assert.assertTrue(this.service.login("risk03", "wasd"));
        Assert.assertTrue(this.service.login(this.CASHIER[5], this.CASHIER[6]));
        Assert.fail();

        this.service.logout();

        Assert.assertTrue(this.service.login("Oules", "wasd"));

    }

    @Ignore
    @Order(8)
    void someTransactions() {
        if (!this.service.login(this.CASHIER[5], this.CASHIER[6])) {
            Assert.fail();
        }

        int j = 1;

        int i;
        for (i = 0; i < NUM_OF_TRANSACTIONS; ++i) {
            if (j > this.STORE.length) {
                j = 1;
            }

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.service.create("transaction", new String[]{String.valueOf(i + 1), String.valueOf(j), this.service.getLoggedUser()[0], dateFormat.format(date)});
            ++j;
        }

        for (i = 0; i < this.STORE.length; ++i) {
            for (int k = 0; k < this.PRODUCT.length; ++k) {
                this.service.setManyToMany("entry", String.valueOf(i + 1), String.valueOf(j + 1), String.valueOf(i * j / 2));
            }
        }

    }

    @Ignore
    @Order(9)
    void showAll() {
        if (!this.service.login("risk03", "wasd")) {
            Assert.fail();
        }

        String[] tables = new String[]{"user", "store", "product", "transaction", "assortment", "entry"};
        String[] var2 = tables;
        int var3 = tables.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String what = var2[var4];
            System.out.println(what + ':');
            Iterator var6 = this.service.readAll(what).iterator();

            while (var6.hasNext()) {
                String[] tr = (String[]) var6.next();
                String[] var8 = tr;
                int var9 = tr.length;

                for (int var10 = 0; var10 < var9; ++var10) {
                    String td = var8[var10];
                    System.out.print(td + ' ');
                }

                System.out.println();
            }
        }

    }

    @Ignore
    @Order(20)
    void clearAll() {
    }

}
