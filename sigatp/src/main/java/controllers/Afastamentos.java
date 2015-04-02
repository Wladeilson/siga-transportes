package controllers;

import java.util.List;

import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import controllers.AutorizacaoGIAntigo.RoleAdmin;
import controllers.AutorizacaoGIAntigo.RoleAdminMissao;
import controllers.AutorizacaoGIAntigo.RoleAdminMissaoComplexo;

@With(AutorizacaoGIAntigo.class)
public class Afastamentos extends Controller {

	public static void listarPorCondutor(Long idCondutor) throws Exception {
		Condutor condutor = Condutor.AR.findById(idCondutor);
		List<Afastamento> afastamentos = Afastamento.buscarTodosPorCondutor(condutor);
		MenuMontador.instance().RecuperarMenuCondutores(idCondutor, ItemMenu.AFASTAMENTOS);
		render(afastamentos, condutor);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir(Long idCondutor) throws Exception {
		Condutor condutor = Condutor.AR.findById(idCondutor);
		Afastamento afastamento = new Afastamento();
		afastamento.condutor = condutor;
		render(afastamento);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id) {
		Afastamento afastamento = Afastamento.findById(id);
		render(afastamento);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(@Valid Afastamento afastamento) throws Exception {
		String template;

		if ((afastamento.dataHoraInicio != null ) && (afastamento.dataHoraFim != null) && (!afastamento.descricao.equals(""))) {
			if (!afastamento.ordemDeDatasCorreta()) {
				Validation.addError("dataHoraInicio", "afastamentos.dataHoraInicio.validation");
			}
		}
		
		template = afastamento.id > 0 ? "Afastamentos/editar.html" : "Afastamentos/incluir.html";
		
		if (Validation.hasErrors()) {
			List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			renderTemplate(template, afastamento, condutores);
		} else {
			List<Missao> missoes = Missao.retornarMissoes("condutor.id",
					afastamento.condutor.getId(),
					afastamento.condutor.getCpOrgaoUsuario().getId(),
					afastamento.dataHoraInicio, afastamento.dataHoraFim);
			String listaMissoes = "";
			String delimitador = "";

			for (Missao item : missoes) {
				listaMissoes += delimitador;
				listaMissoes += item.getSequence();
				delimitador = ",";
			}

			if (missoes.size() > 0) {
				Validation.addError("LinkErroCondutor", listaMissoes);
				renderTemplate(template, afastamento);
			} else {
				afastamento.save();
				listarPorCondutor(afastamento.condutor.getId());
			}
		}
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception {
		Afastamento afastamento = Afastamento.findById(id);
		afastamento.delete();
		listarPorCondutor(afastamento.condutor.getId());
	}

}
