package com.example.tina;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Chatbot extends Fragment {

    private EditText userInputEditText;
    private ScrollView messageScrollView;
    private LinearLayout messageContainer;

    private List<String> questions;
    private int currentQuestionIndex = 0;
    private String reservationDate = "";
    private String reservationTime = "";
    private String numberOfSeats = "";
    private String tableNumber = "";

    private final String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private final String user;

    {
        assert userEmail != null;
        user = userEmail.substring(0, userEmail.indexOf('@')).replace(".", "-");
    }

    private boolean isMakingReservation = false;

    // Mapa para converter palavras em números para mesas e pessoas
    private final Map<String, Integer> tableAndPeopleWordToNumberMap = new HashMap<>();

    // Mapa para converter palavras em números para horários
    private final Map<String, Integer> timeWordToNumberMap = new HashMap<>();

    private boolean isChoosingAnotherTable = false;
    private String chosenTableNumber = "";

    private String userName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        userInputEditText = view.findViewById(R.id.userInputEditText);
        messageScrollView = view.findViewById(R.id.messageScrollView);
        messageContainer = view.findViewById(R.id.messageContainer);
        Button sendButton = view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> sendMessage());

        String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String user = userEmail.substring(0, userEmail.indexOf('@')).replace(".", "-");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Usuário").child(user);

        userReference.child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        String username = dataSnapshot.child("nome").getValue(String.class);
                        if (username != null) {
                            // Atualize a variável de instância userName
                            userName = Codex.decode(username);
                        } else {
                            // Lide com o caso em que "nome" é nulo
                        }
                    } catch (NullPointerException e) {
                        // Trate a exceção de maneira apropriada, por exemplo, exibindo uma mensagem de erro
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chatbot", "Erro ao acessar o banco de dados para obter o nome do usuário: " + databaseError.getMessage());
            }
        });

        questions = new ArrayList<>();
        questions.add("Qual data você deseja fazer a reserva? (DD/MM/AAAA)");
        questions.add("Qual horário você deseja reservar?");
        questions.add("Quantas pessoas estarão na reserva?");
        questions.add("Qual é o número da mesa desejada?");

        // Mensagem de apresentação quando o fragmento é inflado
        addBotMessage("Chatbot", "Olá, eu sou o Chatbot Tina e estou aqui para ajudar você a fazer reservas de mesa. Para começar, digite 'reserva' se deseja fazer uma reserva.");

        // Inicializar mapas de conversão para mesas e pessoas e para horários
        initializeTableAndPeopleWordToNumberMap();
        initializeTimeWordToNumberMap();

        return view;
    }

    // Função para inicializar o mapa de conversão para mesas e pessoas
    private void initializeTableAndPeopleWordToNumberMap() {
        tableAndPeopleWordToNumberMap.put("um", 1);
        tableAndPeopleWordToNumberMap.put("uma", 1);
        tableAndPeopleWordToNumberMap.put("dois", 2);
        tableAndPeopleWordToNumberMap.put("duas", 2);
        // Adicione mais mapeamentos conforme necessário para mesas e pessoas
    }

    // Função para inicializar o mapa de conversão para horários
    private void initializeTimeWordToNumberMap() {
        timeWordToNumberMap.put("uma", 1);
        timeWordToNumberMap.put("duas", 2);
        timeWordToNumberMap.put("três", 3);
        timeWordToNumberMap.put("quatro", 4);
        timeWordToNumberMap.put("cinco", 5);
        timeWordToNumberMap.put("seis", 6);
        timeWordToNumberMap.put("sete", 7);
        timeWordToNumberMap.put("oito", 8);
        timeWordToNumberMap.put("nove", 9);
        timeWordToNumberMap.put("dez", 10);
        timeWordToNumberMap.put("onze", 11);
        timeWordToNumberMap.put("doze", 12);
        timeWordToNumberMap.put("meio-dia", 12);
        timeWordToNumberMap.put("meia-noite", 24);
        // Adicione mais mapeamentos conforme necessário para horários


    }

    private void sendMessage() {
        String userMessage = userInputEditText.getText().toString().trim();

        if (!userMessage.isEmpty()) {
            if (isMakingReservation) {
                if (currentQuestionIndex < questions.size()) {
                    // O usuário está respondendo a uma pergunta durante o processo de reserva
                    handleReservation(userMessage);
                } else if (isChoosingAnotherTable) {
                    // O usuário está escolhendo outra mesa
                    // Aqui você pode adicionar lógica para capturar o número da mesa
                    String chosenTableNumber = convertWordToNumber(userMessage, tableAndPeopleWordToNumberMap);

                    // Verifique a disponibilidade da mesa escolhida com a data fornecida
                    checkTableAvailability(reservationDate, chosenTableNumber, () -> {
                        // A mesa está disponível, continue com o processo de reserva
                        this.chosenTableNumber = chosenTableNumber;
                        isChoosingAnotherTable = false;
                        continueWithReservation();
                    }, () -> {
                        // A mesa não está disponível, informe ao usuário e continue a coleta
                        addBotMessage("Tina", "A mesa " + chosenTableNumber + " não está disponível para a data selecionada. Por favor, escolha outra mesa ou digite 'cancelar' para encerrar a reserva.");
                        // Você pode adicionar mais lógica aqui, como permitir que o usuário escolha outra mesa novamente
                    });
                }
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

    private void continueWithReservation() {
        // Continue com o processo de reserva aqui
        // Você pode usar a variável chosenTableNumber para saber qual mesa o usuário escolheu
        // Certifique-se de validar a escolha da mesa e processar a reserva de acordo

        // Por exemplo, você pode adicionar chosenTableNumber à sua instância de Reserva:
        Reserva reserva = new Reserva();
        reserva.setNomeCliente(userName);
        reserva.setNumeroMesa(tableNumber);
        reserva.setDataReserva(reservationDate);
        reserva.setHorarioReserva(reservationTime);
        reserva.setNumeroPessoas(numberOfSeats);


        // Verifique a disponibilidade da mesa escolhida e prossiga
        checkTableAvailabilityAndProceed(reservationDate, tableNumber);
    }

    private void askNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            addMessage("Você", userInputEditText.getText().toString().trim()); // Adicione a resposta do usuário à direita

            if (currentQuestionIndex == 3) {
                // Se for a pergunta sobre a mesa, adicione a imagem da mesa
// Enviar a mensagem com imagem
                addBotMessageWithImage("Tina",questions.get(currentQuestionIndex), R.drawable.mapamesas);

// Enviar a mensagem de texto abaixo da imagem
                addBotMessage("Tina", questions.get(currentQuestionIndex));

            } else {
                // Caso contrário, adicione apenas o texto da pergunta
                addBotMessage("Tina", questions.get(currentQuestionIndex));
            }
        } else {
            // Todas as perguntas foram respondidas
            if (isMakingReservation) {
                // Realize a checagem e envie para o banco de dados
                checkTableAvailabilityAndProceed(reservationDate, tableNumber);
            } else {
                // Reinicie o chatbot ou conclua o processo aqui conforme necessário

                // Aqui você pode criar uma função para processar a reserva e enviar os dados para o banco
                registerReservationInDatabase();
                isMakingReservation = false;
                currentQuestionIndex = 0;
            }
        }
    }

    private void handleReservation(String userMessage) {
        if (currentQuestionIndex < questions.size()) {
            // Armazenar a resposta do usuário nas variáveis apropriadas com base na pergunta atual
            String currentQuestion = questions.get(currentQuestionIndex).toLowerCase(); // Converter para letras minúsculas

            // Com base na pergunta atual, atualize as variáveis de reserva com as respostas do usuário
            if (currentQuestion.contains("data")) {
                // Formate a data para o formato "DD-MM-AAAA" removendo barras
                reservationDate = formatUserInputDate(userMessage);
            } else if (currentQuestion.contains("horário")) {
                reservationTime = convertWordTo24HourFormat(userMessage, timeWordToNumberMap);
            } else if (currentQuestion.contains("quantas pessoas")) {
                numberOfSeats = convertWordToNumber(userMessage, tableAndPeopleWordToNumberMap);
            } else if (currentQuestion.contains("número da mesa")) {
                tableNumber = convertWordToNumber(userMessage, tableAndPeopleWordToNumberMap);
            }

            // Continue com a próxima pergunta
            currentQuestionIndex++;
            askNextQuestion();
        } else {
            // Todas as perguntas foram respondidas
            if (isMakingReservation) {
                // Verifique se todas as informações necessárias estão preenchidas
                if (reservationDate.isEmpty() || reservationTime.isEmpty() || numberOfSeats.isEmpty() || tableNumber.isEmpty()) {
                    addBotMessage("Tina", "Por favor, forneça todas as informações necessárias para concluir a reserva.");
                    // Pode adicionar mais lógica para tratar esse caso, se necessário
                } else {
                    // Continue com o processo de reserva
                    continueWithReservation();
                }
            }
        }
    }

    // Função para formatar a data no formato "dd-MM-yyyy" e remover barras
    private String formatUserInputDate(String userInput) {
        // Remova as barras da data
        userInput = userInput.replace("/", "");

        // Verifique se a entrada tem o formato correto (DDMMAAAA)
        if (userInput.matches("\\d{8}")) {
            // Formate a data como "dd-MM-yyyy"
            return userInput.substring(0, 2) + "-" + userInput.substring(2, 4) + "-" + userInput.substring(4);
        } else {
            // Caso a entrada não esteja no formato correto, retorne uma string vazia ou outra indicação de erro
            return "";
        }
    }

    // Função para converter palavras em números
    private String convertWordToNumber(String input, Map<String, Integer> wordToNumberMap) {
        // Tente converter a entrada diretamente em um número, se possível
        try {
            int numericValue = Integer.parseInt(input);
            return String.valueOf(numericValue);
        } catch (NumberFormatException e) {
            // A conversão direta falhou, então, divida a entrada em palavras
            String[] words = input.split("\\s+");

            // Inicialize um acumulador para o resultado
            int result = 0;
            boolean isAfterNoon = false;

            // Verifique cada palavra e adicione seu valor ao resultado
            for (String word : words) {
                String lowercaseWord = word.toLowerCase();

                if (wordToNumberMap.containsKey(lowercaseWord)) {
                    result += wordToNumberMap.get(lowercaseWord);

                    // Verifique se a palavra é "hora" e se estamos à tarde
                    if (lowercaseWord.equals("hora") && isAfterNoon) {
                        result -= 12; // Subtrai 12 horas para "uma hora" da tarde
                    }
                } else if (lowercaseWord.equals("da")) {
                    // Verifique se a palavra "da" indica que estamos falando de tarde
                    isAfterNoon = true;
                }
            }

            // Converta o resultado de volta para uma string
            return String.valueOf(result);
        }
    }

    private String convertWordTo24HourFormat(String input, Map<String, Integer> wordToNumberMap) {
        String[] words = input.split("\\s+");
        int result = 0;
        boolean isAfterNoon = false;

        for (String word : words) {
            String lowercaseWord = word.toLowerCase();

            if (wordToNumberMap.containsKey(lowercaseWord)) {
                result += wordToNumberMap.get(lowercaseWord);
            } else if (lowercaseWord.equals("manhã")) {
                // Se "manhã" estiver presente, defina isAfterNoon como false
                isAfterNoon = false;
            } else if (lowercaseWord.equals("noite")) {
                // Se "noite" estiver presente, defina isAfterNoon como true
                isAfterNoon = true;
            }
        }

        // Se for à tarde (noite), adicione 12 horas para converter para 24 horas
        if (isAfterNoon) {
            result += 12;
        }

        return String.valueOf(result);
    }

    private void addBotMessage(String sender, String message) {
        // Adicione a mensagem do bot ao chat
        addMessage(sender, message);
        // Role para a parte inferior da ScrollView
        messageScrollView.post(() -> messageScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void addMessage(String sender, String message) {
        TextView textView = new TextView(getContext());
        textView.setText(message);

        // Definir margens
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Definir margens padrão de 10
        int defaultMargin = getResources().getDimensionPixelSize(R.dimen.message_margin);
        layoutParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);

        // Alinhar a mensagem do usuário à direita e do chatbot à esquerda
        layoutParams.gravity = sender.equals("Você") ? android.view.Gravity.END : android.view.Gravity.START;

        // Definir o background com base no remetente
        if (sender.equals("Você")) {
            // Margem direita de 100 para mensagens do usuário
            layoutParams.setMargins(100, defaultMargin, defaultMargin, defaultMargin);
            textView.setBackgroundResource(R.drawable.user_message_background);
        } else {
            // Margem esquerda de 100 para mensagens do chatbot
            layoutParams.setMargins(defaultMargin, defaultMargin, 100, defaultMargin);
            textView.setBackgroundResource(R.drawable.chatbot_message_background);
        }

        textView.setLayoutParams(layoutParams);

        // Cor do texto
        textView.setTextColor(getResources().getColor(android.R.color.white));

        // Adicionar a mensagem ao container
        messageContainer.addView(textView);
    }


    // Função para converter a entrada da pessoa em um objeto de data do Firebase
    private Date convertUserInputToDate(String userInput) {
        try {
            // Substitua as barras por traços no formato da data
            userInput = userInput.replace("/", "-");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return dateFormat.parse(userInput);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void registerReservationInDatabase() {
        // Crie uma instância da sua classe Reserva e preencha os dados
        Reserva reserva = new Reserva();
        // Adicione o nome do usuário à reserva
        reserva.setNomeCliente(userName);
        reserva.setDataReserva(reservationDate);
        reserva.setHorarioReserva(reservationTime);
        reserva.setNumeroMesa(tableNumber);
        reserva.setNumeroPessoas(numberOfSeats);

        // Use o ID do usuário autenticado como parte da chave da reserva
        String reservationKey = user; // Crie uma chave única para a reserva


        // Enviar dados para o Firebase
        DatabaseReference reservasRef = FirebaseDatabase.getInstance().getReference("Reservas");

        if (reservationKey != null) {
            reservasRef.child(reservationKey).setValue(reserva, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    // Ocorreu um erro ao enviar os dados
                    Log.e("Chatbot", "Erro ao enviar os dados da reserva: " + databaseError.getMessage());
                } else {
                    // Os dados da reserva foram enviados com sucesso
                    // Realize ações adicionais ou notifique o usuário da conclusão da reserva
                    addBotMessage("Tina", "Reserva realizada com sucesso!");
                }
            });
        } else {
            // Ocorreu um erro ao criar uma chave única para a reserva
            Log.e("Chatbot", "Erro ao criar uma chave única para a reserva.");
        }
    }

    private void checkTableAvailabilityAndProceed(String date, String tableNumber) {
        checkTableAvailability(date, tableNumber, () -> {
            // A mesa está disponível, continue com o processo de reserva
            registerReservationInDatabase();
        }, () -> {
            // A mesa não está disponível, informe ao usuário
            addBotMessage("Tina", "A mesa " + tableNumber + " não está mais disponível para a data e horário selecionados. Qual mesa você deseja?");
            isChoosingAnotherTable = true;
        });
    }

    private void checkTableAvailability(String date, String tableNumber, Runnable onAvailable, Runnable onNotAvailable) {
        DatabaseReference reservasRef = FirebaseDatabase.getInstance().getReference("Reservas");
        reservasRef.orderByChild("dataReserva").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Chatbot", "DataSnapshot: " + dataSnapshot.toString());
                boolean mesaDisponivel = true;

                for (DataSnapshot reservaSnapshot : dataSnapshot.getChildren()) {
                    String numeroMesa = reservaSnapshot.child("numeroMesa").getValue(String.class);

                    if (numeroMesa != null && numeroMesa.equals(tableNumber)) {
                        // A mesa está ocupada na data fornecida
                        mesaDisponivel = false;
                        break;
                    }
                }

                if (mesaDisponivel) {
                    // A mesa está disponível, execute a ação onAvailable
                    onAvailable.run();
                } else {
                    // A mesa não está disponível, execute a ação onNotAvailable
                    onNotAvailable.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chatbot", "Erro ao acessar o banco de dados: " + databaseError.getMessage());
            }
        });
    }

    private void addBotMessageWithImage(String sender, String message, int imageResourceId) {
        // Crie uma View para conter a imagem e o texto
        LinearLayout messageLayout = new LinearLayout(getContext());
        messageLayout.setOrientation(LinearLayout.VERTICAL);

        // Adicione a imagem
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(imageResourceId);
        // Defina o tamanho da imagem como 200x200 pixels
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(1000, 1000);
        imageView.setLayoutParams(imageParams);
        messageLayout.addView(imageView);

        // Defina margens e layout para a mensagem
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int defaultMargin = getResources().getDimensionPixelSize(R.dimen.message_margin);
        layoutParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        layoutParams.gravity = sender.equals("Você") ? Gravity.END : Gravity.START;
        messageLayout.setLayoutParams(layoutParams);

        // Defina o background com base no remetente
        if (sender.equals("Você")) {
            layoutParams.setMargins(100, defaultMargin, defaultMargin, defaultMargin);
            messageLayout.setBackgroundResource(R.drawable.user_message_background);
        } else {
            layoutParams.setMargins(defaultMargin, defaultMargin, 100, defaultMargin);
            messageLayout.setBackgroundResource(R.drawable.chatbot_message_background);
        }

        // Adicione a mensagem ao container
        messageContainer.addView(messageLayout);

        // Role para a parte inferior da ScrollView
        messageScrollView.post(() -> messageScrollView.fullScroll(View.FOCUS_DOWN));
    }

}

