package com.adobe.aem.guides.wknd.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.adobe.aem.guides.wknd.core.services.ChatGPTService;
import com.adobe.aem.guides.wknd.core.services.impl.ChatGPTServiceImpl.ChatGPTConfiguration;

@Component(
        service = ChatGPTService.class,
        immediate = true)
@Designate(ocd = ChatGPTConfiguration.class)
public class ChatGPTServiceImpl implements ChatGPTService {
	
	private String chatGPTHostname;
	private String chatGPTApiEndpoint;
	private String chatGPTApiKey;
	private String chatGPTRole;
	private String chatGPTModel;
	
	@Activate
	@Modified
	protected void Activate(final ChatGPTConfiguration chatGPTConfiguration) {
		chatGPTHostname = chatGPTConfiguration.chatGPTApiHostname();
		chatGPTApiEndpoint = chatGPTConfiguration.chatGPTApiEndpoint();
		chatGPTApiKey = chatGPTConfiguration.chatGPTApiKey();
		chatGPTRole = chatGPTConfiguration.chatGPTRole();
		chatGPTModel = chatGPTConfiguration.chatGPTModel();
	}
	
	@ObjectClassDefinition(name = "Chat GPT Configuration")
	public @interface ChatGPTConfiguration {

		@AttributeDefinition(name = "Chat GPT API Hostname", description = "API Hostname for Chat GPT")
		String chatGPTApiHostname() default "https://api.openai.com";
		
		@AttributeDefinition(name = "Chat GPT API Endpoint", description = "API Endpoint for Chat GPT")
		String chatGPTApiEndpoint() default "/v1/chat/completions";
		
		@AttributeDefinition(name = "Chat GPT API Key", description = "API Key for Chat GPT")
		String chatGPTApiKey() default "<Replace with your own key>";
		
		@AttributeDefinition(name = "Chat GPT Role", description = "The ‘role’ can take one of three values: ‘system’, ‘user’ or the ‘assistant’")
		String chatGPTRole() default "user";
		
		@AttributeDefinition(name = "Chat GPT Model", description = "The AI Model to be used")
		String chatGPTModel() default "gpt-3.5-turbo";

	}

	@Override
	public String getChatGPTApiEndpoint() {
		return chatGPTApiEndpoint;
	}

	@Override
	public String getChatGPTHostname() {
		return chatGPTHostname;
	}

	@Override
	public String getChatGPTApiKey() {
		return chatGPTApiKey;
	}
	
	@Override
	public String getChatGPTRole() {
		return chatGPTRole;
	}
	
	@Override
	public String getChatGPTModel() {
		return chatGPTModel;
	}

}
