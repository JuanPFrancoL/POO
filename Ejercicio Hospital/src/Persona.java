public abstract class Persona {
    protected String nombre;
    protected String dni;
    protected int edad;
    protected String genero;

    public Persona(String nombre, String dni, int edad, String genero) {
        this.nombre = nombre;
        this.dni = dni;
        this.edad = edad;
        this.genero = genero;
    }

    public Persona(String nombre, String dni) {
        this.nombre = nombre;
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public abstract void presentarse();
}
