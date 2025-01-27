package com.example.demoSBus.Controllers;



import com.example.demoSBus.Models.DataModel;
import com.example.demoSBus.Services.FileReader;
import com.example.demoSBus.Services.ServiceBusProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApplicationController {
    @Autowired
    private FileReader fileReaderService;
    @Autowired
    private ServiceBusProducer serviceBusProducer;

    @Value("${app.file-path}")
    private String defaultFilePath;

    public ApplicationController(FileReader fileReaderService, ServiceBusProducer serviceBusProducer) {
        this.fileReaderService = fileReaderService;
        this.serviceBusProducer = serviceBusProducer;
    }

    @GetMapping("/push-to-queue")
    public String pushDataToQueue(@RequestParam(required = false) String filePath) {
        try {
            String pathToRead = filePath != null ? filePath : defaultFilePath;
            List<DataModel> dataModels = fileReaderService.readJsonFile(pathToRead);

            for (DataModel data : dataModels) {
                serviceBusProducer.sendMessage(data.toString());
            }

            return "Data successfully pushed to Azure Service Bus queue.";
        } catch (Exception e) {
            return "Error occurred: " + e.getMessage();
        }
    }
}



