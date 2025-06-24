package com.example.activiti_demo;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @date: 2025/6/22 19:45
 * @author: chenjian
 */
@Slf4j
@SpringBootTest
public class ActivitiDemoTest {

    /**
     * 部署流程定义
     * ACT_GE_PROPERTY 流程定义信息
     * ACT_RE_PROCDEF 流程定义信息
     * ACT_RE_DEPLOYMENT 流程部署信息
     * ACT_GE_BYTEARRAY 流程定义的二进制数据
     */
    @Test
    public void deployByFile() {
        // 获取流程引擎示例
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("processes_bak/leave.bpmn20.xml")
                .addClasspathResource("processes_bak/leave.png")
                .name("请假流程")
                .deploy();

        System.out.println("流程部署ID：" + deploy.getId());
        System.out.println("流程部署名称：" + deploy.getName());
    }

    /**
     * 获取流程定义信息
     */
    @Test
    public void getProcessDefinitions() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myLeave")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        for (ProcessDefinition definition : list) {
            System.out.println("流程定义ID：" + definition.getId());
            System.out.println("流程定义名称：" + definition.getName());
            System.out.println("流程定义Key：" + definition.getKey());
            System.out.println("流程定义版本：" + definition.getVersion());
            System.out.println("流程定义描述：" + definition.getDescription());
            System.out.println("流程定义部署ID：" + definition.getDeploymentId());
        }
    }

    /**
     * 启动流程实例
     * ACT_HI_TASKINST 存放任务节点信息
     * ACT_HI_PROCINST 存放流程实例信息
     * ACT_HI_ACTINST 存放流程节点信息
     * ACT_HI_IDENTITYLINK 存放流程节点信息
     * ACT_RU_EXECUTION 存放流程实例信息
     * ACT_RU_TASK 存放任务节点信息
     * ACT_RU_IDENTITYLINK 存放流程节点信息
     */
    @Test
    public void startProcess() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myLeave");
        System.out.println("流程定义ID：" + instance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + instance.getId());
    }

    /**
     * 查询当前待办任务
     */
    @Test
    public void queryTaskByAssignList() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myLeave")
                .taskAssignee("lingdao")
                .list();
        for (Task task : list) {
            System.out.println("任务ID:" + task.getId());
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务创建时间:" + task.getCreateTime());
            System.out.println("任务办理人:" + task.getAssignee());
            System.out.println("流程实例ID:" + task.getProcessInstanceId());
            System.out.println("执行对象ID:" + task.getExecutionId());
        }
    }

    /**
     * 查询当前待办任务
     */
    @Test
    public void completeTaskByAssignList() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myLeave")
                .taskAssignee("lingdao")
                .list();
        if (CollectionUtils.isEmpty(list)) {
            log.info("没有待处理的任务");
        }
        for (Task task : list) {
            taskService.complete(task.getId());
            log.info("任务完成：{}", task.getName());
        }
    }

    @Test
    public void historyTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                //.processDefinitionId("15001")
                .processInstanceId("15001")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        for (HistoricActivityInstance historicActivityInstance : list) {
            System.out.println("任务名称 = " + historicActivityInstance.getActivityName());
            System.out.println("任务开始时间 = " + historicActivityInstance.getStartTime());
            System.out.println("任务结束时间 = " + historicActivityInstance.getEndTime());
            System.out.println("任务耗时 = " + historicActivityInstance.getDurationInMillis());
            System.out.println("任务ID = " + historicActivityInstance.getId());
            System.out.println("任务ID = " + historicActivityInstance.getTaskId());
            System.out.println("任务节点ID = " + historicActivityInstance.getActivityId());
            System.out.println("任务类型 = " + historicActivityInstance.getActivityType());
            System.out.println("任务办理人 = " + historicActivityInstance.getAssignee());
            System.out.println("流程实例ID = " + historicActivityInstance.getProcessInstanceId());
            System.out.println("流程定义ID = " + historicActivityInstance.getProcessDefinitionId());
            System.out.println("------------------------------------------------");
        }
    }
}
