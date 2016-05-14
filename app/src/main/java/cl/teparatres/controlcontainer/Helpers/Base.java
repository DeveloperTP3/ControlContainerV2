package cl.teparatres.controlcontainer.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by francisco on 10/10/2015.
 */
public class Base {

    public Base(){

    }

    public static boolean checkConnectivity(Context ctx)
    {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable()))
        {
            enabled = false;
        }
        return enabled;
    }

    public static boolean validarContenedor(String codigo){
        return true;
    }

}


