package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiTestClient {

    // --- UZUPEŁNIJ TE TRZY ZMIENNE ---
    // 1. Pełny adres URL Twojego API z etapu 'dev'
    private static final String API_URL = "https://lbfcw5al5g.execute-api.us-east-1.amazonaws.com/dev";

    // 2. Twój najnowszy, świeżo pobrany ID Token z paska adresu przeglądarki
    private static final String ID_TOKEN = "eyJraWQiOiJ3eSthS2N1bmIzckVaR0thRmQwQnZLSEZ1NWVXcEx5ZThETW1JeHJLZkRjPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiN2s2VEJzM2RtVzNYRExtN1JYdmxEUSIsInN1YiI6ImE0Nzg1NDE4LTAwNzEtNzA3OS0yMmMyLWEyYzUyMzllMGNkZCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9ET2M1c2xQWEciLCJjb2duaXRvOnVzZXJuYW1lIjoiYTQ3ODU0MTgtMDA3MS03MDc5LTIyYzItYTJjNTIzOWUwY2RkIiwiYXVkIjoiNTFlanZiMDU1OWJ0aHRrcGV2dWhrbDM2Ym8iLCJldmVudF9pZCI6ImE2MWY2NGU2LWZkZDYtNGMwMy1hMTA3LTQ2ODYzYWVhYjQzZiIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNzUxMDExMTg2LCJleHAiOjE3NTEwMTQ3ODYsImlhdCI6MTc1MTAxMTE4NiwianRpIjoiOWU0Yjc0ZGEtMTY0Ni00MmUxLTkyNmItOTAyMzBiOWQ2MzZlIiwiZW1haWwiOiJpZ29wb29kMzNAZ21haWwuY29tIn0.eYdPEcazsZ4sOkCSZ6DgvqcVsSc9ukcOvtP4aBaY8elr4eSIm0c1eH1bSemtMWc5j8M546HLDx7umQtTyBOM_UJdDGyYNSOIIXj5aGaYfZfhk-CpJu1XXlGw9ZP4rDt-_DSTD3CqymmvxTx61SWfCXv2OJLn1mFJEgl2CsCdNkLfFTHgoHl-c_KlXyxYBTgG4BqaLEPRg7_8gGST52tx8BzMAJwU86ZUG-OCUv2Ispq34PEXWmCTdacmv9R2GVW452MfkmhRkHjmv8qTS81u5--eHWt2woyNhD_33uae-FdhXX74H0muLc1o-QmhLlssoOvkEt9dHz6r4uLZXJaUQw";

    // 3. Ciało żądania w formacie JSON
    private static final String JSON_PAYLOAD = "{\"name\": \"Test Bezpośrednio z Javy\"}";


    public static void main(String[] args) {
        System.out.println("Attempting to send a raw HTTP request...");

        // Tworzymy klienta HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Budujemy nasze żądanie HTTP z pełną kontrolą nad nagłówkami
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/categories"))
                .header("Content-Type", "application/json")
                // Programowo i bezbłędnie budujemy nagłówek Authorization
                .header("Authorization", "Bearer " + ID_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(JSON_PAYLOAD))
                .build();

        try {
            // Wysyłamy żądanie i czekamy na odpowiedź
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Drukujemy wynik
            System.out.println("---- WYNIK TESTU ----");
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Headers: " + response.headers());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            System.err.println("Wystąpił krytyczny błąd podczas wysyłania żądania:");
            e.printStackTrace();
        }
    }
}