/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author ZhangHouYing
 * @date 2019-08-24 15:44
 */
@Configuration
//注掉的内容仅供参考
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
public class WebSocketConfig{

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

//	@Override
//	public void configureMessageBroker(MessageBrokerRegistry registry) {
//		registry.enableSimpleBroker("/topic");//注册一个队列，主要用来做消息区分的(在我看来)
//	}
//
//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint("/api").setAllowedOrigins("*").addInterceptors().withSockJS();//通俗易懂简单的来讲，addEndpoint("/api")就是客户端连接的时候url地址，后边的就不解释了。。。
//	}
}
