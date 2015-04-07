package br.gov.jfrj.siga.tp.interceptor;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.tp.auth.annotation.LogMotivo;

@Intercepts(after = { AutorizacaoAcessoInterceptor.class }, before = ExecuteMethodInterceptor.class)
public class MotivoLogInterceptor implements Interceptor {

	private HttpServletRequest request;
	private Result result;
	private Localization localization;

	public MotivoLogInterceptor(Result result, HttpServletRequest request, Localization localization) {
		this.result = result;
		this.request = request;
		this.localization = localization;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		LogMotivo motivoLogAnnotation = method.getMethod().getAnnotation(LogMotivo.class);

		if (motivoLogAnnotation != null) {
			String motivoLog = request.getParameter("motivoLog");
			if (motivoLog == null) {
				throw new InterceptionException(localization.getMessage("motivoLogAnnotation.exception"));
			}
			result.include("motivoLog", motivoLog);
		}
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return method.containsAnnotation(LogMotivo.class);
	}
}
