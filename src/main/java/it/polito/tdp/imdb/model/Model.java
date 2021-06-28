package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private ImdbDAO dao;
	private Graph<Actor,DefaultWeightedEdge> grafo;
	private Map<Integer,Actor> idMap;
	
	// Modello del mondo
	
	private List<Actor> attoriRimanenti;
	private boolean pausa;
	private String lastGender;
	private boolean doubleGender;
		
	// Parametri di input
		
	private int n;  

	// Parametri di output
	
	private int totPause;
	private List<Actor> attoriIntervistati;

	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	
	public void creaGrafo(String genre) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		// aggiungo i vertici
		this.dao.getVertici(idMap, genre);
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		// aggiungo gli archi
		
		for(Arco a : dao.getArchi(genre, idMap)) {
			Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
	}
	
	
	public List<Actor> attoriSimili(Actor primo){
		
		BreadthFirstIterator<Actor,DefaultWeightedEdge> bfv = new BreadthFirstIterator<>(this.grafo,primo);
		List<Actor> simili = new ArrayList<>();
		
		while(bfv.hasNext()) {
			Actor a = bfv.next();
			
			if(!a.equals(primo)) {
				simili.add(a);
			}
		}
		
		Collections.sort(simili);
		return simili;
	}
	
	public void simula(int n){
		this.init(n);
		this.run();
	}
	
	
	// Inizializza il simulatore e crea gli eventi iniziali
	private void init(int n) {
		
		// inizializza modello del mondo
		this.n=n;
		this.attoriRimanenti = new ArrayList<>(grafo.vertexSet());
		this.pausa=true;
		this.doubleGender=false;
		this.lastGender="";
		
		
		// inizializza i parametri di output
		
		this.totPause=0;
		this.attoriIntervistati = new LinkedList<>();
		
	}
	
	private void run() {
		
		for(int i=1;i<=n;i++) {
			
			if(this.doubleGender) { // due gg consecutivi lo stesso genere
				
				if(Math.random()<0.9) {
					pausa = true;
					this.totPause++;
					this.doubleGender=false;
					continue;
				} else {
					pausa = false;
				}
			}
			
			if(pausa) {//scelgo in modo casuale
				Actor a = this.attoriRimanenti.get((int)Math.random()*(attoriRimanenti.size()-1));
				if(a.getGender().equals(lastGender)) {
					doubleGender = true;
				} else {
					doubleGender = false;
				}
				this.lastGender=a.getGender();
				this.attoriRimanenti.remove(a);
				this.attoriIntervistati.add(a);
				
			} else { // scelgo in base alle specifiche
				if(Math.random()<0.6) { //causalmente
					Actor a = this.attoriRimanenti.get((int)Math.random()*(attoriRimanenti.size()-1));
					if(a.getGender().equals(lastGender)) {
						doubleGender = true;
					} else {
						doubleGender = false;
					}
					this.lastGender=a.getGender();
					this.attoriRimanenti.remove(a);
					this.attoriIntervistati.add(a);
				} else { // una tra quelli suggeriti da last
					
					Actor last = attoriIntervistati.get(attoriIntervistati.size()-1);
					
					if(grafo.outgoingEdgesOf(last).size()>0) { //esiste un attore che last possa suggerirmi
						double max = 0;
						List<Actor> next = new ArrayList<>();
						
						for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(last)) { //cerco attore suggerito
							if(grafo.getEdgeWeight(e)>max) {
								max=grafo.getEdgeWeight(e);
								next = new ArrayList<>();
								next.add(Graphs.getOppositeVertex(grafo, e, last));
							} else if(grafo.getEdgeWeight(e)==max) {
								next.add(Graphs.getOppositeVertex(grafo, e, last));
							}
						}
						
						Actor a;
						
						if(next.size()==1) { //solo uno suggerito
							a = next.get(0);

						} else{ //più di uno suggerito --> scelgo casualmente
							a = next.get((int)Math.random()*(next.size()-1));
						}
						
						if(a.getGender().equals(lastGender)) {
							doubleGender = true;
						} else {
							doubleGender = false;
						}
						this.lastGender=a.getGender();
						this.attoriIntervistati.add(a);
						this.attoriRimanenti.remove(a);
						
					} else { // non esistono attori che last possa suggerirmi --> scelgo casualmente
						
						Actor a = this.attoriRimanenti.get((int)Math.random()*(attoriRimanenti.size()-1));
						if(a.getGender().equals(lastGender)) {
							doubleGender = true;
						} else {
							doubleGender = false;
						}
						this.lastGender=a.getGender();
						this.attoriRimanenti.remove(a);
						this.attoriIntervistati.add(a);
					}
					
				
				}
			}
		}
		
	}
	
	
	public List<String> getGenres(){
		return dao.listAllGenres();
	}


	public Graph<Actor, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}


	public int getTotPause() {
		return totPause;
	}


	public List<Actor> getAttoriIntervistati() {
		return attoriIntervistati;
	}
	
	
	
	
}
