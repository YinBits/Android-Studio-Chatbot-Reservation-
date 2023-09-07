package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Chatbot extends Fragment {

    private EditText userInputEditText;
    private TextView chatTextView;

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
        String userMessage = userInputEditText.getText().toString();

        // Aqui você pode implementar a lógica para responder com base na mensagem do usuário
        String botResponse = getBotResponse(userMessage);

        // Atualizar a exibição do chat com a resposta do chatbot
        updateChatView("You", userMessage);
        updateChatView("Bot", botResponse);

        userInputEditText.setText("");
    }

    private String getBotResponse(String userMessage) {
        // Implemente a lógica para gerar uma resposta com base na mensagem do usuário
        // Aqui você pode usar condicionais, banco de dados, ou qualquer método de geração de respostas

        // Por exemplo, uma resposta simples com base na mensagem do usuário:
        if (userMessage.contains("olá")) {
            return "Olá! Como posso ajudar você?";
        } else if (userMessage.contains("como vai")) {
            return "Estou bem, obrigado! E você?";
        } else {
            return "Desculpe, não entendi. Pode reformular sua pergunta?";
        }
    }

    private void updateChatView(String sender, String message) {
        String currentChat = chatTextView.getText().toString();
        String newChat = currentChat + "\n" + sender + ": " + message + "\n";
        chatTextView.setText(newChat);
    }
}
