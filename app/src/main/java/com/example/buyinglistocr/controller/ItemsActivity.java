package com.example.buyinglistocr.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buyinglistocr.BuildConfig;
import com.example.buyinglistocr.R;
import com.example.buyinglistocr.model.Item;
import com.example.buyinglistocr.model.ItemManager;
import com.example.buyinglistocr.model.List;
import com.example.buyinglistocr.model.ListManager;
import com.example.buyinglistocr.util.AnalyseData;
import com.example.buyinglistocr.util.SharedPreferencesList;
import com.example.buyinglistocr.util.VolleyCallback;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ItemsActivity extends AppCompatActivity {

    private ListManager listManager;
    private ItemManager itemManager;

    private List list;

    private ArrayList<Item> items;

    private TextView textViewSpent;

    private RecyclerView recyclerView;

    /***********************************************************************************************
     * Partie photo
     **********************************************************************************************/

    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = ListsActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private String mCurrentPhotoPath;

    /***********************************************************************************************
     * Partie photo
     **********************************************************************************************/

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        listManager = new ListManager(ItemsActivity.this);
        itemManager = new ItemManager(ItemsActivity.this);

        list = SharedPreferencesList.getInstance(ItemsActivity.this).getList();

        Toolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(list.getName());

        textViewSpent = findViewById(R.id.textViewSpent);
        textViewSpent.setText(textViewSpent.getText() + " " + list.getSpent());

        items = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AdapterItems(this, items, recyclerView));

        FloatingActionButton buttonAddItem = findViewById(R.id.floatingButtonAddItems);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Launch the alert dialog
                showAlertDialogButtonClicked(view);

            }

        });

        /*******************************************************************************************
         * Partie photo
         ******************************************************************************************/

        final Activity activity = this;

        checkPermission();

        this.findViewById(R.id.floatingButtonCamera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                checkPermission();
                dispatchTakePictureIntent();

            }

        });

        /*******************************************************************************************
         * Photo
         *******************************************************************************************/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.itemModify:

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_modify_list, null);
                builder2.setView(customLayout);

                builder2.setTitle("Modify")

                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText editText = customLayout.findViewById(R.id.name);

                                list.setName(editText.getText().toString());

                                getSupportActionBar().setTitle(list.getName());

                                SharedPreferencesList.getInstance(ItemsActivity.this).setList(list);

                                listManager.update(list);

                            }

                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                closeContextMenu();

                            }

                        });

                AlertDialog dialog = builder2.create();
                dialog.show();

                break;

            case R.id.itemDelete:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Delete")

                    .setMessage("Are you sure ?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            listManager.delete(list.getId());

                            list.setName(null);

                            SharedPreferencesList.getInstance(ItemsActivity.this).setList(list);

                            ItemsActivity.this.finish();

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

                break;

            case R.id.itemShare:

                break;

        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Allow to define the alert dialog
     * @param view - The view
     */
    public void showAlertDialogButtonClicked(View view) {

        final Context context = this;

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_create_item, null);
        builder.setView(customLayout);
        builder.setTitle("Add Product");
        final EditText editText = customLayout.findViewById(R.id.name);

        final EditText editTextQte = customLayout.findViewById(R.id.quantities);
        // Define the positive button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                if(isPresent(editText.getText().toString(), list.getId())) {

                    Toast toast = Toast.makeText(context, "This name already exist", Toast.LENGTH_SHORT);
                    toast.show();

                }



                    else {

                    int quantityDesired = 1;

                    if (editTextQte.getText().length() > 0) {
                        System.out.println("true");
                        if (Integer.parseInt(editTextQte.getText().toString())>0){
                            quantityDesired = Integer.parseInt(editTextQte.getText().toString());
                        }
                    }

                    // Create the new item with the data of the edit text
                    Item item = new Item(0, editText.getText().toString(), quantityDesired, 0, new String(), 0, list.getId());

                    // Add this item to the database and get it id
                    int idItem = itemManager.add(item);

                    // TEST
                    System.out.println("TEST LISTVIEW - quantityDesired : " + itemManager.getItem(idItem).getQuantityDesired());

                    item.setId(idItem);

                    // Add this item to the ArrayList
                    items.add(item);
                    // Notify the recycler view that a data is inserted
                    recyclerView.getAdapter().notifyItemInserted(items.size() - 1);

                }

            }

        });

        // Create and show the alert dialog
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getText().length()<1){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    Toast toast = Toast.makeText(context, "You need to choose a name", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (!editText.getText().toString().startsWith(" ") && (editTextQte.getText().length()<1 || Integer.parseInt(editTextQte.getText().toString()) > 0)){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextQte.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTextQte.getText().length()>0) {
                    if (Integer.parseInt(editTextQte.getText().toString()) <= 0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        Toast toast = Toast.makeText(context, "Impossible to set a quantity at 0", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (editText.getText().length()>1 && !editText.getText().toString().startsWith(" ")) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                } else if (editText.getText().length()>1 && !editText.getText().toString().startsWith(" ")){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Allow to know if an item exist with the same name in this list
     * @param name - The name
     * @param idList - The list id
     * @return - True if the name exist, false else
     */
    public boolean isPresent(String name, long idList) {

        // The return value
        Boolean ret = false;

        // Get all items of our list
        ArrayList<Item> items = itemManager.get(idList);

        for(Item item : items) {

            if(item.getName().equals(name)) {

                ret = true;

            }

        }

        return ret;

    }

    /*******************************************************************************************
     * Photo
     *******************************************************************************************/

    //Gestion Des Permissions
    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 122);
        }


    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1024);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024) {
            if (resultCode == Activity.RESULT_OK) {
                prepareTessData();
                startOCR(outputFileDir);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void prepareTessData() {
        try {
            File dir = getExternalFilesDir(TESS_DATA);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = getAssets().list("");
            for (String fileName : fileList) {
                String pathToDataFile = dir + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startOCR(Uri imageUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            System.out.println(options.toString());

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            String result = this.getText(bitmap);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        recreate();
    }

    //ORIENTATION BITMAP
    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        Bitmap bOutput;
        float degreees = 90;//rotation degree
        Matrix matrix = new Matrix();
        matrix.setRotate(degreees);
        bOutput = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        return bOutput;
    }

    private String getText(Bitmap bitmap) {
        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        String dataPath = getExternalFilesDir("/").getPath() + "/";
        tessBaseAPI.init(dataPath, "fra",TessBaseAPI.OEM_TESSERACT_ONLY);
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "aàAbBcçCdDeEéèêfFgGhHiIjJkKlLmMnNoôOpPqQrRsStTuùUvVwWxXyYzZ1234567890°\',.;+*-_%/ ");
        /**
         * Selon le téléphone commentez
         */
        //tessBaseAPI.setImage(rotateBitmap(bitmap, 90));
        tessBaseAPI.setImage(bitmap);


        String retStr = "No result";
        try {
            retStr = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Bitmap bitmapfinal = WriteFile.writeBitmap(tessBaseAPI.getThresholdedImage());

        tessBaseAPI.end();

        /**Analyse Data*/
        AnalyseData test = new AnalyseData(retStr, ItemsActivity.this, list.getId());
        System.out.println("IdList is : "+list.getId());
        System.out.println(test.getTextBrut());

        test.clean(test.getTextBrut());
        test.tableToCorrespondenceTable(test.getTable());
        double spent = test.removePurchase(test.getCorrespondanceTable());

        List lists = null;

        listManager.get(SharedPreferencesList.getInstance(ItemsActivity.this).getId(), new VolleyCallback() {
            @Override
            public void onSuccess(String response) {

            }
        });
        spent = spent + lists.getSpent();
        lists.setSpent(spent);
        listManager.update(lists);

        for(int i = 0; i<test.getCorrespondanceTable().size(); i++) {
            System.out.println("Element TABLE CORRES numéro "+i+" "+test.getCorrespondanceTable().get(i).getName());

            /** AJOUTER DANS SAVEPURCHASE ?????  **/
            /**if(test.getCorrespondanceTable().get(i).isFindCores()){
                SavePurchase s = new SavePurchase(test.getCorrespondanceTable().get(i).getName());
                savePurchaseDAO.add(s);
            }*/


        }



        return retStr;
    }

    @Override
    public void onResume(){

        super.onResume();

        recyclerView.getAdapter().notifyDataSetChanged();

    }

}