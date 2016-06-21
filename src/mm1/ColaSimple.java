package mm1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class ColaSimple {
	//Inicio el VTA (vector tiempo de arribos)
	static ArrayList<Double> vector_tiempos_arribos = new ArrayList<Double>();
	static int e=0;
	//Defino constante el tiempo de servicio y el tiempo en el que se produce un arribo
	
	//el factor de utilizacion es lambda/mu, debe ser mayor a 1
	private static final double TIEMPO_ENTRE_ARRIBOS =2.8; //frecuencia
	private final static double TIEMPO_SERVICIO = 3; //tiempo
	
	//Analisis de Resultados
	private static final int KELEMENTOS=1;
	static ArrayList<Double> array_tiempo_kElemento = new ArrayList<Double>();
	static ArrayList<Double> array_kElementos = new ArrayList<Double>();
	static Double acumKElementos = 0.0;
	
	 /* inicializamos el reloj de la simulacion*/
	static double reloj = 0.0;

    /*iniciamos las variables de estado */
    static boolean servidor_ocupado = false;//false: disponible, true: ocupado
    static int nro_clientes_cola = 0;
    static int nro_clientes_atendidos = 0;
    static double tiempo_ultimo_evento = 0.0; //TUE

    /*iniciamos los contadores estadisticos */
    static double demoras_acumuladas = 0.0; // Ad
    static ArrayList<Double> array_dd = new ArrayList<Double>();
    static ArrayList<Integer> array_cli_at = new ArrayList<Integer>();
    static double area_cli_cola = 0.0; // Aq
    static ArrayList<Double> array_dq = new ArrayList<Double>();
    static ArrayList<Double> array_tiempo_dq = new ArrayList<Double>();
    static double area_uso_servidor = 0.0; // Ab
    static ArrayList<Double> array_db = new ArrayList<Double>();
    static ArrayList<Double> array_tiempo_db = new ArrayList<Double>();

    static double tiempo_servidor_comienza_ocupado = 0.0;
    /* iniciamos la lista de eventos */
    static ArrayList<Evento> lista_eventos = new ArrayList<Evento>();
  


	public static void main(String[] args) {
		
		double tiempo_arribo = reloj + exp(TIEMPO_ENTRE_ARRIBOS);
		//Genero el primer evento que será un arribo y lo registro en el LEV
		Evento primerEvento = new Evento("Arribo", tiempo_arribo);
		tiempo_servidor_comienza_ocupado=tiempo_arribo;
		lista_eventos.add(primerEvento);
		vector_tiempos_arribos.add(tiempo_arribo);
		//Genero la partida del arribo anterior, el tiempo de esta partida es infinito.
		Evento segundoEvento = new Evento("Partida", Math.pow(2, 32));
		lista_eventos.add(segundoEvento);
		
		while(reloj < 10000)
		{
			String tipo_evento = tiempos();
			if(tipo_evento.equals("Arribo"))
			{	
				arribo();
			}
			else if(tipo_evento.equals("Partida"))
			{
				partida();
			}
		}
		reporte();

	}

	private static void reporte() {
		System.out.println("LISTA DE EVENTOS(LEV): CANTIDAD DE EVENTOS: "+lista_eventos.size());
		int arribos=0;
		for(Evento lista: lista_eventos)
		{
			System.out.println("-----------------");
			System.out.println(lista.getTipo_evento()+" tiempo: "+new DecimalFormat("#.####").format(lista.getTiempo()));
			if(lista.getTipo_evento().equals("Arribo") && lista.getTiempo()<=reloj) arribos++;
		}
		System.out.println("-------------------------------------------");
		/*System.out.println("Vector Tiempos Arribos(VTA):");
		for(int i=0;i<vector_tiempos_arribos.size();i++)
		{
			System.out.println(vector_tiempos_arribos.get(i));
		}*/
		System.out.println("Cantidad de arribos:"+ arribos + " \n\n Tiempo de servicio: "+TIEMPO_SERVICIO + " personas/segundo \n Tiempo entre arribos: "+TIEMPO_ENTRE_ARRIBOS+" personas/segundos \n Factor de Utilizacion: "+TIEMPO_ENTRE_ARRIBOS/TIEMPO_SERVICIO+"\n\n");
		System.out.println(" Demora Promedio: "+new DecimalFormat("#.####").format(demoras_acumuladas/nro_clientes_atendidos)+" segundos \n "+"uso promedio del servidor: "+new DecimalFormat("#.####").format(area_uso_servidor/reloj)+" \n"+" Tamaño promedio de cola: "+ new DecimalFormat("#.####").format(area_cli_cola/reloj)+" personas");
		System.out.println("\n Probabilidad de que haya "+KELEMENTOS+" elemento/s en el sistema: "+array_kElementos.get(array_kElementos.size()-1));
		System.out.println("\n\n Clientes en cola: "+nro_clientes_cola+ "\n Clientes Atendidos: "+nro_clientes_atendidos);
		//System.out.println("tamaño db: "+ array_db.size() +" \n tamño dq: "+array_dq.size());
		
		try {
			String ubicacion = "C:\\Users\\nicolas\\desktop\\Simulacion\\";
	        BufferedWriter out = new BufferedWriter(new FileWriter(ubicacion+"demoras_acumuladas.txt"));
	            for (int i=0;i<array_dd.size();i++)
	            {
	            		out.write(array_cli_at.get(i)+" \t"+new DecimalFormat("#.####").format(array_dd.get(i))+" \n");
	            }
	            out.close();
	            out = new BufferedWriter(new FileWriter(ubicacion+"area_uso_servidor.txt"));
	            for (int i=0;i<array_db.size();i++)
	            {
	            		out.write(new DecimalFormat("#.####").format(array_db.get(i))+ " \t"+new DecimalFormat("#.####").format(array_tiempo_db.get(i))+" \n");
	            }
	            out.close();
	            out = new BufferedWriter(new FileWriter(ubicacion+"area_cli_cola.txt"));
	            for (int i=0;i<array_dq.size();i++)
	            {
	            		out.write(new DecimalFormat("#.####").format(array_dq.get(i))+ " \t"+new DecimalFormat("#.####").format(array_tiempo_dq.get(i))+" \n");
	            }
	            out.close();
	            out = new BufferedWriter(new FileWriter(ubicacion+"prob_kElementos.txt"));
	            for (int i=0;i<array_kElementos.size();i++)
	            {
	            		out.write(new DecimalFormat("#.####").format(array_kElementos.get(i))+ " \t"+new DecimalFormat("#.####").format(array_tiempo_kElemento.get(i))+" \n");
	            }
	            out.close();
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}

	private static String tiempos()
	{
		double menorTiempo = Math.pow(2, 32);
		String tipo_evento = "Evento";
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
		
		if(servidor_ocupado)
		{
			
			area_cli_cola =  area_cli_cola + (nro_clientes_cola * (reloj - tiempo_ultimo_evento));//tiempo - tiempo_ultimo_evento --> tiempo transcurrido desde el último evento hasta ahora
			array_dq.add(area_cli_cola/reloj);
			if(nro_clientes_cola == KELEMENTOS)
			{
				acumKElementos = acumKElementos + (reloj - tiempo_ultimo_evento);
				array_kElementos.add(acumKElementos/reloj);
				array_tiempo_kElemento.add(reloj);
			}
			array_tiempo_dq.add(reloj);
			nro_clientes_cola ++;
		}
		else
		{
			servidor_ocupado = true;
			tiempo_servidor_comienza_ocupado = reloj;
			double nuevo_tiempo_servicio = exp(TIEMPO_SERVICIO);
			lista_eventos.add(new Evento("Partida", (reloj + nuevo_tiempo_servicio)));
		}
		vector_tiempos_arribos.add(reloj);
		double proximo_arribo = exp(TIEMPO_ENTRE_ARRIBOS);
		lista_eventos.add(new Evento("Arribo", (reloj + proximo_arribo)));
		tiempo_ultimo_evento = reloj;
		
	}
	
	private static void partida() {
		if(nro_clientes_cola==0)
		{
			servidor_ocupado = false; // servidor libre
			//lista_eventos.add(new Evento("Partida", exp(Math.pow(2, 32))));
		}
		else
		{
			
			area_cli_cola = area_cli_cola+ (nro_clientes_cola * (reloj - tiempo_ultimo_evento));
			array_dq.add(area_cli_cola/reloj);
			if(nro_clientes_cola == KELEMENTOS)
			{	
				acumKElementos = acumKElementos + (reloj - tiempo_ultimo_evento);
				array_kElementos.add(acumKElementos/reloj);
				array_tiempo_kElemento.add(reloj);
			}
			array_tiempo_dq.add(reloj);
			area_uso_servidor = area_uso_servidor + (reloj - tiempo_servidor_comienza_ocupado);
			array_db.add(area_uso_servidor/reloj);
			nro_clientes_cola --;
			array_tiempo_db.add(reloj);
			
			
			double tiempoArribo = (vector_tiempos_arribos.get(nro_clientes_atendidos));
			demoras_acumuladas =  demoras_acumuladas + (reloj - tiempoArribo);
			
			array_dd.add(demoras_acumuladas/nro_clientes_atendidos);
		
			array_cli_at.add(nro_clientes_atendidos);
			
			double nuevo_tiempo_servicio = exp(TIEMPO_SERVICIO);
			lista_eventos.add(new Evento("Partida", (reloj + nuevo_tiempo_servicio)));
			tiempo_servidor_comienza_ocupado=reloj;
		}
		tiempo_ultimo_evento = reloj;
		nro_clientes_atendidos ++;
		
		
	}
	
	public static double exp(double coef) {
		double retorno;
		Random rand=new Random();
	    double random;
	    random=rand.nextDouble();
	    retorno=Math.log(random/coef)/-coef;
	    return retorno;
    }
}