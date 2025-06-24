package com.example.activiti_demo;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class ActivitiDemoApplicationTests {

    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;

    // 创建 ProcessEngine 对象
//    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();




//    /**
//     * 初始化数据库表：第一种方式
//     */
//    @Test
//    public void testInitOne() {
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        System.err.println("processEngine = " + processEngine);
//    }

    @Test
    public void test11() {
        ProcessInstance myLeave = runtimeService.startProcessInstanceByKey("myLeave");
        System.out.println("流程定义的id = " + myLeave.getProcessDefinitionId());
        System.out.println("流程实例的id = " + myLeave.getId());
        System.out.println("当前活动ID =" + myLeave.getActivityId());
    }

    /**
     * 初始化数据库表
     * 通过读取 activiti.cfg.xml 配置文件
     */
    @Test
    public void testInitTwo() {
        System.out.println(repositoryService);
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine processEngine = configuration.buildProcessEngine();
        System.err.println("processEngine = " + processEngine.toString());
    }

    /**
     * 流程部署
     */
    @Test
    public void testDeploy() {
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService 对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes_bak/leave.bpmn20.xml")
                .addClasspathResource("processes_bak/leave.png")
                .name("请假流程")
                .deploy();
        // 输出部署的一些信息
        System.out.println("流程部署ID：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
        System.out.println("流程部署成功");
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess() {
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 runtimeService 对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 1.根据流程定义的key启动流程实例，这个key是在定义bpmn的时候设置的
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myLeave");
        // 2.根据流程定义id启动流程实例
//        ProcessInstance instance = runtimeService.startProcessInstanceById("leave:1:4");

        // 获取流程实例的相关信息
        System.out.println("流程定义的id = " + instance.getProcessDefinitionId());
        System.out.println("流程实例的id = " + instance.getId());
        System.out.println("启动流程成功 ");
    }



    /**
     * 查询待办任务
     */
    @Test
    public void testSelectTodoTaskList() {
        String assignee = "works";
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取指定用户审核任务
        TaskService taskService = processEngine.getTaskService();
        // 使用面对对象方式查询数据库
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey("myLeave")
                .taskAssignee(assignee)
                // 返回多个结果
                .list();
        // 只返回一个结果
        // .singleResult();

        // 自定义 sql 查询
        // taskService.createNativeTaskQuery();

        // 获取流程实例的相关信息
        for (Task task : tasks) {
            System.out.println("流程定义的id = " + task.getProcessDefinitionId());
            System.out.println("流程实例的id = " + task.getProcessInstanceId());
            System.out.println("任务id = " + task.getId());
            System.out.println("任务名称 = " + task.getName());
        }
    }


    /**
     * 指定用户去完成任务待办：多人审批在这操作，改变审核人名称就行了
     */
    @Test
    public void testCompleteTask() {
        String assignee = "works";
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取指定用户审核任务
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey("myLeave")
                .taskAssignee(assignee)
                .list();
        if (tasks != null && !tasks.isEmpty()){
            // 当前流程图所限制，只能做审核同意的动作
            for (Task task : tasks) {
                taskService.complete(task.getId());
            }
        }
    }


    /**
     * 审批添加备注
     */
    @Test
    public void testAddComment() {
        String assignee = "张三";
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取指定用户审核任务
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey("leave")
                .taskAssignee(assignee)
                .list();
        if (tasks != null && !tasks.isEmpty()) {
            // 当前流程图所限制，只能做审核同意的动作
            for (Task task : tasks) {
                // 添加备注
                taskService.addComment(task.getId(), task.getProcessInstanceId(), assignee + "表示同意");
                taskService.complete(task.getId());
            }
        }
    }


    /**
     * 查询审批历史
     */
    @Test
    public void testSelectHistoryTask() {
        String processInstanceId = "2501";
        String assignee = "张三";
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 查看历史信息
        HistoryService historyService = processEngine.getHistoryService();
        // 获取指定用户审核任务
        TaskService taskService = processEngine.getTaskService();
        // 获取历史审核信息
        List<HistoricActivityInstance> userTask = historyService.createHistoricActivityInstanceQuery()
                .activityType("userTask")
                // 指定实例的id
                .processInstanceId(processInstanceId)
                .taskAssignee(assignee)
                .finished()
                .list();

        for (HistoricActivityInstance instance : userTask) {
            System.out.println("任务名称 = " + instance.getActivityName());
            System.out.println("任务开始时间 = " + instance.getStartTime());
            System.out.println("任务结束时间 = " + instance.getEndTime());
            System.out.println("任务耗时 = " + instance.getDurationInMillis());
            // 获取审批批注信息
            List<Comment> taskComments = taskService.getTaskComments(instance.getTaskId());
            if (!taskComments.isEmpty()){
                System.out.println("审批批注 = " + taskComments.get(0).getFullMessage());
            }
        }
    }


    /**
     * 查询流程相关信息
     */
    @Test
    public void testDefinitionQuery() {
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义集合
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave")
                // 最新的一个版本
                .latestVersion()
                .list();
        // 遍历集合
        for (ProcessDefinition definition : processDefinitionList) {
            System.out.println("流程定义id = " + definition.getId());
            System.out.println("流程定义名称 = " + definition.getName());
            System.out.println("流程定义key = " + definition.getKey());
            System.out.println("流程定义版本 = " + definition.getVersion());
            System.out.println("流程部署id = " + definition.getDeploymentId());
            System.out.println("===============");
        }
    }


    /**
     * 资源文件下载
     */
    @Test
    public void testDownloadResource() throws IOException {
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义集合
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave")
                // 按照版本降序
                .orderByProcessDefinitionVersion()
                // 降序
                .desc()
                .list();

        // 获取最新那个
        ProcessDefinition definition = list.get(0);
        // 获取部署id
        String deploymentId = definition.getDeploymentId();

        // 获取bpmn的输入流
        InputStream bpmnInputStream = repositoryService.getResourceAsStream(deploymentId, definition.getResourceName());
        // 获取png的输入流
//        InputStream pngInputStream = repositoryService.getResourceAsStream(deploymentId, definition.getDiagramResourceName());

        String resourcePath = "C:/Users/ASUS/Desktop/" + File.separator + definition.getResourceName();
        File file = new File(resourcePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        String diagramResourcePath = "C:/Users/ASUS/Desktop/" + File.separator + definition.getDiagramResourceName();
        file = new File(diagramResourcePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        //复制文件
        FileCopyUtils.copy(bpmnInputStream, Files.newOutputStream(Paths.get(resourcePath)));
//        FileCopyUtils.copy(pngInputStream, Files.newOutputStream(Paths.get(diagramResourcePath)));
    }


    /**
     * 删除已经部署的流程定义
     */
    @Test
    public void testDeleteDeploy() {
        // 删除已经部署的流程定义
        String deploymentId = "45001";
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 删除流程定义，如果改流程定义已有流程实例启动，则会报错
//        repositoryService.deleteDeployment(deploymentId);
        // 设置 true，级联删除流程定义，即使有启动的实例也可以删除
        repositoryService.deleteDeployment(deploymentId, true);
    }


    /**
     * 启动流程，需要进行 BusinessKey 绑定流程实例
     */
    @Test
    public void testStartBindBusinessKey() {
        String businessKey = "1";
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 根据流程定义的key启动流程实例，这个key是在定义bpmn的时候设置的
        // 在启动流程的时候将业务key加进去
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("leave", businessKey);
        // 获取流程实例的相关信息
        System.out.println("流程定义id = " + instance.getProcessDefinitionId());
        System.out.println("流程实例id = " + instance.getId());
        System.out.println("业务标识 = " + instance.getBusinessKey());
    }


    /**
     * 跑到下一个节点，需要进行审批了，此时需要获取 BusinessKey 进而获取请假单信息
     */
    @Test
    public void testGetBusinessKey() {
        // 1、获取李四的待办信息
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> task = taskService.createTaskQuery()
                .taskAssignee("李四")
                .processDefinitionKey("leave")
                .list();

        // 2、获取 businessKey
        // 获取流程实例id
        String processInstanceId = task.get(1).getProcessInstanceId();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String businessKey = processInstance.getBusinessKey();

        System.out.println("业务标识 = " + businessKey);
    }


    /**
     * 流程定义挂起与激活
     */
    @Test
    public void testSuspendAllProcessInstance() {
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave")
                .singleResult();
        boolean suspended = processDefinition.isSuspended();
        // 输出流程定义状态
        System.out.println("流程定义状态：" + (suspended ? "已挂起" : "已激活"));
        String processDefinitionId = processDefinition.getId();

        if (suspended) {
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程id：" + processDefinitionId + "已激活");
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程id：" + processDefinitionId + "已挂起");
        }
    }


    /**
     * 流程实例挂起与激活
     */
    @Test
    public void testProcessInstance() {
        // 创建 ProcessEngine 对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 获取流程定义对象
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("leave")
                .list();

        // 遍历集合
        for (ProcessInstance processInstance  : processInstanceList) {
            boolean suspended = processInstance.isSuspended();
            // 输出流程定义状态
            System.out.println("流程实例状态：" + processInstance + "," + (suspended ? "已挂起" : "已激活"));
            String processDefinitionId = processInstance.getId();

            if (suspended) {
                runtimeService.activateProcessInstanceById(processDefinitionId);
                System.out.println("流程实例id：" + processDefinitionId + "已激活");
            } else {
                runtimeService.suspendProcessInstanceById(processDefinitionId);
                System.out.println("流程实例id：" + processDefinitionId + "已挂起");
            }
        }
    }
}