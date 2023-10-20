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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser currentUser;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userInputEditText = view.findViewById(R.id.userInputEditText);
        messageScrollView = view.findViewById(R.id.messageScrollView);
        messageContainer = view.findViewById(R.id.messageContainer);
        Button sendButton = view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> sendMessage());

        questions = new ArrayList<>();
        questions.add("Qual data você deseja fazer a reserva? (DD/MM/AAAA)");
        questions.add("Qual horário você deseja reservar?");
        questions.add("Quantas pessoas estarão na reserva?");
        questions.add("Qual é o número da mesa desejada?");

        // Mensagem de apresentação quando o fragmento é inflado
        addBotMessage("Tina", "Olá, eu sou o Chatbot Tina e estou aqui para ajudar você a fazer reservas de mesa. Para começar, digite 'reserva' se deseja fazer uma reserva.");

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
                // Responder de acordo com a mensagem do usuário durante o processo de reserva
                if (currentQuestionIndex == 0) {
                    // O usuário está fornecendo a data da reserva
                    reservationDate = formatUserInputDate(userMessage);
                } else if (currentQuestionIndex == 1) {
                    // O usuário está fornecendo o horário da reserva
                    reservationTime = convertWordTo24HourFormat(userMessage, timeWordToNumberMap);
                } else if (currentQuestionIndex == 2) {
                    // O usuário está fornecendo o número de pessoas
                    numberOfSeats = convertWordToNumber(userMessage, tableAndPeopleWordToNumberMap);
                } else if (currentQuestionIndex == 3) {
                    // O usuário está fornecendo o número da mesa
                    tableNumber = convertWordToNumber(userMessage, tableAndPeopleWordToNumberMap);

                    // Agora que temos todas as informações, verifique a disponibilidade
                    checkTableAvailability(reservationDate, reservationTime);
                } else if (isChoosingAnotherTable) {
                    // O usuário está escolhendo outra mesa
                    // Aqui você pode adicionar lógica para capturar o número da mesa
                    // Verifique se o usuário forneceu um número de mesa válido e armazene-o na variável chosenTableNumber
                    // Certifique-se de validar a entrada do usuário, como verificar se corresponde a uma mesa disponível

                    // Após capturar a escolha da mesa, você pode continuar com o processo de reserva ou tomar a ação apropriada
                    isChoosingAnotherTable = false;
                    continueWithReservation();
                }

                // Continue com a próxima pergunta
                currentQuestionIndex++;
                askNextQuestion();
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
        reserva.setNumeroMesa(chosenTableNumber);

        // Após concluir o processo, envie os dados da reserva para o Firebase ou faça a ação apropriada
        registerReservationInDatabase();
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

            // Criar uma instância da sua classe Reserva e preencher os dados
            Reserva reserva = new Reserva();
            // Adicione o nome do usuário à reserva
            addUserToReservation(reserva);
            reserva.setDataReserva(reservationDate);
            reserva.setHorarioReserva(reservationTime);
            reserva.setNumeroMesa(tableNumber);
            reserva.setNumeroPessoas(numberOfSeats);

            // Enviar dados para o Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuário").child(user);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Obtém o nome do usuário da tabela "Usuário"
                    String clientName = dataSnapshot.child("nome").getValue(String.class);
                    if (clientName != null) {
                        // Defina o nome do cliente na reserva
                        reserva.setNomeCliente(clientName);
                    }

                    // Enviar a reserva para o Firebase
                    FirebaseDatabase.getInstance().getReference().child("Reservas").child(user).setValue(reserva)
                            .addOnSuccessListener(e -> Log.i("DadosColetados", "Dados enviados com sucesso!"))
                            .addOnFailureListener(e -> Log.i("DadosColetados", "Falha ao enviar dados: " + e.getMessage()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("DadosColetados", "Erro ao obter dados do usuário: " + databaseError.getMessage());
                }
            });
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
        }
    }

    // Função para formatar a data no formato "DD-MM-AAAA" e remover barras
    private String formatUserInputDate(String userInput) {
        // Remova as barras da data
        userInput = userInput.replace("/", "");

        // Verifique se a entrada tem o formato correto (DDMMAAAA)
        if (userInput.matches("\\d{8}")) {
            // Formate a data como DD-MM-AAAA
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

    // Função para adicionar o nome do usuário autenticado aos dados da reserva
    private void addUserToReservation(Reserva reserva) {
        if (currentUser != null && currentUser.getDisplayName() != null) {
            reserva.setNomeCliente(currentUser.getDisplayName());
        }
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

    private void checkTableAvailability(String date, String time) {
        DatabaseReference reservasRef = FirebaseDatabase.getInstance().getReference("Reservas");
        reservasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> mesasOcupadas = new ArrayList<>();

                // Itera sobre as reservas para verificar mesas ocupadas
                for (DataSnapshot reservaSnapshot : dataSnapshot.getChildren()) {
                    String numeroMesa = reservaSnapshot.child("numeroMesa").getValue(String.class);
                    String dataReserva = reservaSnapshot.child("dataReserva").getValue(String.class);
                    String horarioReserva = reservaSnapshot.child("horarioReserva").getValue(String.class);

                    if (numeroMesa != null && dataReserva != null && horarioReserva != null &&
                            numeroMesa.equals(tableNumber) && dataReserva.equals(date) && horarioReserva.equals(time)) {
                        mesasOcupadas.add(numeroMesa);
                    }
                }

                if (mesasOcupadas.isEmpty()) {
                    // A mesa está disponível, então você pode prosseguir com o cadastro
                    registerReservationInDatabase();
                } else {
                    // Algumas mesas estão ocupadas, mostre a lista
                    StringBuilder mensagem = new StringBuilder("As seguintes mesas já estão ocupadas para a data e horário selecionados:");
                    for (String mesa : mesasOcupadas) {
                        mensagem.append("\n").append(mesa);
                    }
                    mensagem.append("\nFora as mesas ocupadas, qual mesa você deseja?");
                    addBotMessage("Tina", mensagem.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chatbot", "Erro ao acessar o banco de dados: " + databaseError.getMessage());
            }
        });
    }

    private void registerReservationInDatabase() {
        // Criar uma instância da sua classe Reserva e preencher os dados
        Reserva reserva = new Reserva();
        // Adicione o nome do usuário à reserva
        addUserToReservation(reserva);
        reserva.setDataReserva(reservationDate);
        reserva.setHorarioReserva(reservationTime);
        reserva.setNumeroMesa(tableNumber);
        reserva.setNumeroPessoas(numberOfSeats);

        // Enviar dados para o Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuário").child(user);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtém o nome do usuário da tabela "Usuário"
                String clientName = dataSnapshot.child("nome").getValue(String.class);
                if (clientName != null) {
                    // Defina o nome do cliente na reserva
                    reserva.setNomeCliente(clientName);
                }

                // Enviar a reserva para o Firebase
                DatabaseReference reservaRef = FirebaseDatabase.getInstance().getReference("Reservas").child(user);
                reservaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Verifique se a mesa ainda está disponível para a data e horário selecionados
                        if (dataSnapshot.exists()) {
                            // A mesa não está mais disponível, então pergunte ao usuário qual mesa ele deseja
                            addBotMessage("Tina", "A mesa " + tableNumber + " não está mais disponível para a data e horário selecionados. Qual mesa você deseja?");
                        } else {
                            // A mesa está disponível, então cadastre a reserva no banco
                            reservaRef.setValue(reserva)
                                    .addOnSuccessListener(e -> {
                                        // Reserva cadastrada com sucesso
                                        addBotMessage("Tina", "Reserva de mesa concluída! Um e-mail de confirmação será enviado em breve.");
                                        // Reinicie o chatbot ou conclua o processo conforme necessário
                                        isMakingReservation = false;
                                        currentQuestionIndex = 0;
                                    })
                                    .addOnFailureListener(e -> Log.i("DadosColetados", "Falha ao enviar dados: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Chatbot", "Erro ao verificar a disponibilidade da mesa: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DadosColetados", "Erro ao obter dados do usuário: " + databaseError.getMessage());
            }
        });
    }

}



