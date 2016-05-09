package mm1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ColaSimple {
	//Inicio el VTA (vector tiempo de arribos)
	static ArrayList<Double> vector_tiempos_arribos = new ArrayList<Double>();
	//Defino constante el tiempo de servicio y el tiempo en el que se produce un arribo
	private static final double TIEMPO_ENTRE_ARRIBOS = 1.0;
	private final static double TIEMPO_SERVICIO = 0.5;
	 /* inicializamos el reloj de la simulacion*/
	static double reloj = 0.0;

    /*iniciamos las variables de estado */
    static boolean estado_servidor = false;//false: disponible, true: ocupado
    static int nro_clientes_cola = 0;
    static int nro_clientes_atendidos = 0;
    static double tiempo_ultimo_evento = 0.0; //TUE

    /*iniciamos los contadores estadisticos */
    static double demoras_acumuladas = 0.0; // Ad
    static ArrayList<Double> array_dd = new ArrayList<Double>();
    static double area_cli_cola = 0.0; // Aq
    static ArrayList<Double> array_dq = new ArrayList<Double>();
    static double area_uso_servidor = 0.0; // Ab
    static ArrayList<Double> array_db = new ArrayList<Double>();

    static double tiempo_servidor_comienza_ocupado = 0.0;
    /* iniciamos la lista de eventos */
    static ArrayList<Evento> lista_eventos = new ArrayList<Evento>();
  



	public static void main(String[] args) {
		
		double tiempo_arribo = reloj + exp(TIEMPO_ENTRE_ARRIBOS);
		//Genero el primer evento que será un arribo y lo registro en el LEV
		Evento primerEvento = new Evento("Arribo", tiempo_arribo);
		lista_eventos.add(primerEvento);
		vector_tiempos_arribos.add(tiempo_arribo);
		//Genero la partida del arribo anterior, el tiempo de esta partida es infinito.
		Evento segundoEvento = new Evento("Partida", Math.pow(2, 32));
		lista_eventos.add(segundoEvento);
		
		while(reloj < 30.0)
		{
			String tipo_evento = tiempos();
			if(tipo_evento.equals("Arribo"))
			{
				arribo();
			}
			else
			{
				partida();
			}
		}
		reporte();

	}

	private static void reporte() {
		System.out.println("LISTA DE EVENTOS(LEV): CANTIDAD DE EVENTOS: "+lista_eventos.size());
		for(Evento lista: lista_eventos)
		{
			System.out.println("-----------------");
			System.out.println(lista.getTipo_evento()+" tiempo: "+new DecimalFormat("#.####").format(lista.getTiempo()));
		}
		System.out.println("-------------------------------------------");
		System.out.println("Vector Tiempos Arribos(VTA):");
		for(Double d: vector_tiempos_arribos)
		{
			System.out.println("                    ---------       ");
			System.out.println("                     "+new DecimalFormat("#.####").format(d));
		}
		System.out.println("Cantidad de arribos:"+ vector_tiempos_arribos.size());
		System.out.println(demoras_acumuladas+" "+area_uso_servidor+" "+ area_cli_cola);
		
		try {
			String ubicacion = "C:\\Users\\nicolas\\desktop\\Simulacion\\";
	        BufferedWriter out = new BufferedWriter(new FileWriter(ubicacion+"demoras_acumuladas.txt"));
	            for (Double dd : array_dd)
	            {
	            		out.write(new DecimalFormat("#.####").format(dd)+ " \n");
	            }
	            out.close();
	            out = new BufferedWriter(new FileWriter(ubicacion+"area_uso_servidor.txt"));
	            for (Double db : array_db)
	            {
	            		out.write(new DecimalFormat("#.####").format(db)+ " \n");
	            }
	            out.close();
	            out = new BufferedWriter(new FileWriter(ubicacion+"area_cli_cola.txt"));
	            for (Double db : array_db)
	            {
	            		out.write(new DecimalFormat("#.####").format(db)+ " \n");
	            }
	            out.close();
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}

	private static String tiempos()
	{
		double menorTiempo = Math.pow(2, 32);
		String tipo_evento = null;
		for(Evento d: lista_eventos)
		{
			if(d.getTiempo()<menorTiempo && reloj<d.getTiempo())
			{
				menorTiempo = d.getTiempo();
				tipo_evento = d.getTipo_evento();
			}
		}
		reloj = menorTiempo;
		return tipo_evento;
	}

	private static void arribo() {
		
		if(estado_servidor == true)
		{
			area_cli_cola += (nro_clientes_cola * (reloj - tiempo_ultimo_evento));//tiempo - tiempo_ultimo_evento --> tiempo transcurrido desde el último evento hasta ahora
			array_dq.add(area_cli_cola);
			nro_clientes_cola ++;
			vector_tiempos_arribos.add(reloj);
		}
		else
		{
			estado_servidor = true;
			tiempo_servidor_comienza_ocupado = reloj;
			nro_clientes_atendidos = nro_clientes_atendidos + 1;
			double nuevo_tiempo_servicio = exp(TIEMPO_SERVICIO);
			lista_eventos.add(new Evento("Partida", (reloj + nuevo_tiempo_servicio)));
		}
		double proximo_arribo = exp(TIEMPO_ENTRE_ARRIBOS);
		lista_eventos.add(new Evento("Arribo", (reloj + proximo_arribo)));
		tiempo_ultimo_evento = reloj;
		
	}
	
	private static void partida() {
		if(nro_clientes_cola==0)
		{
			estado_servidor = false;
			lista_eventos.add(new Evento("Partida", exp(Math.pow(2, 32))));
		}
		else
		{
			area_cli_cola += (nro_clientes_cola * (reloj - tiempo_ultimo_evento));
			array_dq.add(area_cli_cola);
			area_uso_servidor += (reloj - tiempo_ultimo_evento);
			array_db.add(area_uso_servidor);
			nro_clientes_cola --;
			int ultimoTiempo = (vector_tiempos_arribos.size()-1);
			demoras_acumuladas =  demoras_acumuladas + (reloj - vector_tiempos_arribos.get(ultimoTiempo));
			array_dd.add(demoras_acumuladas);
			nro_clientes_atendidos ++;
			double nuevo_tiempo_servicio = exp(TIEMPO_SERVICIO);
			lista_eventos.add(new Evento("Partida", (reloj + nuevo_tiempo_servicio)));
		}
		tiempo_ultimo_evento = reloj;
	}
	
	 public static double exp(double media) {
	        return Math.log(1 - Math.random()) / (-(1 / media));
	    }
}