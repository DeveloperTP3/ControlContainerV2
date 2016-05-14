package cl.teparatres.controlcontainer.Helpers;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Created by francisco on 28/10/2015.
 */
public class Datos {

    protected Drawable estadoContenedor;
    protected String numContenedor;
    protected String numFile;
    protected long id;
    protected long estadoProceso;
    protected Boolean incidentado;

    public Boolean getIncidentado() {
        return incidentado;
    }

    public void setIncidentado(Boolean incidentado) {
        this.incidentado = incidentado;
    }

    public Datos(long id, long estadoProceso, Drawable imgEstado, String numCont, String numFile, Boolean incidentado){
        this.estadoContenedor = imgEstado;
        this.numContenedor = numCont;
        this.numFile = numFile;
        this.id = id;
        this.estadoProceso = estadoProceso;
        this.incidentado = incidentado;
    }

    public Drawable getEstadoContenedor() {
        return estadoContenedor;
    }

    public void setEstadoContenedor(Drawable estadoContenedor) {
        this.estadoContenedor = estadoContenedor;
    }

    public String getNumContenedor() {
        return numContenedor;
    }

    public void setNumContenedor(String numContenedor) {
        this.numContenedor = numContenedor;
    }

    public String getNumFile() {
        return numFile;
    }

    public void setNumFile(String numFile) {
        this.numFile = numFile;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEstadoProceso() {
        return estadoProceso;
    }

    public void setEstadoProceso(long estadoProceso) {
        this.estadoProceso = estadoProceso;
    }

}
