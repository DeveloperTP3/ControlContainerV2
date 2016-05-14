package cl.teparatres.controlcontainer.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import cl.teparatres.controlcontainer.Helpers.AdapterDatos;
import cl.teparatres.controlcontainer.Helpers.DataBaseManager;
import cl.teparatres.controlcontainer.Helpers.Datos;
import cl.teparatres.controlcontainer.R;

public class ContenedorActivity extends AppCompatActivity {

    Button btnBuscarContenedor;
    public EditText txtContainer;
    public int posicionItemActivo = 0;
    String modoIngreso;
    String nombreUsuario;
    private static String idUsuario;
    private static String idSesion;

    TextView lblModo;
    Datos datos;
    ListView lista;
    public final String LINK_LOGIN = "http://bulk.teparatres.cl/services/services.php";
    public final ArrayList<Datos> arraydatos = new ArrayList<Datos>();
    final AdapterDatos adapter = new AdapterDatos(this, arraydatos);

    public static  final  int C_CAMBIAR_ESTADO = 551;
    public static final int C_REPORTAR_INCIDENTE = 552;
    public static final int C_DESPACHAR = 553;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lista = (ListView) findViewById(R.id.lv_listado);

        modoIngreso = getIntent().getStringExtra("modo");
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        idUsuario = getIntent().getStringExtra("idUsuario");
        idSesion = getIntent().getStringExtra("idSesion");

        btnBuscarContenedor = (Button) findViewById(R.id.btnBuscar);
        txtContainer = (EditText) findViewById(R.id.txtContainer);

        lblModo = (TextView) findViewById(R.id.lblModoIngreso);
        lblModo.setText("(" + modoIngreso + ")");
        if(modoIngreso.equalsIgnoreCase("Modo Online"))
            lblModo.setTextColor(ContextCompat.getColor(this, R.color.verdeOnline));
        else
            lblModo.setTextColor(ContextCompat.getColor(this, R.color.rojoOffline));

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());

        btnBuscarContenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numCont = txtContainer.getText().toString();
                if (numCont.length() > 0) {
                    if (modoIngreso.equalsIgnoreCase("Modo Online"))
                        new buscador().execute("2", numCont);
                    //else
                    //procesarContenedorIngresado(numCont);
                }
            }
        });
        lista.setAdapter(adapter);
        registerForContextMenu(lista);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ContenedorActivity.this.cerrarSesion(idSesion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            ContenedorActivity.this.cerrarSesion(idSesion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        new android.support.v7.app.AlertDialog.Builder(ContenedorActivity.this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Desea finalizar su sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            ContenedorActivity.this.cerrarSesion(idSesion);
                            ContenedorActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private class buscador extends AsyncTask<String, String, String> {

        ProgressDialog dialogo = new ProgressDialog(ContenedorActivity.this);

        @Override
        protected void onPreExecute() {
            dialogo = ProgressDialog.show(ContenedorActivity.this, "Buscando Contenedor", "Por favor espere...", true);
            dialogo.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is;
            BufferedReader br;
            StringBuilder sb = new StringBuilder();
            String linea;
            String respuesta = "";
            try {
                URL url = new URL(MainActivity.LINK_LOGIN + "?acc=" + params[0] + "&contenedor=" + params[1]);
                URLConnection conex = url.openConnection();
                if (conex instanceof HttpURLConnection) {
                    //castear dentro del httpurlconecction
                    HttpURLConnection httpurl = (HttpURLConnection) conex;
                    int response;
                    //connect
                    httpurl.connect();
                    response = httpurl.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        is = httpurl.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        try {
                            while ((linea = br.readLine()) != null) {
                                sb.append(linea);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        respuesta = sb.toString();

                        if (sb.equals("")) {
                            respuesta = "No hubo resultado";
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return respuesta;
        }

        protected void onProgressUpdate(Integer... values) {
            dialogo.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            final String resultado = result;

            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialogo.dismiss();
                        if(!resultado.isEmpty() && !resultado.equals("null")){
                            JSONObject json;
                            try{
                                json = new JSONObject(resultado);
                                String numeroContenedor = json.getString("codigo");
                                String numeroFile = json.getString("numero_file");
                                long idContenedor = json.getLong("id");
                                short proceso = (short) json.getInt("proceso");

                                int cantidad_retornada = 0;
                                for (int position = 0; position<adapter.getCount(); position++) {
                                    if (adapter.getItemId(position) == idContenedor)
                                        cantidad_retornada++;
                                }
                                if (cantidad_retornada == 0) {
                                    datos = new Datos(idContenedor, proceso, ContextCompat.getDrawable(getApplicationContext(), getImgProceso(proceso)), numeroContenedor, numeroFile, false);
                                    arraydatos.add(datos);
                                    txtContainer.setText("");
                                    adapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(getApplicationContext(), "El contenedor buscado se encuenta en la lista.", Toast.LENGTH_LONG).show();
                                }
                            } catch(JSONException e){
                                Toast.makeText(getApplicationContext(), "error " + e, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Contenedor no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
        }
    }

    public void procesarContenedorIngresado(String numeroContenedor){
        DataBaseManager dbm = new DataBaseManager(this);
        int cantidad_retornada = 0;
        for (int position = 0; position<adapter.getCount(); position++) {
            if (adapter.getItemId(position) == adapter.getItemId(position))
                cantidad_retornada++;
        }
        if(cantidad_retornada > 0){
            Toast.makeText(getApplicationContext(), "El contenedor buscado se encuenta en la lista.", Toast.LENGTH_LONG).show();
        }else{
            if (dbm.existeContenedor(numeroContenedor)){
                Toast.makeText(getApplicationContext(), "El contenedor buscado se encuenta en la lista.", Toast.LENGTH_LONG).show();
            }else{
                Map datosCont = dbm.insertarContenedorDB(numeroContenedor, Integer.parseInt(idUsuario));
                if (datosCont != null) {
                    //TODO: Activar al solucionar el problema con el adaptador actual, lo mas probable es el crear un adaptador nuevo para el modo offline.
                    //datos = new Datos((int)datosCont.get(0), (int)datosCont.get(1), ContextCompat.getDrawable(getApplicationContext(), getImgProceso((int)datosCont.get(1))), numeroContenedor, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.next) );
                    arraydatos.add(datos);
                    txtContainer.setText("");
                    adapter.notifyDataSetChanged();
                } else {

                }
            }
        }
    }

    public void cerrarSesion(String idSesion) throws Exception{
        String linea;
        StringBuilder sb = null;
        URL url = new URL(LINK_LOGIN + "?acc=5&idusuario=" + idUsuario + "&idsesion=" + idSesion);
        HttpURLConnection conexion = (HttpURLConnection)
                url.openConnection();
        if (conexion.getResponseCode()==HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conexion.getInputStream()));
            try {
                while ((linea = reader.readLine()) != null) {
                    //devuelve += linea;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            reader.close();
        }
        conexion.disconnect();
    }

    public int getImgProceso(int proceso){
        int imgProceso = 1;
        switch(proceso){
            case 1:
                imgProceso = R.mipmap.container1;
                break;
            case 2:
                imgProceso = R.mipmap.container2;
                break;
            case 3:
                imgProceso = R.mipmap.container3;
                break;
            case 4:
                imgProceso = R.mipmap.container4;
                break;
            case 5:
                imgProceso = R.mipmap.container5;
                break;
            case 6:
                imgProceso = R.mipmap.container6;
                break;
            case 7:
                imgProceso = R.mipmap.container7;
                break;
        }
        return imgProceso;
    }

    public String getIdUsuario(){
        return idUsuario;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Settings option clicked.
                // TODO: Antes de cerrar debo guardar la salida del usuario

                new AlertDialog.Builder(ContenedorActivity.this)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Desea finalizar su sesión?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    ContenedorActivity.this.cerrarSesion(idSesion);
                                    ContenedorActivity.this.finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        Datos datos = (Datos)adapter.getItem(position);
        String titulo = datos.getNumContenedor();
        posicionItemActivo = position;

        menu.setHeaderTitle(titulo);
        menu.add(Menu.NONE, C_CAMBIAR_ESTADO, Menu.NONE, R.string.opcion_cambiar_estado);
        menu.add(Menu.NONE, C_REPORTAR_INCIDENTE, Menu.NONE, R.string.opcion_reportar_incidente);
        menu.add(Menu.NONE, C_DESPACHAR, Menu.NONE, R.string.opcion_despachar);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final Datos datoItem = (Datos)adapter.getItem(posicionItemActivo);
        String nombreProceso = adapter.obtenerNombreProceso(((int) datoItem.getEstadoProceso() + 1));
        switch(item.getItemId())
        {
            case C_CAMBIAR_ESTADO:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new CambiaEstado().execute("3", datoItem.getEstadoProceso() + "", datoItem.getId() + "", new ContenedorActivity().getIdUsuario(), posicionItemActivo + "");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ContenedorActivity.this);
                builder.setMessage("¿Está seguro de pasar al estado \"" + nombreProceso + "\" el contenedor " + datoItem.getNumContenedor() + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            case C_REPORTAR_INCIDENTE:
                Dialog dialog;
                final String[] listaIncidente = {" Contenedor Inflado ", " Contenedor filtrando ", " Otros "};
                final ArrayList itemsSelected = new ArrayList();

                android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(ContenedorActivity.this);
                builder2.setTitle(" Tipo de Incidente ");
                builder2.setMultiChoiceItems(listaIncidente, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                                if (isSelected) {
                                    itemsSelected.add(selectedItemId);
                                } else if (itemsSelected.contains(selectedItemId)) {
                                    itemsSelected.remove(Integer.valueOf(selectedItemId));
                                }
                            }
                        })
                        .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (itemsSelected.size() == 0) {
                                    Toast.makeText(ContenedorActivity.this, "Debe seleccionar al menos un tipo de incidente. ", Toast.LENGTH_SHORT).show();
                                } else {
                                    String seleccionados = "";
                                    for (int i = 0; i < itemsSelected.size(); i++) {
                                        if (i > 0)
                                            seleccionados += ",";
                                        seleccionados += itemsSelected.get(i).toString();
                                        //concat += listaIncidente[(int) itemsSelected.get(i)];
                                    }
                                    new Incidente().execute("4", datoItem.getEstadoProceso() + "", datoItem.getId() + "", new ContenedorActivity().getIdUsuario(), seleccionados, posicionItemActivo + "");
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                dialog = builder2.create();
                dialog.show();
                return true;
            case C_DESPACHAR:
                if(datoItem.getEstadoProceso() < 5){
                    Toast.makeText(ContenedorActivity.this, "El Contenedor debe estar Cargado", Toast.LENGTH_LONG).show();
                    return true;
                }
                DialogInterface.OnClickListener despacharClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new CambiaEstado().execute("6", datoItem.getEstadoProceso() + "", datoItem.getId() + "", new ContenedorActivity().getIdUsuario(), posicionItemActivo + "");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder3 = new AlertDialog.Builder(ContenedorActivity.this);
                builder3.setMessage("¿Está seguro de despachar el contenedor " + datoItem.getNumContenedor() + "?").setPositiveButton("Yes", despacharClickListener)
                        .setNegativeButton("No", despacharClickListener).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public class CambiaEstado extends AsyncTask<String, String, String> {
        ProgressDialog dialogo = new ProgressDialog(ContenedorActivity.this);

        @Override
        protected void onPreExecute() {
            dialogo = ProgressDialog.show(ContenedorActivity.this, "Procesando", "Por favor espere, estamos cambiando el estado del contenedor", true);
            dialogo.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String linea;
            String respuesta = "";
            try {

                URL url = new URL(LINK_LOGIN + "?acc=" + params[0] + "&procesoactual=" + params[1] + "&idcontenedor=" + params[2] + "&idusuario=" + params[3] + "&posicion=" + params[4]);
                URLConnection conex = url.openConnection();
                if (conex instanceof HttpURLConnection) {
                    //castear dentro del httpurlconecction
                    HttpURLConnection httpurl = (HttpURLConnection) conex;
                    int response = -1;
                    //connect
                    httpurl.connect();
                    response = httpurl.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        is = httpurl.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        try {
                            while ((linea = br.readLine()) != null) {
                                sb.append(linea);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        respuesta = sb.toString();

                        if (sb.equals("")) {
                            respuesta = "No hubo resultado";
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return respuesta;
        }

        protected void onProgressUpdate(Integer... values) {
            dialogo.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            final String resultado = result;

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            dialogo.dismiss();
                            if(!resultado.isEmpty() && !resultado.equals("null")){
                                JSONObject json;
                                try{
                                    json = new JSONObject(resultado);
                                    String respuesta = (String) json.getString("respuesta");

                                    if(respuesta.equals("NO EXISTE")){
                                        Toast.makeText(ContenedorActivity.this, "El contenedor ha sido eliminado", Toast.LENGTH_LONG).show();
                                    }else{
                                        int posicion = json.getInt("posicion");
                                        String numeroContenedor = "";
                                        String numeroFile = "";
                                        int idContenedor = 0;
                                        int proceso = 0;

                                        if(!json.getString("codigo").toString().equals(""))
                                            numeroContenedor = json.getString("codigo");
                                        if(!json.getString("numero_file").toString().equals(""))
                                            numeroFile = json.getString("numero_file");
                                        if(json.getInt("id") > 0)
                                            idContenedor = json.getInt("id");
                                        if(json.getInt("proceso") > 0) {
                                            proceso = json.getInt("proceso");
                                        }
                                        Datos info_contenedor = (Datos) adapter.getItem(posicion);
                                        switch(respuesta){
                                            case "OK":
                                                String fecha_cambio = json.getString("fecha_cambio").toString();
                                                Datos datos = new Datos(idContenedor, (proceso + 1), ContextCompat.getDrawable(ContenedorActivity.this, new ContenedorActivity().getImgProceso(proceso + 1)), numeroContenedor, numeroFile, info_contenedor.getIncidentado());
                                                adapter.items.set(posicion, datos);
                                                Toast.makeText(ContenedorActivity.this, "El estado del contenedor ha cambiado correctamente.\nFecha del cambio: " + fecha_cambio, Toast.LENGTH_LONG).show();
                                                break;
                                            case "DISTINTO":
                                                datos = new Datos(idContenedor, (proceso), ContextCompat.getDrawable(ContenedorActivity.this, new ContenedorActivity().getImgProceso(proceso)), numeroContenedor, numeroFile, info_contenedor.getIncidentado());
                                                adapter.items.set(posicion, datos);
                                                Toast.makeText(ContenedorActivity.this, "El estado del contenedor ha cambiado anteriormente, vuelva a realizar la acción", Toast.LENGTH_LONG).show();
                                                break;
                                            case "DESPACHO":
                                                datos = new Datos(idContenedor, (proceso), ContextCompat.getDrawable(ContenedorActivity.this, new ContenedorActivity().getImgProceso(proceso)), numeroContenedor, numeroFile, info_contenedor.getIncidentado());
                                                adapter.items.set(posicion, datos);
                                                Toast.makeText(ContenedorActivity.this, " Este contenedor se encuentra en estado Despachado" , Toast.LENGTH_LONG).show();
                                                break;
                                            case "BODEGA":
                                                datos = new Datos(idContenedor, (proceso), ContextCompat.getDrawable(ContenedorActivity.this, new ContenedorActivity().getImgProceso(proceso)), numeroContenedor, numeroFile, info_contenedor.getIncidentado());
                                                adapter.items.set(posicion, datos);
                                                Toast.makeText(ContenedorActivity.this, "No se Puede realizar esta acción, Ya que el contenedor se encuentra en Bodega" , Toast.LENGTH_LONG).show();
                                                break;
                                            case "NO DISPONIBLE":
                                                new ContenedorActivity().arraydatos.remove(posicion);
                                                Toast.makeText(ContenedorActivity.this, "El contenedor no está disponible para realizar esta acción", Toast.LENGTH_LONG).show();
                                                break;
                                            case "FAIL":
                                                Toast.makeText(ContenedorActivity.this, "Hubo un error al cambiar el estado, compruebe el estado de su conexion a internet", Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                } catch(JSONException e){
                                    Toast.makeText(ContenedorActivity.this, "error " + e, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(ContenedorActivity.this, "Contenedor no encontrado", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 1000);
        }
    }


    public class Incidente extends AsyncTask<String, String, String> {
        ProgressDialog dialogo = new ProgressDialog(ContenedorActivity.this);

        @Override
        protected void onPreExecute() {
            dialogo = ProgressDialog.show(ContenedorActivity.this, "Procesando", "Espere un momento por favor", true);
            dialogo.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String linea;
            String respuesta = "";
            try {

                URL url = new URL(LINK_LOGIN + "?acc=" + params[0] + "&procesoactual=" + params[1] + "&idcontenedor=" + params[2] + "&idusuario=" + params[3] + "&seleccionados=" + params[4] + "&posicion=" + params[5]);
                URLConnection conex = url.openConnection();
                if (conex instanceof HttpURLConnection) {
                    //castear dentro del httpurlconecction
                    HttpURLConnection httpurl = (HttpURLConnection) conex;
                    int response = -1;
                    //connect
                    httpurl.connect();
                    response = httpurl.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        is = httpurl.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        try {
                            while ((linea = br.readLine()) != null) {
                                sb.append(linea);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        respuesta = sb.toString();

                        if (sb.equals("")) {
                            respuesta = "No hubo resultado";
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return respuesta;
        }

        protected void onProgressUpdate(Integer... values) {
            dialogo.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            final String resultado = result;

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            dialogo.dismiss();
                            if(!resultado.isEmpty() && !resultado.equals("null")){
                                JSONObject json;
                                try{
                                    json = new JSONObject(resultado);
                                    String respuesta = (String) json.getString("respuesta");

                                    if(respuesta.equals("NO EXISTE")){
                                        Toast.makeText(ContenedorActivity.this, "El contenedor no existe o no está disponible", Toast.LENGTH_LONG).show();
                                    }else{
                                        int posicion = json.getInt("posicion");
                                        String numeroContenedor = "";
                                        int idContenedor = 0;
                                        String numeroFile = "";
                                        short proceso = 0;

                                        if(!json.getString("codigo").toString().equals(""))
                                            numeroContenedor = json.getString("codigo");
                                        if(json.getInt("id") > 0)
                                            idContenedor = json.getInt("id");
                                        if(json.getInt("proceso") > 0)
                                            proceso = (short) json.getInt("proceso");
                                        if(!json.getString("numero_file").toString().equals(""))
                                            numeroFile = json.getString("numero_file");


                                        switch(respuesta){
                                            case "OK":
                                                Datos datos = new Datos(idContenedor, (proceso), ContextCompat.getDrawable(ContenedorActivity.this, new ContenedorActivity().getImgProceso(proceso)), numeroContenedor, numeroFile, true);
                                                adapter.items.set(posicion, datos);
                                                Toast.makeText(ContenedorActivity.this, "El incidente ha sido reportado correctamente", Toast.LENGTH_LONG).show();
                                                break;
                                            case "FAIL":
                                                Toast.makeText(ContenedorActivity.this, "Hubo un error al reportar el incidente, compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                        adapter.notifyDataSetChanged();
                                    }
                                } catch(JSONException e){
                                    Toast.makeText(ContenedorActivity.this, "error " + e, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(ContenedorActivity.this, "Contenedor no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 1000);
        }
    }





}