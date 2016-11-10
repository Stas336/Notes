package com.stasl.notes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainMenuFragment extends Fragment
{
    ListView listNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listNotes = (ListView)rootView.findViewById(R.id.listNotes);
        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                ((MainActivity)getActivity()).editNote(listNotes.getItemAtPosition(i).toString(), i);
            }
        });
        return rootView;
    }

    public void updateList(List<String> notes)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, notes);
        listNotes.setAdapter(adapter);
    }
}