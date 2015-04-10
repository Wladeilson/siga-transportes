<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>${condutor.dadosParaExibicao}</h2>
			<h3>Autos de Infra&ccedil;&atilde;o</h3>

			<%-- #{include 'Condutores/menu.html' /} --%>

			<c:choose>
				<c:when test="${autosDeInfracao.size() > 0}">
					<div class="gt-content-box gt-for-table">
						<table id="htmlgrid" class="gt-table">
							<thead>
								<tr style="font-weight: bold;">
									<th>Data e Hora</th>
									<th>Ve&iacute;culo</th>
									<th>Gravidade</th>
									<th>Qtd. Pontos</th>
									<th>Valor</th>
									<th>Valor c/ Desconto</th>
									<th>Vencimento</th>
									<th>Pagamento</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${autosDeInfracao}" var="autoDeInfracao">
									<tr>
										<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${autoDeInfracao.dataHora.time}" /></td>
										<td>${autoDeInfracao.veiculo.dadosParaExibicao}</td>
										<td>${autoDeInfracao.gravidade.descricao}</td>
										<td>${autoDeInfracao.quantidadeDePontos}</td>
										<td>${autoDeInfracao.valor}</td>
										<td>${autoDeInfracao.valorComDesconto}</td>
										<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${autoDeInfracao.dataDeVencimento.time}" /></td>
										<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${autoDeInfracao.dataDePagamento.time}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div id="pagination" />
					</div>
				</c:when>
				<c:otherwise>
					<br />
					<h3>N&atilde;o existem autos de infra&ccedil;&atilde;o cadastrados para este condutor.</h3>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</siga:pagina>