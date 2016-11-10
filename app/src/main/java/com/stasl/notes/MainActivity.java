package com.stasl.notes;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AddNoteFragment addNoteFragment;
    MainMenuFragment mainMenuFragment;
    FragmentTransaction fragmentTransaction;
    MenuInflater drawerMenuInflater;
    DrawerLayout drawerLayout;
    Menu drawerMenu;
    ListView listView;
    Toolbar toolbar;
    DataBase dbhelper;
    Boolean isPrevious = false;
    int previousID = 0;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //TEMPORARY
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listToolbarMenu);
        addNoteFragment = new AddNoteFragment();
        mainMenuFragment = new MainMenuFragment();
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        dbhelper = new DataBase(this);
        db = dbhelper.getWritableDatabase();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, mainMenuFragment);
        fragmentTransaction.commit();
        getFragmentManager().executePendingTransactions();
        showNotes();
    }

    public void saveNote()
    {
        ContentValues cv = new ContentValues();
        String user_text = addNoteFragment.text.getText().toString();
        Log.d(BuildConfig.APPLICATION_ID, "Inserting text in table");
        cv.put("note", user_text);
        long rowID = db.insert("notes", null, cv);
        Log.d(BuildConfig.APPLICATION_ID, "row inserted, ID = " + rowID);
    }

    public void savePreviousNote(int id)
    {
        ContentValues cv = new ContentValues();
        String user_text = addNoteFragment.text.getText().toString();
        cv.put("note", user_text);
        Log.d(BuildConfig.APPLICATION_ID, "Updating text in table");
        try
        {
            db.update("notes", cv, "id = ?", new String[] {String.valueOf(id + 1)});
        }catch (Exception ex)
        {
            Log.d(BuildConfig.APPLICATION_ID, ex.getMessage());
        }
        Log.d(BuildConfig.APPLICATION_ID, "row updated, ID = " + id);
        isPrevious = false;
    }

    public void showNotes()
    {
        Cursor c = db.query("notes", null, null, null, null, null, null);
        List<String> notes = new ArrayList<>();
        if (c.moveToFirst())
        {
            int idColIndex = c.getColumnIndex("id");
            int textColIndex = c.getColumnIndex("note");
            do
            {
                notes.add(c.getString(textColIndex));
            } while (c.moveToNext());
        }
        else
        {
            notes.add("There are no notes");
        }
        c.close();
        mainMenuFragment.updateList(notes);
    }

    public void editNote(String previous_text, int id)
    {
        isPrevious = true;
        previousID = id;
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, addNoteFragment);
        fragmentTransaction.commit();
        getFragmentManager().executePendingTransactions();
        drawerMenu.clear();
        addNoteFragment.text.setText(previous_text);
        drawerMenuInflater.inflate(R.menu.toolbar_menu_note, drawerMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        drawerMenu = menu;
        drawerMenuInflater = getMenuInflater();
        drawerMenuInflater.inflate(R.menu.toolbar_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_exit)
        {
            dbhelper.close();
            android.os.Process.killProcess(android.os.Process.myPid());
            return true;
        }
        else if (id == R.id.action_save)
        {
            if (isPrevious.equals(true))
            {
                savePreviousNote(previousID);
            }
            else
            {
                saveNote();
            }
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, mainMenuFragment);
            fragmentTransaction.commit();
            getFragmentManager().executePendingTransactions();
            drawerMenu.clear();
            drawerMenuInflater.inflate(R.menu.toolbar_menu_main, drawerMenu);
            showNotes();
        }
        else if (id == R.id.action_delete)
        {
            db.delete("notesDB", "where id == ", null);
        }
        else if (id == R.id.action_add)
        {
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, addNoteFragment);
            fragmentTransaction.commit();
            getFragmentManager().executePendingTransactions();
            drawerMenu.clear();
            drawerMenuInflater.inflate(R.menu.toolbar_menu_note, drawerMenu);
        }
        else if (id == R.id.action_search)
        {
            throw new UnsupportedOperationException();
        }
        else if (id == R.id.action_settings)
        {
            throw new UnsupportedOperationException();
        }
        return super.onOptionsItemSelected(item);
    }

    class DataBase extends SQLiteOpenHelper
    {
        DataBase(Context context) {
            super(context, "notesDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(BuildConfig.APPLICATION_ID , "Creating database");
            db.execSQL("create table notes (id integer primary key autoincrement, note text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            throw new UnsupportedOperationException();
        }
    }
}