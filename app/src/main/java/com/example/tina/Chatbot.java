package com.example.tina;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chatbot extends Fragment {

    private EditText userInputEditText;
    private ScrollView messageScrollView;
    private LinearLayout messageContainer;

    private List<String> questions;
    private int currentQuestionIndex = 0;
    private String reservationTime = "";
    private String numberOfSeats = "";
    private String tableNumber = "";

    private boolean isMakingReservation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        userInputEditText = view.findViewById(R.id.userInputEditText);
        messageScrollView = view.findViewById(R.id.messageScrollView);
        messageContainer = view.findViewById(R.id.messageContainer);
        Button sendButton = view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        questions = new ArrayList<>();
        questions.add("Qual horário você deseja reservar?");
        questions.add("Quantas pessoas estarão na reserva?");
        questions.add("Qual é o número da mesa desejada?");

        // Mensagem de apresentação quando o fragmento é inflado
        addBotMessage("Tina", "Olá, eu sou o Chatbot Tina e estou aqui para ajudar você a fazer reservas de mesa. Para começar, digite 'reserva' se deseja fazer uma reserva.");

        return view;
    }

    private void sendMessage() {
        String userMessage = userInputEditText.getText().toString().trim();

        if (!userMessage.isEmpty()) {
            if (isMakingReservation) {
                // Responder de acordo com a mensagem do usuário durante o processo de reserva
                handleReservation(userMessage);
            } else {
                // Verificar se o usuário deseja fazer uma reserva
                if (userMessage.equalsIgnoreCase("reserva")) {
                    isMakingReservation = true;
                    // Iniciar o processo de reserva
                    askNextQuestion();
                } else {
                    // Mensagem de confirmação para repetir a pergunta
                    addBotMessage("Tina", "Desculpe, não entendi. Você gostaria de fazer uma reserva de mesa? (reserva)");
                }
            }

            userInputEditText.setText("");
        }
    }

    private void askNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            addMessage("Você", userInputEditText.getText().toString().trim()); // Adiciona a resposta do usuário à direita
            addBotMessage("Tina", questions.get(currentQuestionIndex));
        } else {
            // Todas as perguntas foram respondidas, você pode enviar os dados para o Firebase aqui
            // Após o processamento, você pode reiniciar o chatbot ou exibir uma mensagem de conclusão
            addBotMessage("Tina", "Reserva de mesa concluída! Um e-mail de confirmação será enviado em breve.");

            // Reiniciar o chatbot ou concluir o processo aqui conforme necessário
            isMakingReservation = false;
            currentQuestionIndex = 0;

            // Adicione logs para verificar os dados coletados
            Log.d("DadosColetados", "Horário: " + reservationTime);
            Log.d("DadosColetados", "Número de Lugares: " + numberOfSeats);
            Log.d("DadosColetados", "Número da Mesa: " + tableNumber);
        }
    }

    private void handleReservation(String userMessage) {
        if (currentQuestionIndex < questions.size()) {
            // Armazenar a resposta do usuário nas variáveis apropriadas com base na pergunta atual
            String currentQuestion = questions.get(currentQuestionIndex).toLowerCase(); // Converter para letras minúsculas

            // Extrair apenas números da mensagem do usuário
            String extractedNumbers = extractNumbers(userMessage);

            // Com base na pergunta atual, atualize as variáveis de reserva com as respostas do usuário
            if (currentQuestion.contains("horário")) {
                reservationTime = extractedNumbers;
            } else if (currentQuestion.contains("quantas pessoas")) {
                numberOfSeats = extractedNumbers;
            } else if (currentQuestion.contains("número da mesa")) {
                tableNumber = extractedNumbers;
            }

            // Continue com a próxima pergunta
            currentQuestionIndex++;
            askNextQuestion();
        }
    }

    // Função para extrair apenas números de uma string
    private String extractNumbers(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        StringBuilder extractedNumbers = new StringBuilder();
        while (matcher.find()) {
            extractedNumbers.append(matcher.group());
        }
        return extractedNumbers.toString();
    }

    private void addBotMessage(String sender, String message) {
        // Adicione a mensagem do bot ao chat
        addMessage(sender, message);
        // Role para a parte inferior da ScrollView
        messageScrollView.post(new Runnable() {
            @Override
            public void run() {
                messageScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
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
}
