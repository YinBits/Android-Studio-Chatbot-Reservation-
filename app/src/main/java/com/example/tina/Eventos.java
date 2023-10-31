package com.example.tina;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Eventos extends Fragment {

    private CalendarView calendarView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Map<String, List<String>> eventsByDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventos, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        listView = view.findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        eventsByDate = new HashMap<>();

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventosRef = database.getReference("eventos");

        eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsByDate.clear();
                adapter.clear();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Evento evento = eventSnapshot.getValue(Evento.class);
                    if (evento != null) {
                        String date = evento.getDate();
                        String eventInfo = evento.getTitle() + ": " + evento.getDescription();

                        // Check if the date already exists in the map
                        if (eventsByDate.containsKey(date)) {
                            eventsByDate.get(date).add(eventInfo);
                        } else {
                            List<String> events = new ArrayList<>();
                            events.add(eventInfo);
                            eventsByDate.put(date, events);
                        }
                    }
                }

                // Populate the ListView with events for the selected date
                long selectedDate = calendarView.getDate();
                String selectedDateString = // Convert selectedDate to a suitable format;

                if (eventsByDate.containsKey(selectedDateString)) {
                    List<String> events = eventsByDate.get(selectedDateString);
                    adapter.addAll(events);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Handle date change and update the ListView
                // You can use the eventsByDate map to retrieve events for the selected date
                // and update the adapter accordingly.
                String selectedDate = null; // Convert year, month, and dayOfMonth to a suitable date format;
                if (eventsByDate.containsKey(selectedDate)) {
                    List<String> events = eventsByDate.get(selectedDate);
                    adapter.clear();
                    adapter.addAll(events);
                }
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}
