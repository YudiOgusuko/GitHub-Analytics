package br.GitHub.Analytics.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utilitarios {

    public static String formatarString(String txt) {
        if(txt == null || txt.isBlank()) {
            return "";
        }

        String[] palavra = txt.trim().split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();

        for(String p : palavra) {
            if(!p.isBlank()) {
                String formatar = p.substring(0, 1).toUpperCase()
                        + p.substring(1).toLowerCase();

                stringBuilder.append(formatar).append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }

    public static String tirarEspaco(String txt) {
        if(txt == null || txt.isBlank()) {
            return "";
        }

        String[] palavra = txt.trim().split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();

        for(String p : palavra) {
            if(!p.isBlank()) {
                String formatar = p.charAt(0) + p.substring(1);

                stringBuilder.append(formatar);
            }
        }
        return stringBuilder.toString().trim().toLowerCase();
    }

    public static String formatarNomeRepositorioNoBanco(String txt) {
        if (txt == null || txt.isBlank()) {
            return "";
        }

        String[] palavras = txt.trim().split("\\s+");
        String[] formatadas = new String[palavras.length];

        for (int i = 0; i < palavras.length; i++) {
            String p = palavras[i];
            formatadas[i] = p.substring(0, 1).toUpperCase() + p.substring(1).toLowerCase();
        }

        return String.join("-", formatadas);
    }

    public static String formatarNomeRepositorioApi(String txt) {
        if (txt == null || txt.isBlank()) {
            return "";
        }

        String[] palavras = txt.trim().split("\\s+");
        String[] formatadas = new String[palavras.length];

        for (int i = 0; i < palavras.length; i++) {
            String p = palavras[i];
            formatadas[i] = p.substring(0, 1).toUpperCase() + p.substring(1).toLowerCase();
        }

        return String.join("_", formatadas);
    }

    public static LocalDate ordenarData (String data) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
        return LocalDate.parse(data, dateTimeFormat);
    }

    public static LocalDate formatarDataRepositorio (String data) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("pt-BR"));
        return LocalDate.parse(data, dateTimeFormat);
    }

    public static String formatarDataSincronia(Instant data) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH mm ss", Locale.forLanguageTag("pt-BR"))
                .withZone(ZoneId.of("America/Sao_Paulo"));
        return dateTimeFormat.format(data);
    }

    public static LocalDateTime formatarDataHistorico (String data) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH mm ss", Locale.forLanguageTag("pt-BR"));
        return LocalDateTime.parse(data, dateTimeFormat);
    }
}
