package bsuir.filipovich.accountingserver;

import bsuir.filipovich.accountingserver.service.AccountingService;
import bsuir.filipovich.accountingserver.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteExporter;

@Configuration
public class Config {
    @Autowired
    AccountingService accountingService;

    @Bean
    RemoteExporter registerRMIExporter() {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setServiceName("accountingservice");
        exporter.setServiceInterface(IService.class);
        exporter.setService(accountingService);
        return exporter;
    }
}
