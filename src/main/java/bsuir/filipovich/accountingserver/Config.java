package bsuir.filipovich.accountingserver;

import bsuir.filipovich.accountingserver.service.IService;
import bsuir.filipovich.accountingserver.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteExporter;

@Configuration
public class Config {
    @Autowired
    Service service;

    @Bean
    RemoteExporter registerRMIExporter() {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setServiceName("accountingservice");
        exporter.setServiceInterface(IService.class);
        exporter.setService(service);
        return exporter;
    }
}
