package br.gov.jfrj.siga.tp.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

/**
 * Controller criado apenas para considerar a chamado ao metodo buscarPelaSequence na tela de erro.
 * 
 * @author db1
 *
 */
@Resource
@Path("/app/missoes")
public class MissoesController extends TpController {

	public MissoesController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), localization, validator, so, em);
	}

	// @RoleAdmin
	// @RoleAdminMissao
	// @RoleAdminMissaoComplexo
	// @RoleAgente
	@Path("/buscarPelaSequenceAposErro/{sequence}")
	public void buscarPelaSequenceAposErro(String sequence) throws Exception {
		// String sequence = "parse";
		// boolean popUp = true;

		result.use(Results.http()).body("TO DO");
	}
}