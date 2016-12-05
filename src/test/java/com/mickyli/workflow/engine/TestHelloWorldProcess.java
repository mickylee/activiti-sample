package com.mickyli.workflow.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestHelloWorldProcess {
	/** 
	 * 获取默认的流程引擎实例 会自动读取activiti.cfg.xml文件  
	 */  
	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
    /** 
     * 部署流程定义 
     */  
    @Test  
    public void deploy(){  
        // 获取部署对象  
        Deployment deployment = processEngine.getRepositoryService() // 部署Service  
                     .createDeployment()  // 创建部署  
                     .addClasspathResource("diagrams/helloworld.bpmn")  // 从classpath的资源中加载，一次只能加载一个文件  
                     .addClasspathResource("diagrams/helloworld.png")   // 从classpath的资源中加载，一次只能加载一个文件  
                     .name("HelloWorld流程")  // 流程名称  
                     .deploy(); // 完成部署  
        System.out.println("流程部署ID:"+deployment.getId());  
        System.out.println("流程部署Name:"+deployment.getName());  
    }  
    
    /** 
     * 部署流程定义使用zip方式 
     */  
    @Test  
    public void deployWithZip(){  
        InputStream inputStream = this.getClass()  // 获取当前class对象  
                            .getClassLoader()   // 获取类加载器  
                            .getResourceAsStream("diagrams/helloworld.zip"); // 获取指定文件资源流  
        ZipInputStream zipInputStream = new ZipInputStream(inputStream); // 实例化zip输入流对象  
        // 获取部署对象  
        Deployment deployment = processEngine.getRepositoryService() // 部署Service  
                     .createDeployment()  // 创建部署  
                     .name("HelloWorld流程2")  // 流程名称  
                     .addZipInputStream(zipInputStream)  // 添加zip是输入流  
                     .deploy(); // 部署  
        System.out.println("流程部署ID:"+deployment.getId());  
        System.out.println("流程部署Name:"+deployment.getName());  
    }  
    
    /** 
     * 启动流程实例 
     */  
    @Test  
    public void start(){  
        // 启动并获取流程实例  
        ProcessInstance processInstance = processEngine.getRuntimeService() // 运行时流程实例Service  
            .startProcessInstanceByKey("myProcess"); // 数据库中流程定义表(act_re_prcdef)的KEY字段值；key对应对应 流程图里的process id的名字，使用Key值 启动，默认是按照最新版本的流程定义启动的    
        System.out.println("流程实例ID:-->"+processInstance.getId());  
        System.out.println("流程定义ID:-->"+processInstance.getProcessDefinitionId());  
    }  
    
    /**查询流程定义*/    
    @Test    
    public void findProcessDefinition(){    
        List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service    
                        .createProcessDefinitionQuery()//创建一个流程定义的查询    
                        /**指定查询条件,where条件*/    
//                      .deploymentId(deploymentId)//使用部署对象ID查询    
//                      .processDefinitionId(processDefinitionId)//使用流程定义ID查询    
//                      .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询    
//                      .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询    
                            
                        /**排序*/    
                        .orderByProcessDefinitionVersion().asc()//按照版本的升序排列    
//                      .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列    
                            
                        /**返回的结果集*/    
                        .list();//返回一个集合列表，封装流程定义    
//                      .singleResult();//返回惟一结果集    
//                      .count();//返回结果集数量    
//                      .listPage(firstResult, maxResults);//分页查询    
        if(list!=null && list.size()>0){    
            for(ProcessDefinition pd:list){    
                System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数    
                System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值    
                System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值    
                System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1    
                System.out.println("资源名称bpmn文件:"+pd.getResourceName());    
                System.out.println("资源名称png文件:"+pd.getDiagramResourceName());    
                System.out.println("部署对象ID："+pd.getDeploymentId());    
                System.out.println("#########################################################");    
            }    
        }               
    }    
    /***附加功能：查询最新版本的流程定义*/    
    @Test    
    public void findLastVersionProcessDefinition(){    
        List<ProcessDefinition> list = processEngine.getRepositoryService()//    
                        .createProcessDefinitionQuery()//    
                        .orderByProcessDefinitionVersion().asc()//使用流程定义的版本升序排列（可能存在多个集合）    
                        .list();    
        /**  
         * Map<String,ProcessDefinition>  
         * map集合的key：流程定义的key  
         * map集合的value：流程定义的对象  
         * map集合的特点：当map集合key值相同的情况下，后一次的值将替换前一次的值  
         */    
        Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();    
        if(list!=null && list.size()>0){    
            for(ProcessDefinition pd:list){    
                map.put(pd.getKey(), pd);    
            }    
        }    
        List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());    
        if(pdList!=null && pdList.size()>0){    
            for(ProcessDefinition pd:pdList){    
                System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数    
                System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值    
                System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值    
                System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1    
                System.out.println("资源名称bpmn文件:"+pd.getResourceName());    
                System.out.println("资源名称png文件:"+pd.getDiagramResourceName());    
                System.out.println("部署对象ID："+pd.getDeploymentId());    
                System.out.println("#########################################################");    
            }    
        }       
    }
    
    /**删除流程定义*/  
    @Test  
    public void deleteProcessDefinition(){  
        //使用部署ID，完成删除  
        String deploymentId = "7501";  
        /** 
         * 不带级联的删除 
         *    只能删除没有启动的流程，如果流程启动，就会抛出异常 
         */  
//      processEngine.getRepositoryService()//  
//                      .deleteDeployment(deploymentId);  
          
        /** 
         * 级联删除 
         *    不管流程是否启动，都能可以删除 
         */  
        processEngine.getRepositoryService()//  
                        .deleteDeployment(deploymentId, true);  
        System.out.println("删除成功！");  
    } 
    
    /**附加功能：删除流程定义（删除key相同的所有不同版本的流程定义）*/  
    @Test  
    public void deleteProcessDefinitionByKey(){  
        //流程定义的key  
        String processDefinitionKey = "myProcess";  
        //先使用流程定义的key查询流程定义，查询出所有的版本  
        List<ProcessDefinition> list = processEngine.getRepositoryService()//  
                        .createProcessDefinitionQuery()//  
                        .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询  
                        .list();  
        System.out.println("查询到流程定义个数:" + list.size());
        //遍历，获取每个流程定义的部署ID  
        if(list!=null && list.size()>0){  
            for(ProcessDefinition pd:list){  
                //获取部署ID  
                String deploymentId = pd.getDeploymentId();  
                processEngine.getRepositoryService()//  
                            .deleteDeployment(deploymentId, true);  
            }  
        }  
    }  
    
    /** 
     * 通过流程部署ID获取流程图图片 
     */  
    @Test  
    public void getImageById()throws Exception{  
        InputStream inputStream=processEngine.getRepositoryService()  
            .getResourceAsStream("10001", "helloWorld.png"); // 根据流程部署ID和资源名称获取输入流  
        FileUtils.copyInputStreamToFile(inputStream, new File("D:/helloWorld.png"));  
    }  
      
    /**查看流程图 
     * @throws IOException */  
    @Test  
    public void viewPic() throws IOException{  
        /**将生成图片放到文件夹下*/  
        //部署Id  
        String deploymentId = "12501";  
        //获取图片资源名称  
        List<String> list = processEngine.getRepositoryService()
                        .getDeploymentResourceNames(deploymentId);  
        System.out.println(list);
        //定义图片资源的名称  
        String resourceName = "";  
        if(list!=null && list.size()>0){  
            for(String name:list){  
                if(name.indexOf(".png")>=0){  
                    resourceName = name;  
                }  
            }  
        }  
                  
        //获取图片的输入流  
        InputStream in = processEngine.getRepositoryService()//  
                        .getResourceAsStream(deploymentId, resourceName);  
          
        //将图片生成到D盘的目录下  
        File file = new File("D:/"+resourceName);  
        //将输入流的图片写到D盘下  
        FileUtils.copyInputStreamToFile(in, file);  
    }  
    
    /** 
     * 查看任务 
     */  
    @Test  
    public void findTask(){  
        // 查询并且返回任务即可  
        List<Task> taskList = processEngine.getTaskService() // 任务相关Service  
                .createTaskQuery()  // 创建任务查询  
                .taskAssignee("micky") // 指定某个人  
                .list();   
        for(Task task:taskList){  
            System.out.println("任务ID:"+task.getId());  
            System.out.println("任务名称："+task.getName());  
            System.out.println("任务创建时间："+task.getCreateTime());  
            System.out.println("任务委派人："+task.getAssignee());  
            System.out.println("流程实例ID:"+task.getProcessInstanceId());  
        }  
    }  
    
    /**查询流程状态（判断流程正在执行，还是结束）*/  
    @Test  
    public void isProcessEnd(){  
        String processInstanceId = "15001";  
        ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象  
                        .createProcessInstanceQuery()//创建流程实例查询  
                        .processInstanceId(processInstanceId)//使用流程实例ID查询  
                        .singleResult();  
        if(pi==null){  
            System.out.println("流程已经结束");  
        }  
        else{  
            System.out.println("流程没有结束");  
        }  
    } 
    
    /** 
     * 完成任务 
     */  
    @Test  
    public void completeTask(){  
    	String taskId = "15004";
        processEngine.getTaskService() // 任务相关Service  
                .complete(taskId); // 指定要完成的任务ID  
        System.out.println("id:" + taskId + "任务已经完成");
    }  
    
    /**查询历史任务*/  
    @Test  
    public void findHistoryTask(){  
        String taskAssignee = "micky";  
        List<HistoricTaskInstance> list = processEngine.getHistoryService()//与历史数据（历史表）相关的Service  
                        .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
                        .taskAssignee(taskAssignee)//指定历史任务的办理人  
                        .list();  
        if(list!=null && list.size()>0){  
            for(HistoricTaskInstance hti:list){  
                System.out.println(hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());  
                System.out.println("################################");  
            }  
        }  
    }
    
    /**查询历史流程实例*/  
    @Test  
    public void findHistoryProcessInstance(){  
        String processInstanceId = "15001";  
        HistoricProcessInstance hpi = processEngine.getHistoryService()//与历史数据（历史表）相关的Service  
                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询  
                        .processInstanceId(processInstanceId)//使用流程实例ID查询  
                        .singleResult();  
        System.out.println(hpi.getId()+"    "+hpi.getProcessDefinitionId()+"    "+hpi.getStartTime()+"    "+hpi.getEndTime()+"     "+hpi.getDurationInMillis());  
    }  
}
