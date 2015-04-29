package uteis;

import java.text.Normalizer;

public class FormatarTextoHtml {
	public static String retirarTagsHtml(String conteudo, String espacosHtml) {
		String retorno = conteudo.replace("<br>", "\n");
		retorno = retorno.replace("&aacute", "�");
		retorno = retorno.replace("&eacute", "�");
		retorno = retorno.replace("&oacute", "�");
		retorno = retorno.replace("&iacute", "�");
		retorno = retorno.replace("&uacute", "�");
		retorno = retorno.replace("&atilde", "�");
		retorno = retorno.replace("&otilde", "�");
		retorno = retorno.replace("&ccedil", "�");
		retorno = retorno.replace("<html>", "");
		retorno = retorno.replace("</html>", "");
		retorno = retorno.replace("<p>", "");
		retorno = retorno.replace("</p>", "\n");
		retorno = retorno.replace(espacosHtml, "");
		retorno = retorno.replace("</a href=", "");
		retorno = retorno.replace(">", "");
		retorno = retorno.replace("<b>", "");
		retorno = retorno.replace("</b>", "");
		retorno = retorno.replace("'", "");
		retorno = retorno.replace("</a>", "");
		return retorno;
	}
	
	public static String removerAcentuacao(String texto) {
		return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
}