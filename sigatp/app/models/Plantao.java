package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;


@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Plantao extends GenericModel implements Comparable<Plantao>  {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public long id;
		
	@Required
	@ManyToOne(cascade=CascadeType.ALL)
	@NotNull
	public Condutor condutor;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Inicio")
	public Calendar dataHoraInicio;
	
	@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora Fim")
	public Calendar dataHoraFim;
	
	public Plantao(){
		this.id = new Long(0);
	}
	
	public String referencia;

	
	public Plantao(long id, Condutor condutor, Calendar dataHoraInicio,
			Calendar dataHoraFim) {
		super();
		this.id = id;
		this.condutor = condutor; 
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
	}
	
	public Plantao(Condutor condutor, Calendar dataHoraInicio,
			Calendar dataHoraFim) {
		this.id = new Long(0);
		this.condutor = condutor; 
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
	}
	
	public static List<Plantao> buscarTodosPorCondutor(Condutor condutor){
		return Plantao.find("CONDUTOR_ID = ? ORDER BY DATAHORAINICIO DESC", condutor.id).fetch();
	}
	
	
	public static List<Plantao> buscarPorCondutor(Long IdCondutor, Calendar dataHoraInicio){
		return Plantao.find("condutor.id = ? AND dataHoraInicio <= ? AND (dataHoraFim is null OR dataHoraFim >= ?) order by dataHoraInicio", IdCondutor, dataHoraInicio, dataHoraInicio).fetch(); 
	}
	
	
	public boolean ordemDeDatasCorreta(){
		return this.dataHoraInicio.before(this.dataHoraFim);
	}

	@SuppressWarnings("unchecked")
	private static List<Plantao> retornarLista(String qrl) throws NoResultException {
		List<Plantao> plantoes;
		Query qry = JPA.em().createQuery(qrl);
		try {
			plantoes = (List<Plantao>) qry.getResultList();
		} catch(NoResultException ex) {
			plantoes = null;
		}
		return plantoes; 
	}

	public static List<Plantao> buscarPorCondutores(Long IdCondutor, String dataHoraInicio){
		String filtroCondutor = "";
		String dataFormatadaOracle = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";

		if (IdCondutor != null) {
			filtroCondutor = "condutor.id = " + IdCondutor + " AND ";  
		}
		
		String qrl = 	"SELECT p FROM Plantao p WHERE " + filtroCondutor +
						"  trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracle + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracle + "))";

		return retornarLista(qrl); 
	}
	
	public static List<Plantao> buscarPorCondutores(Long IdCondutor, String dataHoraInicio, String dataHoraFim){
		String dataFormatadaOracleInicio = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		String dataFormatadaOracleFim = "to_date('" + dataHoraFim + "', 'DD/MM/YYYY')";
		String filtroCondutor = "";

		if (IdCondutor != null) {
			filtroCondutor = "condutor.id = " + IdCondutor + " AND ";  
		}
		
		String qrl = 	"SELECT p FROM Plantao p " +
		                " WHERE " + filtroCondutor +
						" ((trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracleInicio + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracleInicio + ")))" +
						" OR (trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracleFim + ")" +  	
						" AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracleFim + "))))";

		return retornarLista(qrl); 
	}


	@Override
	public int compareTo(Plantao o) {
		// por enquanto compara por data de inicio
		// sera usado para ordenar os plantoes 24h
		
		return this.dataHoraInicio.compareTo(o.dataHoraInicio);
	}
	
	public static List<String> getReferencias(Long orgaoUsuario) {
		List<Plantao> objetos;
		List<String> retorno;
		//TODO ordenar
		/*SELECT p.referencia FROM Plantao p
WHERE p.timestamp = (SELECT MAX(ee.timestamp) FROM Entity ee WHERE ee.entityId = e.entityId)*/
		Query qry = JPA.em().createQuery("select p from Plantao p "
										+ "where p.referencia is not null "
										+ "and p.dataHoraInicio = (select max(pp.dataHoraInicio) from Plantao pp where pp.referencia = p.referencia) "
										+ "and p.condutor.cpOrgaoUsuario = " + orgaoUsuario + " "
										+ "order by p.dataHoraInicio desc ");
		try {
			objetos = (List<Plantao>) qry.getResultList();
		} catch(NoResultException ex) {
			return null;
		}
		
		retorno = new ArrayList<String>();
		for (Iterator<Plantao> iterator = objetos.iterator(); iterator.hasNext();) {
			Plantao plantao = (Plantao) iterator.next();
			retorno.add(plantao.referencia);
		}
		return retorno;
	}
	
	public static List<Plantao> getPlantoesPorReferencia(String referencia) {
		List<Plantao> retorno;
		Query qry = JPA.em().createQuery(
				"select p "
				+ "from Plantao p "
				+ "where p.referencia = '" + referencia + "' "
				+ "order by p.dataHoraInicio");
		try {
			retorno = (List<Plantao>) qry.getResultList();
		} catch(NoResultException ex) {
			retorno = null;
		}
		return retorno;
	}

	public static boolean plantaoMensalJaExiste(String referencia) {
		List<Plantao> plantoes;
		Query qry = JPA.em().createQuery(
				"select p "
				+ "from Plantao p "
				+ "where p.referencia = '" + referencia + "'");
		try {
			plantoes = (List<Plantao>) qry.getResultList();
		} catch(NoResultException ex) {
			return false;
		}
		if(plantoes.isEmpty()) {
			return false;
		}
		return true;
	}

	public static List<Plantao> buscarTodosPorReferencia(String referencia) {
		List<Plantao> retorno;
		
		retorno = Plantao.find("referencia", referencia).fetch();
		
		return retorno;
	}
}
