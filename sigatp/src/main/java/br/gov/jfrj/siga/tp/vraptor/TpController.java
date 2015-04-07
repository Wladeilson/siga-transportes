package br.gov.jfrj.siga.tp.vraptor;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.Autorizacoes;
import br.gov.jfrj.siga.tp.model.CpRepository;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class TpController extends SigaController {

	private static final Logger LOGGER = Logger.getLogger(TpController.class);
	
	protected Validator validator;
	protected Localization localization;
	private AutorizacaoGI dadosAutorizacao;

	public TpController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, dao, so, em);
		this.validator = validator;
		this.dadosAutorizacao = dadosAutorizacao;
		this.localization = localization;
		this.preencherDadosPadrao();
	}
	private void preencherDadosPadrao() throws Exception {
		this.preencherDadosAutorizacoes();
		this.result.include("currentTimeMillis", new Date().getTime());
	}

	private void preencherDadosAutorizacoes() {
		result.include(Autorizacoes.EXIBIR_MENU_ADMINISTRAR, dadosAutorizacao.ehAdministrador());
		result.include(Autorizacoes.EXIBIR_MENU_ADMINISTRAR_FROTA, dadosAutorizacao.ehAdministradorFrota());
		result.include(Autorizacoes.EXIBIR_MENU_ADMINISTRAR_MISSAO, dadosAutorizacao.ehAdministradorMissao());
		result.include(Autorizacoes.EXIBIR_MENU_APROVADOR, dadosAutorizacao.ehAprovador());
		result.include(Autorizacoes.EXIBIR_MENU_GABINETE, dadosAutorizacao.ehGabinete());
		result.include(Autorizacoes.EXIBIR_MENU_ADMIN_GABINETE, dadosAutorizacao.ehAdminGabinete());
		result.include(Autorizacoes.EXIBIR_MENU_AGENTE, dadosAutorizacao.ehAgente());
		result.include(Autorizacoes.EXIBIR_MENU_ADMMISSAO_ADMINISTRAR_MISSAO_COMPLEXO, dadosAutorizacao.deveExibirMenuAdmissaoComplexo());
	}
	
	/**
	 * Recupera na configura��o do GI o complexo padr�o para usu�rio logado verificando �rgao e Lota��o e o tipo de configur��o "Utilizar Complexo"
	 * 
	 * @return
	 * @throws Exception
	 */
	protected CpComplexo recuperarComplexoPadrao() throws Exception {
		return recuperarComplexoPadrao(getTitular());
	}

	protected CpComplexo recuperarComplexoPadrao(DpPessoa dpPessoa) throws Exception {
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf = CpRepository.findById(CpTipoConfiguracao.class, TIPO_CONFIG_COMPLEXO_PADRAO);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = CpRepository.findById(CpSituacaoConfiguracao.class, 1L);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPadrao = CpRepository.findById(CpSituacaoConfiguracao.class, 5L);
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// Recuperando Configura��o Pode para uma lota��o espec�fica
		Object[] parametros = { dpPessoa.getLotacao().getIdLotacaoIni(), cpSituacaoConfiguracaoPode, dpPessoa.getOrgaoUsuario(), tpConf };
		configuracoes = CpRepository.find(CpConfiguracao.class, "((lotacao.idLotacaoIni = ? and cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )",
				parametros).fetch();
		if (configuracoes != null && configuracoes.size() > 0) {
			cpComplexo = configuracoes.get(0).getComplexo();
		} else {
			// Recuperando Configura��o default para um �rg�o espec�fico
			Object[] parametros1 = { cpSituacaoConfiguracaoPadrao, dpPessoa.getOrgaoUsuario(), tpConf };
			configuracoes = CpRepository.find(CpConfiguracao.class, "((cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
			if (configuracoes != null && configuracoes.size() > 0) {
				cpComplexo = configuracoes.get(0).getComplexo();
			}
		}
		if (cpComplexo == null) {
			throw new Exception(Messages.get("cpComplexo.null.exception"));
		}
		return cpComplexo;
	}

	// TODO: adicionar tratamento de excecao generico em algum lugar
	public void tratarExcecoes(Exception e) {
		EntityManager em = ContextoPersistencia.em();

		if (getCadastrante() != null)
			LOGGER.error(MessageFormat.format("Erro Siga-TP; Pessoa: {0}; Lota��o: {1}", getCadastrante().getSigla(), getLotaTitular().getSigla()), e);

		if (em != null && em.getTransaction() != null && em.getTransaction().isActive())
			em.getTransaction().rollback();
		e.printStackTrace();
		LOGGER.error(e.getMessage(), e);
	}

	protected String getBaseSiga() {
		return MessageFormat.format("http://{0}:{1}/siga", getRequest().getServerName(), String.valueOf(getRequest().getServerPort()));
	}

}