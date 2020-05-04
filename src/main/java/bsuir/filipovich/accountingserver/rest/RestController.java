package bsuir.filipovich.accountingserver.rest;

import bsuir.filipovich.accountingserver.service.IService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/accounting-server")
public class RestController {
    private final IService service;

    public RestController(IService service) {
        this.service = service;
    }

    @GetMapping("/test")
    String[] test(@RequestParam String[] list) {
        return list;
    }

    @GetMapping("/create")
    boolean create(@RequestParam String type, @RequestParam String[] strings) {
        return service.create(type, strings);
    }

    @GetMapping("/readOne")
    String[] readOne(@RequestParam String type, @RequestParam String id) {
        return service.readOne(type, id);
    }

    @GetMapping("/readAll")
    ArrayList<String[]> readAll(@RequestParam String type) {
        return service.readAll(type);
    }

    @GetMapping("/readAllById")
    ArrayList<String[]> readAll(@RequestParam String type, @RequestParam String id) {
        return service.readAll(type, id);
    }

    @GetMapping("/update")
    boolean update(@RequestParam String type, @RequestParam String[] strings) {
        return service.update(type, strings);
    }

    @GetMapping("/remove")
    boolean remove(@RequestParam String type, @RequestParam String id) {
        return service.remove(type, id);
    }

    @GetMapping("/setManyToMany")
    boolean setManyToMany(@RequestParam String type, @RequestParam String root, @RequestParam String product, @RequestParam String quantity) {
        return service.setManyToMany(type, root, product, quantity);
    }

    @GetMapping("/login")
    boolean login(@RequestParam String login, @RequestParam String password) {
        return service.login(login, password);
    }

    @GetMapping("/logout")
    void logout() {
        service.logout();
    }

    @GetMapping("/getLoggedUser")
    String[] getLoggedUser() {
        return service.getLoggedUser();
    }

    @GetMapping("/showWhere")
    String[] showWhere(@RequestParam String id) {
        return service.showWhere(id);
    }

    @GetMapping("/getSalesByStoreReport")
    ArrayList<String[]> getSalesByStoreReport(@RequestParam String from, @RequestParam String to) {
        return service.getSalesByStoreReport(from, to);
    }

    @GetMapping("/getSalesByProductReport")
    ArrayList<String[]> getSalesByProductReport(@RequestParam String from, @RequestParam String to) {
        return service.getSalesByProductReport(from, to);
    }

    @GetMapping("/getSalesByCashierReport")
    ArrayList<String[]> getSalesByCashierReport(@RequestParam String from, @RequestParam String to) {
        return service.getSalesByCashierReport(from, to);
    }

    @GetMapping("/toCur")
    String toCur(@RequestParam String value, @RequestParam String currency) {
        return service.toCur(value, currency);
    }
}
