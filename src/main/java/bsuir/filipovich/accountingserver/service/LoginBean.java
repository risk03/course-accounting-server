package bsuir.filipovich.accountingserver.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LoginBean {
    String foo() {
        return "spam";
    }
}
