package cl.teparatres.controlcontainer.Helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import cl.teparatres.controlcontainer.R;

/**
 * Created by francisco on 28/10/2015.
 */
public class AdapterDatos extends BaseAdapter {

    protected Activity activity;
    public ArrayList<Datos> items;
    
    public AdapterDatos(Activity activity, ArrayList<Datos> items){
        this.activity =  activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        //obtener el tamaño del array
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        //obtenemos la posicion de los items.
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //generamos una convertView por motivos de eficiencia.
        View v = convertView;

        //Asociamos el layout de la lista que hemos creado.
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.itemlista, null);
        }
        //Creamos una clase de la base datos.
        Datos datos = items.get(position);
        //final Datos dir = items.get(position);

        //rellenamos estadocontainer imagen.
        ImageView imgEstado = (ImageView) v.findViewById(R.id.imageViewEstado);
        imgEstado.setImageDrawable(datos.getEstadoContenedor());

        //rellenamos estadocontainer imagen.
        TextView codigo = (TextView) v.findViewById(R.id.lblContenedor);
        codigo.setText(datos.getNumContenedor());
        TextView numeroFile = (TextView) v.findViewById(R.id.lblNumeroFile);
        numeroFile.setText(datos.getNumFile());
        if (datos.getIncidentado()){
            codigo.setTextColor(Color.RED);
            numeroFile.setTextColor(Color.RED);
        }else{
            codigo.setTextColor(Color.BLACK);
            numeroFile.setTextColor(Color.BLACK);
        }
        return v;
    }

    public void cambiarColorIncidentado(int position){
        this.getView(position, null, null);

    }

    public String obtenerNombreProceso(int proceso){
        String nombre = "";
        switch(proceso){
            case 1:
                nombre = "EN BODEGA";
                break;
            case 2:
                nombre = "ARMADO";
                break;
            case 3:
                nombre = "EN PLANTA";
                break;
            case 4:
                nombre = "CARGANDO";
                break;
            case 5:
                nombre = "CARGADO";
                break;
            case 6:
                nombre = "PESAJE";
                break;
            case 7:
                nombre = "DESPACHADO";
                break;
        }
        return nombre;
    }
}
