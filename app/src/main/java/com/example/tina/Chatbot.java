package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chatbot extends Fragment {

    private EditText userInputEditText;
    private TextView chatTextView;

    private List<String> greetings = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        userInputEditText = view.findViewById(R.id.userInputEditText);
        chatTextView = view.findViewById(R.id.chatTextView);
        Button sendButton = view.findViewById(R.id.sendButton);

        greetings.add("Olá");
        greetings.add("Oi");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return view;
    }

    private void sendMessage() {
        String userMessage = userInputEditText.getText().toString();
        String botResponse = getBotResponse(userMessage);

        updateChatView("Você", userMessage);
        updateChatView("Tina", botResponse);

        userInputEditText.setText("");
    }

    private String getBotResponse(String userMessage) {
        // Verificar se a mensagem do usuário é uma saudação
        if (isGreeting(userMessage.toLowerCase())) {
            return getRandomGreeting();
        }

        // Implementar outras respostas com base na mensagem do usuário
        return "Desculpe, não entendi. Como posso ajudar?";
    }

    private boolean isGreeting(String message) {
        for (String greeting : greetings) {
            if (message.contains(greeting.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String getRandomGreeting() {
        Random random = new Random();
        int index = random.nextInt(greetings.size());
        return greetings.get(index);
    }

    private void updateChatView(String sender, String message) {
        String currentChat = chatTextView.getText().toString();
        String newChat = currentChat + "\n" + sender + ": " + message + "\n";
        chatTextView.setText(newChat);
    }
}
