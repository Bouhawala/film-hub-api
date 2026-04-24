package com.neofacto.filmhub.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableWireMock(@ConfigureWireMock(name = "filmhub", baseUrlProperties = "filmhub.base-url", port = 8089))
public abstract class WireMockTestBase {

    @InjectWireMock("filmhub")
    protected WireMockServer wireMock;

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }
}