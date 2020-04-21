package bsuir.filipovich.accountingserver;

import bsuir.filipovich.accountingserver.service.IService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountingServerApplicationTests {

    @Autowired
    private IService service;

    @Test
    void contextLoads() {
        System.out.println(service.getMessage());
        service.login("login", "password");
        service.logout();
        System.out.println(service.useBean());
    }

}
