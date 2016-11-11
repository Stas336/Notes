package com.stasl.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

    final String TAG = BuildConfig.APPLICATION_ID;
    AddNoteFragment addNoteFragment;
    MainMenuFragment mainMenuFragment;
    FragmentControllerImpl fragmentController;
    MenuInflater drawerMenuInflater;
    DrawerLayout drawerLayout;
    Menu drawerMenu;
    ListView listView;
    Toolbar toolbar;
    DataBase dbhelper;
    Boolean isAlreadyCreated = false;
    int currentID = -1;
    SQLiteDatabase db;
    List<Integer> notesID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //TEMPORARY
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listToolbarMenu);
        notesID = new ArrayList<>();
        fragmentController = new FragmentControllerImpl(this);
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
        fragmentController.addFragment(R.id.fragmentContainer, mainMenuFragment);
        showNotes();
    }

    public void saveNewNote()
    {
        ContentValues cv = new ContentValues();
        String user_text = addNoteFragment.text.getText().toString();
        Log.d(TAG, "Inserting text in table");
        cv.put("note", user_text);
        long rowID = db.insert("notes", null, cv);
        Log.d(TAG, "row inserted, ID = " + rowID);
        clearTextField();
    }

    public void saveAlreadyCreatedNote()
    {
        ContentValues cv = new ContentValues();
        String user_text = addNoteFragment.text.getText().toString();
        cv.put("note", user_text);
        Log.d(TAG, "Updating text in table");
        try
        {
            db.update("notes", cv, "id = ?", new String[] {String.valueOf(notesID.get(currentID))});
        }catch (Exception ex)
        {
            Log.d(TAG, ex.getMessage());
        }
        Log.d(TAG, "row updated, ID = " + currentID);
        currentID = -1;
        isAlreadyCreated = false;
        clearTextField();
    }

    public void clearTextField()
    {
        addNoteFragment.text.setText("");
    }

    public void changeDrawerLayoutMenu(int menuResource)
    {
        drawerMenu.clear();
        drawerMenuInflater.inflate(menuResource, drawerMenu);
    }

    public void showNotes()
    {
        Cursor c = db.query("notes", null, null, null, null, null, null);
        List<String> notes = new ArrayList<>();
        List<Integer> notesIDNew = new ArrayList<>();
        if (c.moveToFirst())
        {
            int idColIndex = c.getColumnIndex("id");
            int textColIndex = c.getColumnIndex("note");
            do
            {
                notesIDNew.add(c.getInt(idColIndex));
                notes.add(c.getString(textColIndex));
            } while (c.moveToNext());
        }
        else
        {
            notes.add("There are no notes");
        }
        c.close();
        notesID = notesIDNew;
        mainMenuFragment.updateList(notes);
    }

    public void editNote(String previous_text, int id)
    {
        isAlreadyCreated = true;
        currentID = id;
        fragmentController.changeFragment(R.id.fragmentContainer, addNoteFragment);
        addNoteFragment.text.setText(previous_text);
        changeDrawerLayoutMenu(R.menu.toolbar_menu_note);
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
            if (isAlreadyCreated.equals(true))
            {
                saveAlreadyCreatedNote();
            }
            else
            {
                saveNewNote();
            }
            fragmentController.changeFragment(R.id.fragmentContainer, mainMenuFragment);
            changeDrawerLayoutMenu(R.menu.toolbar_menu_main);
            showNotes();
        }
        else if (id == R.id.action_delete)
        {
            if (currentID != -1)
            {
                Log.d(TAG, "ID is " + notesID.get(currentID));
                db.delete("notes", "id = ?", new String[]{String.valueOf(notesID.get(currentID))});
                Log.d(TAG, "Deleted value with id = " + notesID.get(currentID));
                notesID.clear();
                currentID = -1;
                isAlreadyCreated = false;
                clearTextField();
            }
            fragmentController.changeFragment(R.id.fragmentContainer, mainMenuFragment);
            drawerMenu.clear();
            drawerMenuInflater.inflate(R.menu.toolbar_menu_main, drawerMenu);
            showNotes();
        }
        else if (id == R.id.action_add)
        {
            fragmentController.changeFragment(R.id.fragmentContainer, addNoteFragment);
            changeDrawerLayoutMenu(R.menu.toolbar_menu_note);
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
            Log.d(TAG , "Creating database");
            db.execSQL("create table notes (id integer primary key autoincrement, note text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            throw new UnsupportedOperationException();
        }
    }
}