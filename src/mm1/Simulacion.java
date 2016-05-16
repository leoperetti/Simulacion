package mm1;

import java.util.Random;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class Simulacion {

	private static float ti_os,lambda,mu,reloj,dAq,dAb,dD,TUE,finSimul;
	private static int cli_at,n,pos;
	private static int estado,e;
	private static ArrayList<Integer> cliAtend;
	private static ArrayList<Float> vectdD;
	private static ArrayList<Integer> cliColaEnt;
	private static ArrayList<Float> tiempoCliCola;
	private static ArrayList<Float> tiemposdAq, vectdAq, tiemposdAb, vectdAb;
	private static ArrayList<String> arregloTipo;
	private static ArrayList<Float> arregloTiempo,VTA;
	
	public static void main(String[] args) throws IOException{
		int posEv;
		lambda=(float) 0.5;
		mu=1;
		finSimul=100;
		inicializar();
		while (reloj<finSimul){
			tiempoCliCola.add(reloj);
			cliColaEnt.add(n);
			posEv=buscaEvento();
			if (arregloTipo.get(posEv)=="A"){
				nuevoArribo();
			}
			else{
				nuevaPartida();
			}	
		}
		reportar();
	}

	private static void nuevaPartida() {
		float tiemServ;
		if (n==0){
			estado=0;

		}
		else{
			
			dAq=dAq+n*(reloj-TUE);
			tiemposdAq.add(reloj);
			vectdAq.add(dAq/reloj);
			
			dAb=dAb+1*(reloj-ti_os);
			tiemposdAb.add(reloj);
			vectdAb.add(dAb/reloj);
			
			n=n-1;
			
			dD=dD+(reloj-VTA.get(e));
			vectdD.add(dD);
			cliAtend.add(cli_at);
			
			e=e+1;
			cli_at=cli_at+1;
			tiemServ= expon(mu);
			arregloTipo.add("P");
			arregloTiempo.add(reloj+tiemServ);
		}
		ti_os=reloj;
		TUE=reloj;
	}


	private static void nuevoArribo() {
		float tiemServ;
		float tiemArribo;
		if (estado==1){
			
			
			dAq=dAq+n*(reloj-TUE);
			tiemposdAq.add(reloj);
			vectdAq.add(dAq/reloj);
			
			n=n+1;
			VTA.add(reloj);
			
		}
		else{
			
			estado=1;
			ti_os=reloj;
			vectdD.add(dD);
			cliAtend.add(cli_at);
			cli_at=cli_at+1;
			tiemServ= expon(mu);
			arregloTipo.add("P");
			arregloTiempo.add(reloj+tiemServ);
		}
		tiemArribo= expon(lambda);
		arregloTipo.add("A");
		arregloTiempo.add(reloj+tiemArribo);
		TUE=reloj;
		
	}

	private static void reportar() throws IOException {
		String ruta = "archivo.txt";
        File archivo = new File(ruta);
        BufferedWriter bw;
        arregloTiempo.remove(1);
        arregloTipo.remove(1);
        bw = new BufferedWriter(new FileWriter(archivo));
        ordenar();
        /*/
        bw.write("Longitud promedio de la cola: "+promTpoCola+" personas, ");
        bw.newLine();
        bw.write("Utilizacion del servidor: "+utilServ*100+"%,");
        bw.newLine();
        bw.write("Demora Promedio: "+demProm);
        bw.newLine();
        bw.write("Clientes atendidos: "+cli_at);
        bw.newLine();
        bw.newLine();
        /*/
        bw.write("Lamda= "+lambda);
        bw.newLine();
        bw.write("Mu= "+mu);
        bw.newLine();
        bw.write("Tiempo de simulacion: "+finSimul);
        bw.newLine();
        bw.newLine();
        bw.write("Tiempo \t cola promedio");
        bw.newLine();
        for(int i=0;i<vectdAq.size();i++){
        	bw.write(Float.toString(tiemposdAq.get(i)));
        	bw.write("\t");
        	bw.write(Float.toString(vectdAq.get(i)));
        	bw.newLine();
        }
        bw.newLine();
        bw.newLine();
        bw.write("Tiempos \t utilidad"); 
        bw.newLine();
        for(int i=0;i<vectdAb.size();i++){
        	bw.write(Float.toString(tiemposdAb.get(i)));
        	bw.write("\t");
        	bw.write(Float.toString(vectdAb.get(i)));
        	bw.newLine();
        }
        bw.newLine();
        bw.newLine();
        
        bw.write("Clientes at \t demora prom");
        bw.newLine();
        for(int i=0;i<vectdD.size();i++){
        	bw.write(Integer.toString(cliAtend.get(i)));
        	bw.write("\t");
        	bw.write(Float.toString(vectdD.get(i)));
        	bw.newLine();
        }
        bw.newLine();
        bw.newLine();
        
        bw.write("Tiempos \t cola");
        bw.newLine();
        for(int i=0;i<tiempoCliCola.size();i++){
        	bw.write(Float.toString(tiempoCliCola.get(i)));
        	bw.write("\t");
        	bw.write(Integer.toString(cliColaEnt.get(i)));
        	bw.newLine();
        }
        bw.close();
        
	}

	private static void ordenar() {
		float aux;
		String aux2;
		for(int i=0;i<arregloTipo.size()-1;i++){
			for(int j=i+1;j<arregloTipo.size();j++){
				if(arregloTiempo.get(i)>arregloTiempo.get(j)){
					aux=arregloTiempo.get(i);
					arregloTiempo.set(i, arregloTiempo.get(j));
					arregloTiempo.set(j,aux);
					aux2=arregloTipo.get(i);
					arregloTipo.set(i, arregloTipo.get(j));
					arregloTipo.set(j,aux2);
				}
			}
		}
		
	}

	private static int buscaEvento() {
		float min=(float) 1.0E30;
		for (int i=0;i<arregloTiempo.size();i++){
			if (arregloTiempo.get(i)<min){
				if (arregloTiempo.get(i)>reloj){
					pos=i;
					min=arregloTiempo.get(i);
				}
			}
		}	
		reloj=arregloTiempo.get(pos);
		return pos;
	}

	private static void inicializar() {
		estado=0;
		tiemposdAq= new ArrayList<Float>();
		vectdAq= new ArrayList<Float>();
		tiemposdAb= new ArrayList<Float>();
		vectdAb= new ArrayList<Float>();
		tiempoCliCola= new ArrayList<Float>();
		cliColaEnt=new ArrayList<Integer>();
		cliAtend= new ArrayList<Integer>();
		vectdD=new ArrayList<Float>();
		reloj=0;
		pos=0;
		cli_at=0;
		n=0;
		e=0;
		dAq=0;
		dAb=0;
		dD=0;
		VTA= new ArrayList<Float>(0);
		arregloTipo= new ArrayList<String>();
		arregloTiempo= new ArrayList<Float>();
		arregloTipo.add("A");
		arregloTiempo.add(reloj + expon(lambda));
		arregloTipo.add("P");
	    arregloTiempo.add((float) 1.0E30);
		
	}
	
	static float expon(float l)
	{
		float t;
		Random rand=new Random();
	    float r;
	    r=rand.nextFloat();
	    t=(float)((Math.log(r/l)/(-l)));
	    return t;
	}
}
