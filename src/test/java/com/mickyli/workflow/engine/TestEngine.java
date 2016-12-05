package com.mickyli.workflow.engine;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class TestEngine {
	@Test  
	public void testCreateTableWithXml(){  
	    // 引擎配置  
	    ProcessEngineConfiguration pec = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");  
	    // 获取流程引擎对象  
	    ProcessEngine processEngine = pec.buildProcessEngine();
	    System.out.println(processEngine);
	} 
}
