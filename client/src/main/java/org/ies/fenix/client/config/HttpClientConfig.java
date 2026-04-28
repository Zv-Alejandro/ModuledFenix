package org.ies.fenix.client.config;

import org.ies.fenix.controller.IClientController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "backend", types = {IClientController.class})
public class HttpClientConfig {
}