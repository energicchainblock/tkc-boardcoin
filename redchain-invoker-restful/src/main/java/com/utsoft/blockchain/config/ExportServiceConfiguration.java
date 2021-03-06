package com.utsoft.blockchain.config;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.IGlobals;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
/**
 * @author hunterfox
 * @date: 2017年7月28日
 * @version 1.0.0
 */
@Configuration
public class ExportServiceConfiguration  {

	 @Bean
	 public AnnotationBean motanAnnotationBean() {
	     AnnotationBean motanAnnotationBean = new AnnotationBean();
	     motanAnnotationBean.setPackage("com.utsoft.blockchain.core.rpc.provider");
	     return motanAnnotationBean;
	 }
	 
	 @Bean(name = "tkcExportServer")
	 public ProtocolConfigBean protocolConfig() {
	     ProtocolConfigBean config = new ProtocolConfigBean();
	     config.setDefault(true);
	     config.setName("motan");
	     config.setRequestTimeout(60*1000);
	     config.setMaxContentLength(1048576);
	     return config;
	 }

	 @Bean(name = "registryConfig")
	 public RegistryConfigBean registryConfig() {
	     
		 RegistryConfigBean config = new RegistryConfigBean();
	     String  zookeeper_address = IGlobals.getProperty("zookeeper.address");
	     config.setRegistrySessionTimeout(60*1000);
	     config.setConnectTimeout(30*1000);
	     config.setRegProtocol("zookeeper");
	     config.setName("rpc_zookeeper");
	     config.setAddress(zookeeper_address);
	     return config;
	 }

	 @Bean
	 public BasicServiceConfigBean baseServiceConfig() {
	   
		 String  group = IGlobals.getProperty("motan.config.group");
		 String  application = IGlobals.getProperty("motan.config.application");
		 
		 BasicServiceConfigBean config = new BasicServiceConfigBean();
	     config.setExport("tkcExportServer:8002");
	     config.setRegistry("registryConfig");
	     config.setGroup(group);
	    
	     /*String domain = IGlobals.getProperty("fabric.getway.domain");
	     if (CommonUtil.isNotEmpty(domain))
	     config.setHost("chain.tangkc.com");*/
	     
	     boolean  isPublicMather = IGlobals.getBooleanProperty("motan.public.match",false);
	     if (isPublicMather) {
	    	 String localHost = CommonUtil.getPublicIPAddress();
	    	 config.setHost(localHost); 
	     } else {
	    	String localHost = CommonUtil.getInnerIPAddress();
	    	config.setHost(localHost);
	     }
	     config.setAccessLog(false);
	     config.setShareChannel(true);
	     config.setModule("motan--rpc");
	     config.setApplication(application);
	     return config;
	 }
	 
	 @Bean
	 public CuratorFramework curatorFramework(){
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 30);
		String  zookeeper_address = IGlobals.getProperty("zookeeper.address");
		CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeper_address, retryPolicy);
		client.start();
		return client;
	}
}
