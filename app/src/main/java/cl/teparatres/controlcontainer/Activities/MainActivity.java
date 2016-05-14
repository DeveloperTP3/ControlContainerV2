package cl.teparatres.controlcontainer.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
import cl.teparatres.controlcontainer.Helpers.Base;
import cl.teparatres.controlcontainer.Helpers.DBHelper;
import cl.teparatres.controlcontainer.Helpers.DataBaseManager;
import cl.teparatres.controlcontainer.R;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    EditText txtUser;
    EditText txtPass;
    TextView btnModoOffline;
    DataBaseManager dbm;
    public static final String LINK_LOGIN = "http://bulk.teparatres.cl/services/services.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        dbm = new DataBaseManager(this);
        txtUser = (EditText)findViewById(R.id.txtUsuario);
        txtPass = (EditText)findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnModoOffline = (TextView) findViewById(R.id.lblModoOffline);

        //creamos base de datos inicial
        DBHelper dbHelper = new DBHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Acceso al sistema si disponemos de internet.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //comprobación de existencia de conexion a internet
                if (Base.checkConnectivity(MainActivity.this)) {
                    new Conexion().execute("1", txtUser.getText().toString(), txtPass.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Revisa tu conexión a internet y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Ingresamos en modo offline, aqui realizamos la comprobación de usuario en base de datos local.
        /*btnModoOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Supervisor supervisor = null;
                supervisor = dbm.loginSupervisor(txtUser.getText().toString(), txtPass.getText().toString());

                if(supervisor != null){
                    int idSesion = dbm.insertarInicioSesionDB(supervisor.getId());
                    Intent irLogin = new Intent(MainActivity.this, ContenedorActivity.class);
                    irLogin.putExtra("usuario", txtUser.getText().toString());
                    irLogin.putExtra("idUsuario", supervisor.getId());
                    irLogin.putExtra("nombreUsuario", supervisor.getNombre());
                    irLogin.putExtra("idSesion", idSesion);
                    irLogin.putExtra("modo", "Modo Offline");
                    startActivity(irLogin);
                }else{
                    Toast.makeText(getApplicationContext(), "Error con el usuario o password", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private class Conexion extends AsyncTask<String, String, String> {
        ProgressDialog dialogo = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialogo = ProgressDialog.show(MainActivity.this, "Procesando", "Por favor espere...", true);
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
                URL url = new URL(MainActivity.LINK_LOGIN + "?acc=" + params[0] + "&email=" + params[1] + "&pass=" + params[2]);
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
                        JSONObject json;
                        try {
                            if(!resultado.equals("FALSE")){
                                dialogo.dismiss();

                                json = new JSONObject(resultado);
                                String idUsuario = json.getString("id");
                                String nombreUsuario = json.getString("nombre");
                                String idSesion = json.getString("id_sesion");

                                Intent irLogin = new Intent(MainActivity.this, ContenedorActivity.class);
                                irLogin.putExtra("usuario", txtUser.getText().toString());
                                irLogin.putExtra("idUsuario", idUsuario);
                                irLogin.putExtra("nombreUsuario", nombreUsuario);
                                irLogin.putExtra("idSesion", idSesion);
                                irLogin.putExtra("modo", "Modo Online");
                                startActivity(irLogin);
                            }else{
                                Toast.makeText(getApplicationContext(), "Error con el usuario o password", Toast.LENGTH_SHORT).show();
                                dialogo.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
             1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(MainActivity.this, "He presionado el boton con id " + R.id.action_logout, Toast.LENGTH_SHORT).show();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}