package bsuir.filipovich.accountingserver.service;

import java.util.ArrayList;

public interface IService {
    ArrayList<String[]> getUserList();

    ArrayList<String[]> getStoresList();

    String getMessage();

    void create(String type, String[] strings);

    void update(String type, String[] strings);

    void remove(String type, String id);

}