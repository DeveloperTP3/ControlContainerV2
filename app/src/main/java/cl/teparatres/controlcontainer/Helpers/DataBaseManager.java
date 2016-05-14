package cl.teparatres.controlcontainer.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cl.teparatres.controlcontainer.Activities.ContenedorActivity;
import cl.teparatres.controlcontainer.Activities.MainActivity;
import cl.teparatres.controlcontainer.Models.Supervisor;

/**
 * Created by Francisco on 15-12-2015.
 */
public class DataBaseManager {

    private DBHelper dbHelper;
    private static String TABLE_SESION = "sesiones";
    private static String TABLE_CONTENEDOR = "contenedores";
    private static String TABLE_SUPERVISOR = "supervisores";
    private static String TABLE_PROCESO = "procesos";
    private static String fecha_actual = "";
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DataBaseManager(Context contexto){
        dbHelper = new DBHelper(contexto);
    }

    public Supervisor loginSupervisor(String email, String pass){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Supervisor supervisor = null;
        String[] valores = {"_id", "email", "nombre"};
        Cursor cursor = db.query(TABLE_SUPERVISOR, valores, "email = '" + email + "' and pass = '" + pass + "'", null, null, null, null, null);

        if(cursor.moveToFirst()) {
            try{
                supervisor = new Supervisor(cursor.getInt(0), cursor.getString(1) + "", cursor.getString(2) + "");
                cursor.close();
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }

        db.close();
        return supervisor;
    }

    public Map insertarContenedorDB(String numero, int id_supervisor){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Map infoContenedor = null;
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("numero", numero);
            int id_cont_insertado = (int) db.insert(TABLE_CONTENEDOR, null, valores);
            if (id_cont_insertado <= 0){
                infoContenedor = new HashMap();
                insertarProcesoDB(id_cont_insertado, 3, id_supervisor);
                infoContenedor.put("idcontenedor", id_cont_insertado);
                infoContenedor.put("proceso", 3);
            }
            db.close();
        }
        return infoContenedor;
    }

    public int insertarProcesoDB(int id_cont, int proceso, int id_supervisor){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String fecha_actual = df.format(c.getTime());
        int idproceso = 0;
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("supervisorID", id_supervisor);
            valores.put("contenedorID", id_cont);
            valores.put("estado", proceso);
            valores.put("fecha_inicio", fecha_actual);
            idproceso = (int) db.insert(TABLE_PROCESO, null, valores);
            db.close();
        }
        return idproceso;
    }

    public Map obtenerInfoContenedor(String numero){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map infoContenedor = null;
        String[] valores = {"_id", "email", "nombre"};
        Cursor cursor = db.query(TABLE_CONTENEDOR, valores, "numero = " + numero, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            try{
                infoContenedor = new HashMap();
                infoContenedor.put("idcontenedor", cursor.getInt(0));
                infoContenedor.put("proceso", 3);
                cursor.close();
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
        db.close();
        return infoContenedor;
    }

    public int obtenerProcesoContenedor(int contenedor){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] valores = {"estado"};
        Cursor cursor = db.query(TABLE_PROCESO, valores, "contenedorID = '" + contenedor, null, null, null, "_id DESC","1");
        if(cursor.moveToFirst()) {
            try{
                cursor.close();
                return cursor.getInt(0);
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
        db.close();
        return 0;
    }

    public boolean existeContenedor(String numContenedor){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] valores = {"_id", "numero"};
        Cursor cursor = db.query(TABLE_CONTENEDOR, valores, "numero = '" + numContenedor, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            try{
                cursor.close();
                return true;
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
        db.close();
        return false;
    }

    public int insertarInicioSesionDB(int supervisor){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String fecha_actual = df.format(c.getTime());
        int sesion = 0;
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("supervisorID", supervisor);
            valores.put("fecha_inicio", fecha_actual);
            sesion = (int) db.insert(TABLE_SESION, null, valores);
            db.close();
        }
        return sesion;
    }

    public void actualizaSesionDB(int supervisor, int sesion){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String fecha_actual = df.format(c.getTime());

        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("fecha_fin", fecha_actual);
            db.update(TABLE_SESION, valores, "_id = " + sesion, null);
            db.close();
        }
    }


}
