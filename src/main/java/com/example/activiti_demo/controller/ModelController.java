package com.example.activiti_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ningbeibei
 * @since 2024-09-26
 */
@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/create")
    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Model model = repositoryService.newModel();
        model.setName("请假流程");
        model.setKey("leave");
        model.setVersion(1);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("name", model.getName());
        node.put("revision", model.getVersion());
        node.put("description", "");
        model.setMetaInfo(node.toString());
        repositoryService.saveModel(model);
        response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=111");
    }

}
 