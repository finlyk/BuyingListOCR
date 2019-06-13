package com.example.buyinglistocr.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.buyinglistocr.R;
import com.example.buyinglistocr.model.AdapterLists;
import com.example.buyinglistocr.model.List;
import com.example.buyinglistocr.model.ListManager;
import com.example.buyinglistocr.model.SharedPrefManager;

import java.util.ArrayList;

/**
 * Allow to represent the main activity
 */
public class ListsActivity extends AppCompatActivity {

    // The list DAO
    private ListManager listManager;

    // The ArrayList of list
    private ArrayList<List> lists;

    // The recycler view
    private RecyclerView rv;

    /**
     * Method that be executed during the creation of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Define the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(SharedPrefManager.getInstance(this).getLogin() + " " + SharedPrefManager.getInstance(this).getId()+ " - My Lists");

        // Get the list DAO
        listManager = new ListManager(this);

        // Get the data
        lists = listManager.get();

        // Define the recycler view
        rv = findViewById(R.id.recyclerViewLists);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(new AdapterLists(ListsActivity.this, lists));

        // Define the buttonAdd
        FloatingActionButton buttonAdd = findViewById(R.id.floatingButtonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Launch the alert dialog
                showAlertDialogButtonClicked(view);

            }

        });

    }

    /**
     * Allow to display the menu on the toolbar
     * @param menu - The menu
     * @return - A boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);

    }
    /**
     * Allow to define the action for each item
     * @param item - The item
     * @return - A boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.participate:

               /** AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Delete")
                        .setMessage("Are you sure ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                listDAO.delete(list.getId());




                                Intent MainIntent = new Intent(ListView.this, MainActivity.class);
                                startActivity(MainIntent);

                            }

                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                closeContextMenu();

                            }

                        })

                        .create()
                        .show();
                        **/
                Intent ParticipateIntent = new Intent(ListsActivity.this, AddCorres.class);
                startActivity(ParticipateIntent);
                break;




        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Method that be executed during the resume of the activity
     */
    @Override
    public void onResume(){

        super.onResume();

        // Notify the data set changed
        rv.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onBackPressed(){
        finish();
    }

    /**
     * Allow to define the alert dialog
     * @param view - The view
     */
    public void showAlertDialogButtonClicked(View view) {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_create_list, null);
        builder.setView(customLayout);
        builder.setTitle("Add List");
        // Define the positive button
        builder.setPositiveButton("Make", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText editText = customLayout.findViewById(R.id.name);

                // Create the new list with the data of the edit text
                List list = new List(0, editText.getText().toString(), 0, 0, 0);

                // Add this list to the database and get it id
                long idList = listManager.add(list);

                list.setId(idList);

                // Add this list to the ArrayList
                lists.add(list);

                // Notify the recycler view that a data is inserted
                rv.getAdapter().notifyItemInserted(lists.size() - 1);

            }

        });

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public ArrayList<List> getLists() {

        return lists;

    }

    public void setLists(ArrayList<List> lists) {

        this.lists = lists;

    }

}