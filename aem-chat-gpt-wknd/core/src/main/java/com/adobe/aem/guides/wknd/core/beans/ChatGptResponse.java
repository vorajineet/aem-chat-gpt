package com.adobe.aem.guides.wknd.core.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true )
public class ChatGptResponse {
    private List<Choice> choices;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true )
    public static class Choice {
        private Message message;

		public Message getMessage() {
			return message;
		}            

    }

	public List<Choice> getChoices() {
		return choices;
	}
}

