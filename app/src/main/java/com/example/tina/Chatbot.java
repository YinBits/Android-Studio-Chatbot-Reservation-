package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class Chatbot extends Fragment {

    private EditText userInputEditText;
    private TextView chatTextView;
    private final OkHttpClient httpClient = new OkHttpClient();
    private static final String OPENAI_API_KEY = "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";

    private boolean isProcessingMessage = false; // Adicione esta variável de controle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        userInputEditText = view.findViewById(R.id.userInputEditText);
        chatTextView = view.findViewById(R.id.chatTextView);
        Button sendButton = view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return view;
    }



    private void sendMessage() {
        // Verifique se já estamos processando uma mensagem
        if (isProcessingMessage) {
            return;
        }

        // Defina a flag para indicar que estamos processando uma mensagem
        isProcessingMessage = true;

        String userMessage = userInputEditText.getText().toString();

        // Enviar a mensagem do usuário para o chatbot e obter a resposta da API do ChatGPT
        String botResponse = generateChatbotResponse(userMessage);

        // Atualizar a exibição do chat com a resposta do chatbot
        updateChatView(userMessage, botResponse);

        // Limpar a caixa de entrada de texto
        userInputEditText.setText("");

        // Restaure a flag após o processamento
        isProcessingMessage = false;
    }

    private String generateChatbotResponse(String userMessage) {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            // Configurar a conversa inicial
            String json = "{\"messages\": ["
                    + "{\"role\": \"system\", \"content\": \"You are a chatbot that speaks like Shakespeare.\"},"
                    + "{\"role\": \"user\", \"content\": \"Olá\"},"
                    + "{\"role\": \"assistant\", \"content\": \"Gostaria de fazer uma reserva?\"}"
                    + "]}";

            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                    .post(body)
                    .build();

            // Adicionar mensagens de log para depuração
            System.out.println("Enviando mensagem para o ChatGPT: " + json);

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Código de resposta não foi bem-sucedido: " + response.code());
            }

            String responseBody = response.body().string();

            // Processar a resposta e retornar a parte relevante
            // Você deve tratar adequadamente a resposta da API aqui
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao se comunicar com o chatbot: " + e.getMessage();
        }
    }


    private void updateChatView(String userMessage, String botResponse) {
        String currentChat = chatTextView.getText().toString();
        String newChat = currentChat + "\nYou: " + userMessage + "\nBot: " + botResponse + "\n";
        chatTextView.setText(newChat);
    }
}