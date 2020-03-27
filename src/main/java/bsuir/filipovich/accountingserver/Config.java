package bsuir.filipovich.accountingserver;

import bsuir.filipovich.accountingserver.service.IService;
import bsuir.filipovich.accountingserver.service.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteExporter;

@Configuration
public class Config {
    @Bean
    RemoteExporter registerRMIExporter() {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setServiceName("accountingservice");
        exporter.setServiceInterface(IService.class);
        exporter.setService(new Service());
        return exporter;
    }
}
