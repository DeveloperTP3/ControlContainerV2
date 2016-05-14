package cl.teparatres.controlcontainer.Models;

/**
 * Created by francisco on 10/10/2015.
 */
public class Supervisor {
    private int id;
    private String email;
    private String nombre;

    public Supervisor(int id, String email, String nombre){
        this.id = id;
        this.email = email;
        this.nombre = nombre;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
