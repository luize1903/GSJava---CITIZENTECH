package dominio;

import java.util.Locale;

//vírgula decimal
public final class Formatacao {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Formatacao() {
    }

    public static String numero(double valor) {
        return String.format(PT_BR, "%.1f", valor);
    }
}
