package com.stasl.notes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AddNoteFragment extends Fragment
{
    EditText text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_note,container, false);
        text = (EditText)rootView.findViewById(R.id.noteTextField);
        return rootView;
    }
}