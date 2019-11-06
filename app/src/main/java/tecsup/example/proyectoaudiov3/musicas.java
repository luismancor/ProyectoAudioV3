package tecsup.example.proyectoaudiov3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class musicas extends AppCompatActivity {

    private ListView lstMusicas;
    private List<String> item = null;
    private String ruta = Environment.getExternalStorageDirectory() + "/Download/";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicas);


        item = new ArrayList<String>();
        final List<String> item = new ArrayList<String>();

        //version sdk 23(android 6.1) para arriba hay que conceder permisos desde codigo adicionalmente al androidmanifest
        final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 123;
        String state = Environment.getExternalStorageState();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }



        File f = new File(ruta);
        File[] files = f.listFiles();

        //recibe en array los archivos del path SSD
        ArrayList<String>ssd = encontrarSongsSD("/storage/sdcard1/");// here you will get all the files path which contains .mp3 at the end.



        for (int i = 0; i < files.length; i++)
        {
            File archivos = files[i];
            if (archivos.isDirectory()) {
                //no agregamos ningun directorio a la lista
            }else if(archivos.getName().endsWith(".mp3")){
                //metodo alterno agregar files mp3 https://stackoverflow.com/questions/39461954/list-all-mp3-files-in-android
                item.add(archivos.getName());
                //agrega los archivos del SSD
            }else if(ssd!=null){
                item.add(archivos.getName());
                //do the remaining stuff
            }


        }


        final ListView lstMusicas = findViewById(R.id.lstMusicas);
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        lstMusicas.setAdapter(fileList);



        //Punt23
        lstMusicas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                int itemPosition     = position;
                String  itemValue    = (String) lstMusicas.getItemAtPosition(position);
                Intent enviarMusica = new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                enviarMusica.putExtra("musica", ruta + itemValue);
                enviarMusica.putExtra("nombre", itemValue);
                startActivity(enviarMusica);
                finish();
            }
        });

    }

    //METODO ACEDER A MEMORIA SD
    ArrayList<String> encontrarSongsSD(String rootPath) {
        ArrayList<String> fileList = new ArrayList<>();
        try{
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (encontrarSongsSD(file.getAbsolutePath()) != null) {
                        fileList.addAll(encontrarSongsSD(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    fileList.add(file.getAbsolutePath());
                }
            }
            return fileList;
        }catch(Exception e){
            return null;
        }
    }



}
