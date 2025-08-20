package org.Simulator.AI;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;

// https://nolialsea.github.io/Wpp/

public class chatbot {
    private OpenAIClient client;
    String Conversation;

    public chatbot(String AI_KEY, String Scenario){

        this.client = OpenAIOkHttpClient.builder().fromEnv().apiKey(AI_KEY).build();
        this.Conversation = Scenario;

    }

    public String chat(String Message) {

        final String[] Response = {""};

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input("this is the memory of your last conversation : " + Conversation + "NOW, respond to this message : " +  Message)
                .model(ChatModel.GPT_4_1)
                .build();

        client.responses().create(params).output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .forEach(outputText -> { Response[0] = Response[0] + outputText.text(); });

        Conversation = Conversation + Response[0] + "\n";

        return Response[0];
    };

    public boolean clear() {
        this.Conversation = "";
        return this.Conversation == "";
    };

}
