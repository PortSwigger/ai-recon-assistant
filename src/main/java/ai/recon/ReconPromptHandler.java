
package ai.recon;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ai.chat.Message;
import burp.api.montoya.ai.chat.PromptResponse;

import static burp.api.montoya.ai.chat.Message.systemMessage;
import static burp.api.montoya.ai.chat.Message.userMessage;

public class ReconPromptHandler {
    private final MontoyaApi api;

    public ReconPromptHandler(MontoyaApi api) {
        this.api = api;
    }

    public PromptResponse ask(String userPrompt) {
        var system = systemMessage("You are an assistant that analyzes web application structure based on HTTP requests.");
        return api.ai().prompt().execute(system, userMessage(userPrompt));
    }
}
