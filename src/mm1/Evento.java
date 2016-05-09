package mm1;

public class Evento {
	private String tipo_evento;
	private double tiempo;
	public Evento()
	{	
	}
	
	public Evento(String tipo, double tiempo)
	{
		setTipo_evento(tipo);
		setTiempo(tiempo);
	}
	
	public String getTipo_evento() {
		return tipo_evento;
	}
	public void setTipo_evento(String tipo_evento) {
		this.tipo_evento = tipo_evento;
	}
	public double getTiempo() {
		return tiempo;
	}
	public void setTiempo(double tiempo) {
		this.tiempo = tiempo;
	}
	
}
