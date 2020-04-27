package bsuir.filipovich.accountingserver.service;

import java.util.ArrayList;

public interface IService {
    boolean create(String type, String[] strings);

    String[] readOne(String type, int id);

    ArrayList<String[]> readAll(String type);

    ArrayList<String[]> readAll(String type, int id);

    boolean update(String type, String[] strings);

    boolean remove(String type, String id);

    void setAssortment(String type, int root, int product, Double quantity);

    boolean login(String login, String password);

    void logout();

    String[] getLoggedUser();

    String[] showWhere(String id);

    ArrayList<String[]> getSalesByStoreReport(String from, String to);

    ArrayList<String[]> getSalesByProductReport(String from, String to);

    ArrayList<String[]> getSalesByCashierReport(String from, String to);

    String toCur(String product, String currency);
}
