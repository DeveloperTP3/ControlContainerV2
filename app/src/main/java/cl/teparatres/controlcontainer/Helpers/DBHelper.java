package cl.teparatres.controlcontainer.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

/**
 * Created by devel_pancho on 29/11/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ContenedorDB";
    private static final int DB_VERSION = 1;
    private static CursorFactory factory = null;
    private Context contexto = null;

    //Sentencia SQL para crear la tabla de Usuarios
    private String CREATE_TABLE_SUPERVISOR = "CREATE TABLE supervisores (_id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, pass TEXT, nombre TEXT)";
    private String CREATE_TABLE_CONTENEDOR = "CREATE TABLE contenedores (_id INTEGER PRIMARY KEY AUTOINCREMENT, numero TEXT)";
    private String CREATE_TABLE_PROCESO = "CREATE TABLE procesos (_id INTEGER PRIMARY KEY AUTOINCREMENT, contenedorID INTEGER, supervisorID INTEGER, estado INTEGER, fecha_creacion DATETIME)";
    private String CREATE_TABLE_SESION = "CREATE TABLE sesiones (_id INTEGER PRIMARY KEY AUTOINCREMENT, supervisorID INTEGER, fecha_inicio DATETIME, fecha_fin DATETIME)";
    private String CREATE_TABLE_INCIDENTE = "CREATE TABLE incidentes (_id INTEGER PRIMARY KEY AUTOINCREMENT, supervisorID INTEGER, contenedorID INTEGER, procesoID INTEGER, motivo TEXT, fecha_creacion DATETIME)";

    //Sentencia SQL para insertar usuarios con rol "supervisor", ya que el sistema funcionará con modo offline.
    private String sqlInsertSupervisor = "INSERT INTO supervisores (email, pass, nombre) VALUES ('super@hillebrand.cl', '1234', 'Roberto')";

    public DBHelper(Context context) {
        super(context, DB_NAME, factory, DB_VERSION);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(db.isReadOnly())
            db = this.getWritableDatabase();

        db.execSQL(CREATE_TABLE_SUPERVISOR);
        db.execSQL(CREATE_TABLE_CONTENEDOR);
        db.execSQL(CREATE_TABLE_PROCESO);
        db.execSQL(CREATE_TABLE_SESION);
        db.execSQL(CREATE_TABLE_INCIDENTE);

        //Insertamos usuarios por defecto que usaran la app en modo offline.
        db.execSQL(sqlInsertSupervisor);
        Toast.makeText(contexto, "Configuración finalizada", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(contexto, "Configuración actualizada", Toast.LENGTH_LONG).show();
    }
}
