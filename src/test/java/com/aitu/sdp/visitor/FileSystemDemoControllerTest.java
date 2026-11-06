package com.aitu.sdp.visitor;

import com.aitu.sdp.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class FileSystemDemoControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void returnsVisitorDemoResults() throws Exception {
        mvc.perform(get("/api/visitor/demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rootName").value("root"))
                .andExpect(jsonPath("$.totalSizeBytes").value(1160))
                .andExpect(jsonPath("$.matches[0]").value("app.log"))
                .andExpect(jsonPath("$.misses").isEmpty());
    }

    @Test
    void buildsAndInspectsPcBundleThroughTheIntegratedDomainFlow() throws Exception {
        mvc.perform(get("/api/visitor/pc-bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rootName").value("pc-build-bundle"))
                .andExpect(jsonPath("$.cpu").isNotEmpty())
                .andExpect(jsonPath("$.gpu").isNotEmpty())
                .andExpect(jsonPath("$.totalSizeBytes").isNumber())
                .andExpect(jsonPath("$.searchQuery").value("log"))
                .andExpect(jsonPath("$.matches", hasItem("thermal-stress.log")))
                .andExpect(jsonPath("$.matches", hasItem("power-rail-diagnostics.log")));

        mvc.perform(get("/api/visitor/pc-bundle")
                        .param("ecosystem", "amd")
                        .param("buildType", "budget")
                        .param("query", "pkg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpu").value("AMD Ryzen 5 7600"))
                .andExpect(jsonPath("$.searchQuery").value("pkg"))
                .andExpect(jsonPath("$.matches", hasItem("gpu-shader-cache-Radeon-Vega-Integrated-Graphics.pkg")));

        mvc.perform(get("/api/visitor/pc-bundle")
                        .param("ecosystem", "intel_nvidia")
                        .param("buildType", "workstation")
                        .param("query", "html"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchQuery").value("html"))
                .andExpect(jsonPath("$.matches", hasItem("diagnostic-report.html")));
    }
}
