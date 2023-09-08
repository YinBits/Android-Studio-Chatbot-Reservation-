package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.HashMap;
import java.util.Map;

public class Chatbot extends Fragment {

    private EditText userInputEditText;
    private ScrollView messageScrollView;
    private LinearLayout messageContainer;

    // Dicionário de sinônimos para perguntas
    private Map<String, String> questionSynonyms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        userInputEditText = view.findViewById(R.id.userInputEditText);
        messageScrollView = view.findViewById(R.id.messageScrollView);
        messageContainer = view.findViewById(R.id.messageContainer);
        Button sendButton = view.findViewById(R.id.sendButton);

        // Inicialize o dicionário de sinônimos para perguntas
        questionSynonyms = new HashMap<>();
        questionSynonyms.put("como você está", "como vai você");
        questionSynonyms.put("o que você pode fazer", "quais são suas habilidades");
        questionSynonyms.put("qual é o seu nome", "como você se chama");
        questionSynonyms.put("você gosta de música", "você curte música");
        questionSynonyms.put("qual é a sua cor favorita", "sua cor preferida");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Mensagem de apresentação quando o fragmento é inflado
        String botIntroduction = "Olá, eu sou o Chatbot Tina e estou aqui para ajudar você a fazer reservas de mesa.";
        addMessage("Tina", botIntroduction);

        return view;
    }

    private void sendMessage() {
        String userMessage = userInputEditText.getText().toString().trim();

        if (!userMessage.isEmpty()) {
            // Responder de acordo com a mensagem do usuário
            String botResponse = getBotResponse(userMessage);

            // Adicionar a mensagem do usuário ao chat
            addMessage("Você", userMessage);

            // Adicionar a resposta do chatbot ao chat (se houver)
            if (!botResponse.isEmpty()) {
                addMessage("Tina", botResponse);
            }

            userInputEditText.setText("");

            // Rolar para a parte inferior da ScrollView
            messageScrollView.post(new Runnable() {
                @Override
                public void run() {
                    messageScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private void addMessage(String sender, String message) {
        TextView textView = new TextView(getContext());
        textView.setText(message);

        // Definir margens
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = getResources().getDimensionPixelSize(R.dimen.message_margin);
        layoutParams.setMargins(margin, margin, margin, margin);

        // Alinhar a mensagem do usuário à direita e do chatbot à esquerda
        layoutParams.gravity = sender.equals("Você") ? android.view.Gravity.END : android.view.Gravity.START;

        // Definir o background com base no remetente
        if (sender.equals("Você")) {
            textView.setBackgroundResource(R.drawable.user_message_background);
        } else {
            textView.setBackgroundResource(R.drawable.chatbot_message_background);
        }

        textView.setLayoutParams(layoutParams);

        // Adicionar a mensagem ao container
        messageContainer.addView(textView);
    }

    private String getBotResponse(String userMessage) {
        // Converter a mensagem do usuário para letras minúsculas para facilitar a correspondência
        userMessage = userMessage.toLowerCase();

        // Verificar sinônimos para perguntas na mensagem do usuário e fornecer respostas correspondentes
        for (Map.Entry<String, String> entry : questionSynonyms.entrySet()) {
            String question = entry.getKey();
            String synonym = entry.getValue();

            if (userMessage.contains(question) || userMessage.contains(synonym)) {
                return getResponseForQuestion(question);
            }
        }

        // Resposta padrão
        return "Desculpe, não entendi. Como posso ajudar?";
    }

    // Função para obter respostas para perguntas específicas
    private String getResponseForQuestion(String question) {
        // Lógica para retornar respostas com base na pergunta
        if (question.equals("como você está")) {
            return "Estou bem, obrigado por perguntar!";
        } else if (question.equals("o que você pode fazer")) {
            return "Eu sou um chatbot e posso responder a perguntas, contar piadas e muito mais!";
        } else if (question.equals("qual é o seu nome")) {
            return "Meu nome é Tina, sou um chatbot.";
        } else if (question.equals("você gosta de música")) {
            return "Eu não tenho preferências musicais, mas a música é maravilhosa!";
        } else if (question.equals("qual é a sua cor favorita")) {
            return "Eu sou apenas um programa de computador, então não tenho uma cor favorita.";
        }

        // Adicione mais respostas para outras perguntas conforme necessário
        return "Desculpe, não tenho uma resposta para essa pergunta específica.";
    }
}
