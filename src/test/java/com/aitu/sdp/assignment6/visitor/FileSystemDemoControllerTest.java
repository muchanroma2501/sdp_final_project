package com.aitu.sdp.assignment6.visitor;

import com.aitu.sdp.assignment2.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
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
}

