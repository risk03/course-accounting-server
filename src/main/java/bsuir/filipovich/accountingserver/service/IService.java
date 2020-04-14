package bsuir.filipovich.accountingserver.service;

import java.util.ArrayList;

public interface IService {
    String getMessage();

    void create(String type, String[] strings);

    ArrayList<String[]> readAll(String type);

    void update(String type, String[] strings);

    void remove(String type, String id);

}