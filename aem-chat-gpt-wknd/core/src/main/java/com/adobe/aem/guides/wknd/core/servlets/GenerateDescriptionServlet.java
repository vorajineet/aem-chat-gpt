package com.adobe.aem.guides.wknd.core.servlets;

import javax.servlet.Servlet;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.framework.Constants;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.adobe.aem.guides.wknd.core.beans.ChatGPTRequest;
import com.adobe.aem.guides.wknd.core.beans.ChatGptResponse;
import com.adobe.aem.guides.wknd.core.services.ChatGPTService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Component(immediate = true, service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=ChatGPT Integration", "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/generative-ai/description", "sling.servlet.extensions={\"json\"}" })

public class GenerateDescriptionServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(GenerateDescriptionServlet.class.getName());

	@Reference
    private ChatGPTService chatGPTService;

	private static final HttpClient client = HttpClients.createDefault();
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		String text = "generate meta description for " + Externalizer.PUBLISH + request.getRequestURI();
		//String text = "generate meta description for https://wknd.site/us/en.html";
		String message = generateMessage(text);
		response.getWriter().write(message);
	}

	private String generateMessage(final String text) throws IOException {

		String requestBody = MAPPER.writeValueAsString(
				new ChatGPTRequest(text, chatGPTService.getChatGPTModel(), chatGPTService.getChatGPTRole()));
		HttpPost request = new HttpPost(chatGPTService.getChatGPTHostname() + chatGPTService.getChatGPTApiEndpoint());
		request.addHeader("Authorization", "Bearer " + chatGPTService.getChatGPTApiKey());
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(requestBody));
		HttpResponse response = client.execute(request);
		ChatGptResponse chatGptResponse = MAPPER.readValue(EntityUtils.toString(response.getEntity()),
				ChatGptResponse.class);
		String message = chatGptResponse.getChoices().get(0).getMessage().getContent();

		return message;
	}

}