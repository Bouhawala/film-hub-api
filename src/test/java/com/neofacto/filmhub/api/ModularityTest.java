package com.neofacto.filmhub.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules modules = ApplicationModules.of(FilmHubApiApplication.class);
        modules.verify();
    }
}
