package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.mvc.With;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Plantao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;
import controllers.AutorizacaoGIAntigo;

@Resource
@With(AutorizacaoGIAntigo.class)
public class PlantaoController extends TpController {

	public PlantaoController(HttpServletRequest request, Result result,
			CpDao dao, Localization localization, SigaObjects so,
			AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), localization, so, dadosAutorizacao, em);
	}

	// TODO HD arrumar esta budega
	@Path("/app/plantao/listarPorCondutor")
	public void listarPorCondutor(Long idCondutor) throws Exception {
		Condutor condutor = Condutor.AR.findById(idCondutor);
//		Plantao plantao = new Plantao();
//		plantao.id=1234L;
//		plantao.dataHoraFim = Calendar.getInstance();
//		plantao.dataHoraInicio = Calendar.getInstance();
		 List<Plantao> plantoes = Plantao.buscarTodosPorCondutor(condutor);
//		List<Plantao> plantoes = new ArrayList<Plantao>();
//		plantoes.add(plantao);
//		plantoes.add(plantao);
//		plantoes.add(plantao);
		 //TODO  HD what?
		// MenuMontador.instance().RecuperarMenuCondutores(idCondutor,
		// ItemMenu.PLANTOES);
		result.include("plantoes", plantoes);
		result.include("idCond", idCondutor);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/plantao/editar/{idCondutor}/{id}")
	public void editar(Long idCondutor, Long id) throws Exception {
		Plantao plantao;
		if(id > 0)
			plantao = Plantao.AR.findById(id);
		else {
			plantao = new Plantao();
			plantao.setCondutor(buscaCondutor(idCondutor));
		}
		
		result.include("plantao", plantao);
		result.include("idCond", idCondutor);
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/plantao/incluir/{idCondutor}")
	public void incluir(Long idCondutor) throws Exception {
		result.redirectTo(this).editar(idCondutor, 0L);
	}

	private static String formatarData(Calendar data) {
		return String.format("%02d", data.get(Calendar.DAY_OF_MONTH)) + "/"
				+ String.format("%02d", data.get(Calendar.MONTH) + 1) + "/"
				+ String.format("%04d", data.get(Calendar.YEAR));
	}

	// @RoleAdmin
	// @RoleAdminMissao
	// @RoleAdminMissaoComplexo
	// @Path("/app/plantao/editar")
	// public void salvar(
	// @Valid Plantao plantao,
	// @As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" }) Calendar
	// dataHoraInicioNova,
	// @As(lang = { "*" }, value = { "dd/MM/yyyy HH:mm" }) Calendar
	// dataHoraFimNova)
	// throws Exception {
	// String template = plantao.id > 0 ? "Plantoes/editar.html"
	// : "Plantoes/incluir.html";
	//
	// if (!plantao.ordemDeDatasCorreta()) {
	// Validation.addError("dataHoraInicio",
	// "plantoes.dataHoraInicio.validation");
	// renderTemplate(template, plantao);
	// }
	//
	// String listaAfastamento = "";
	// List<Afastamento> afastamentos = Afastamento.buscarPorCondutores(
	// plantao.condutor.getId(), formatarData(plantao.dataHoraInicio),
	// formatarData(plantao.dataHoraFim));
	//
	// for (Afastamento item : afastamentos) {
	// listaAfastamento += listaAfastamento == "" ? "" : ",";
	// listaAfastamento += formatarData(item.dataHoraInicio) + " a "
	// + formatarData(item.dataHoraFim);
	// }
	//
	// if (!listaAfastamento.equals("")) {
	// String plural = listaAfastamento.contains(",") ? "nos per&iacute;odos"
	// : "no per&iacute;odo";
	// Validation.addError("plantao", "Condutor afastado " + plural
	// + " de: " + listaAfastamento + ".");
	// }
	//
	// if (Validation.hasErrors()) {
	// renderTemplate(template, plantao);
	// } else {
	// String listaPlantao = "";
	// List<Plantao> plantoes = Plantao.buscarPorCondutores(
	// plantao.condutor.getId(),
	// formatarData(plantao.dataHoraInicio),
	// formatarData(plantao.dataHoraFim));
	//
	// for (Plantao item : plantoes) {
	// listaPlantao += listaPlantao == "" ? "" : ",";
	// listaPlantao += formatarData(item.dataHoraInicio) + " a "
	// + formatarData(item.dataHoraFim);
	// }
	//
	// if (!listaPlantao.equals("")) {
	// String plural = listaPlantao.contains(",") ? "nos per&iacute;odos"
	// : "no per&iacute;odo";
	// Validation.addError("plantao", "Condutor em plant&atilde;o "
	// + plural + " de: " + listaPlantao + ".");
	// }
	// }
	//
	// if (Validation.hasErrors()) {
	// renderTemplate(template, plantao);
	// } else {
	// if (template.contains("editar")) {
	// if (!(plantao.dataHoraInicio.before(dataHoraInicioNova) &&
	// plantao.dataHoraFim
	// .after(dataHoraFimNova))) {
	// List<Missao> missoes = retornarMissoesCondutorPlantao(
	// plantao, dataHoraInicioNova, dataHoraFimNova);
	// String listaMissoes = "";
	//
	// for (Missao item : missoes) {
	// listaMissoes += listaMissoes == "" ? "" : ",";
	// listaMissoes += item.getSequence();
	// }
	//
	// if (missoes.size() > 0) {
	// Validation.addError("LinkErroCondutor", listaMissoes);
	// }
	// }
	// }
	//
	// if (Validation.hasErrors()) {
	// renderTemplate(template, plantao);
	// } else {
	// plantao.save();
	// listarPorCondutor(plantao.condutor.getId());
	// }
	// }
	// }

	private static List<Missao> retornarMissoesCondutorPlantao(Plantao plantao,
			Calendar dataHoraInicioNova, Calendar dataHoraFimNova) {
		List<Missao> retorno = new ArrayList<Missao>();

		if (dataHoraInicioNova == null && dataHoraFimNova == null) {
			return Missao.retornarMissoes("condutor.id", plantao.condutor
					.getId(), plantao.condutor.getCpOrgaoUsuario().getId(),
					plantao.dataHoraInicio, plantao.dataHoraFim);
		}

		if (dataHoraInicioNova != null) {
			if (dataHoraInicioNova.after(plantao.dataHoraInicio)) {
				retorno.addAll(Missao.retornarMissoes("condutor.id",
						plantao.condutor.getId(), plantao.condutor
								.getCpOrgaoUsuario().getId(),
						dataHoraInicioNova, plantao.dataHoraInicio));
			}
		}

		if (dataHoraFimNova != null) {
			if (dataHoraFimNova.before(plantao.dataHoraFim)) {
				retorno.addAll(Missao.retornarMissoes("condutor.id",
						plantao.condutor.getId(), plantao.condutor
								.getCpOrgaoUsuario().getId(),
						plantao.dataHoraFim, dataHoraFimNova));
			}
		}

		return retorno;
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/plantao/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Plantao plantao = Plantao.AR.findById(id);

		List<Missao> missoes = retornarMissoesCondutorPlantao(plantao, null,
				null);
		String listaMissoes = "";
		String delimitador = "";

		for (Missao item : missoes) {
			listaMissoes += delimitador;
			listaMissoes += item.getSequence();
			delimitador = ",";
		}

		// TODO HD arrumar!
//		 if (missoes.size() > 0) {
//		 Validation.addError("LinkErroCondutor", listaMissoes);
//		 }
//		
//		 if (Validation.hasErrors()) {
//		 renderTemplate("Afastamentos/editar.html", plantao);
//		 } else {
		 plantao.delete();
		 listarPorCondutor(plantao.condutor.getId());
//		 }
	}
	
	@Path("/app/plantao/salvar")
	public void salvar(Plantao plantao) throws Exception {
//		Condutor condutor = Condutor.AR.findById(165050L);
//		plantaoSalvar.dataHoraFim = Calendar.getInstance();
//		plantaoSalvar.dataHoraInicio = Calendar.getInstance();
//		plantaoSalvar.setCondutor(condutor);
		
		plantao.save();
	 	listarPorCondutor(plantao.condutor.getId());
    }
	
	private Condutor buscaCondutor(Long idCondutor) throws Exception {
		return Condutor.AR.findById(idCondutor);
	}

}