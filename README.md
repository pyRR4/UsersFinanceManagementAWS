# Zarządzanie Finansami - Aplikacja Serverless w Chmurze AWS

## Opis Projektu

Jest to w pełni bezserwerowa (serverless) aplikacja do zarządzania finansami osobistymi, zbudowana w chmurze AWS. Projekt został stworzony w celach edukacyjnych, aby zademonstrować budowę nowoczesnej, skalowalnej i bezpiecznej architektury opartej o usługi AWS, zarządzanej w całości za pomocą "Infrastruktury jako Kod" (IaC) z użyciem Terraforma.

Aplikacja pozwala użytkownikom na śledzenie swoich transakcji, zarządzanie kategoriami wydatków, tworzenie celów oszczędnościowych oraz wykorzystuje asynchroniczne procesy do generowania raportów i prognoz.

### Aktualny Stan Projektu (Ważne)
> ❗ **Uwaga:** Ten projekt jest wciąż w fazie rozwoju. Poniższe funkcjonalności są zaimplementowane i wdrożone, ale mogą wymagać dalszych testów lub poprawek.
> * ✅ **Uwierzytelnianie użytkowników** przez Amazon Cognito.
> * ✅ **Pełne operacje CRUD oraz działanie API** (Create, Read, Update, Delete) dla transakcji, kategorii i celów oszczędnościowych.
> * ❌ **Automatyczne generowanie raportów, prognozowanie wydatków, powiadomienia** jedynie vibe-coded, jeszcze nieprzetestowane.

---

## Architektura

System został zaprojektowany w oparciu o architekturę sterowaną zdarzeniami i bezserwerową, co zapewnia wysoką skalowalność, bezpieczeństwo i optymalizację kosztów (płatność tylko za faktyczne użycie).

* **API:** Publicznie dostępne **API Gateway** z endpointami RESTful.
* **Logika Aplikacji:** Funkcje **AWS Lambda** napisane w Javie 17, obsługujące logikę biznesową.
* **Baza Danych:** Relacyjna baza danych **PostgreSQL** w usłudze **Amazon RDS**, umieszczona w prywatnej podsieci dla maksymalnego bezpieczeństwa.
* **Pula Połączeń:** **RDS Proxy** zarządza pulą połączeń z bazą danych, chroniąc ją przed przeciążeniem i rozwiązując problemy z "zimnym startem" Lambdy.
* **Uwierzytelnianie:** **Amazon Cognito User Pools** zarządza rejestracją, logowaniem i autoryzacją użytkowników za pomocą tokenów JWT.
* **Procesy Asynchroniczne:**
    * **EventBridge Scheduler** cyklicznie uruchamia zadania w tle.
    * **Amazon SQS** służy jako kolejka zadań.
    * **Amazon SNS** wysyła powiadomienia do użytkowników.
    * **AWS Step Functions** orkiestruje wieloetapowe procesy (np. prognozowanie).
* **Sekrety:** Dane dostępowe do bazy są bezpiecznie przechowywane w **AWS Secrets Manager**.
* **Infrastruktura jako Kod (IaC):** Cała powyższa infrastruktura jest definiowana i zarządzana w **Terraformie** przy użyciu reużywalnych modułów.

---

## Technologie

* **Backend:** Java 17, Maven
* **Infrastruktura jako Kod:** Terraform
* **Baza Danych:** PostgreSQL
* **Główne Usługi AWS:** Lambda, API Gateway, RDS, Cognito, S3, SQS, SNS, Step Functions, EventBridge, IAM, CloudFront, CloudWatch, Secrets Manager.

---

## Uruchomienie i Deployment

Poniższe kroki pozwolą na wdrożenie całej infrastruktury na nowym koncie AWS.

### Wymagania Wstępne
1.  Zainstalowane **AWS CLI** i skonfigurowane poświadczenia (`aws configure`).
2.  Zainstalowany **Terraform** (wersja 1.0.0+).
3.  Zainstalowana **Java 17 (JDK)**.
4.  Zainstalowany **Apache Maven**.

### Kroki Wdrożenia

1.  **Sklonuj Repozytorium:**
    ```bash
    git clone [https://github.com/pyRR4/UsersFinanceManagementAWS.git](https://github.com/pyRR4/UsersFinanceManagementAWS.git)
    cd UsersFinanceManagementAWS
    ```

2.  **Skompiluj Aplikację Java:**
    Przejdź do folderu z kodem aplikacji i zbuduj plik `.jar`.
    ```bash
    cd application
    mvn clean package
    cd .. 
    ```

3.  **Skonfiguruj Zmienne Terraforma:**
    * Przejdź do folderu z kodem infrastruktury: `cd infrastructure`.
    * Stwórz plik `terraform.tfvars` z kopii pliku `terraform.tfvars.example` lub od zera.
    * W pliku `terraform.tfvars` umieść wymagane z pliku `terraform.tfvars.example` dane.
    * **Ważne:** Dodaj `terraform.tfvars` do pliku `.gitignore`, aby nie wysłać go do repozytorium!

4.  **Wdróż Infrastrukturę:**
    * Zainicjuj Terraform (pobierze potrzebne moduły i providery):
        ```bash
        terraform init
        ```
    * (Opcjonalnie) Zobacz plan wdrożenia:
        ```bash
        terraform plan
        ```
    * Wdróż całą infrastrukturę do chmury AWS:
        ```bash
        terraform apply
        ```
    * Zatwierdź, wpisując `yes`. Proces może potrwać 10-15 minut, zwłaszcza przy pierwszym tworzeniu bazy danych.

5.  **Konfiguracja po Wdrożeniu (Jednorazowa):**
    * **Stwórz Schemę Bazy Danych:** Połącz się z nowo utworzoną bazą danych RDS (używając metody z **Bastion Host** i klienta SQL jak DBeaver/pgAdmin) i wykonaj skrypt `schema.sql`, aby stworzyć wszystkie potrzebne tabele (chwilowo brak automigracji).

### Testowanie API
Po wdrożeniu, `terraform output` wyświetli adres URL Twojego API oraz konieczne dane. Testowanie zabezpieczonych endpointów wymaga tokenu JWT.
