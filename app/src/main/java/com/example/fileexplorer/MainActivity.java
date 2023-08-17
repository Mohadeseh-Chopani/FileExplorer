package com.example.fileexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddNewFolder{

    TextInputEditText et_search;
    MaterialButton btn_list,btn_grid;
    MaterialButtonToggleGroup toggleGroup;
    ImageView btn_add;
    File path;
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        path = getExternalFilesDir(null);

        btn_add = findViewById(R.id.btn_add_folder);
        et_search = findViewById(R.id.et_search);
        toggleGroup = findViewById(R.id.toggle_group);
        btn_list = findViewById(R.id.btn_main_list);
        btn_grid = findViewById(R.id.btn_main_grid);


        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_folder);
                if (fragment instanceof FragmentFiles){
                    ((FragmentFiles)fragment).Search(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        runtimepermissions();


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAdd dialogAdd=new DialogAdd();
                dialogAdd.show(getSupportFragmentManager(),null);
            }
        });


        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if (checkedId == R.id.btn_main_list && isChecked){
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_folder);
                    if (fragment instanceof FragmentFiles){
                        ((FragmentFiles) fragment).setView(ViewType.ROW);

                    }
                } else if (checkedId == R.id.btn_main_grid && isChecked){
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_folder);
                    if (fragment instanceof FragmentFiles){
                        ((FragmentFiles) fragment).setView(ViewType.GRID);
                    }
                }
            }
        });
    }

    private void runtimepermissions(){
        Dexter.withContext(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                displayFiles(String.valueOf(path),false);
            }


            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void displayFiles(String path , boolean onBackToStack) {
        Bundle bundle=new Bundle();
        FragmentFiles fragmentFiles=new FragmentFiles();
        bundle.putString("path",path);
        fragmentFiles.setArguments(bundle);

        FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_folder,fragmentFiles);
        fragmentTransaction.commit();

        if(onBackToStack)
            fragmentTransaction.addToBackStack(null);
    }

    public void displayFiles(String path){
        this.displayFiles(path,true);
    }

    @Override
    public void add_folder(String name) throws IOException {
        if(StorageHelper.isExternalStorageWritable()){
        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.fragment_folder);
        if(fragment instanceof FragmentFiles) {
            ((FragmentFiles) fragment).CreateFile(name);
        }
        }
    }
}