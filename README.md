# Aplikacija za skeniranje QR kode in upravljanje pametnih paketnikov

Aplikacija za skeniranje QR kode omogoča uporabnikom, da dodajo pametne paketnike in jih odprejo s pomočjo algoritma za prepoznavanje obraza. Aplikacija je napisana v jeziku Kotlin in uporablja Compose framework za uporabniški vmesnik.

## Funkcionalnosti

1. **Skeniranje QR kode:** Aplikacija omogoča uporabnikom, da s pomočjo kamere na svoji napravi skenirajo QR kodo na pametnem paketniku. QR koda vsebuje informacije o paketniku, ki se bodo uporabile pri dodajanju paketnika v uporabnikov račun.

2. **Dodajanje pametnih paketnikov:** Po skeniranju QR kode lahko uporabnik doda paketnik v svoj račun. Informacije o paketniku, kot so ime, naslov in uporabnikov ID, se shranijo v podatkovno bazo za nadaljnjo uporabo.

3. **Prepoznavanje obraza:** Ko uporabnik želi odpreti paketnik, aplikacija uporabi algoritem za prepoznavanje obraza, da preveri, ali je uporabnikov obraz pravilno prepoznan. Če je prepoznavanje uspešno, se paketnik odpre. V nasprotnem primeru uporabniku ni dovoljeno dostopati do paketnika.

4. **Beleženje dostopa:** Aplikacija beleži, kdo vse je odprl določen paketnik. Vsakič, ko se paketnik odpre, se shranijo časovni žig in ID uporabnika, ki je opravil dostop.

5. **Obvestila o sumu vdora:** Če se zaznajo večji tresljaji na paketniku, kar lahko nakazuje na poskus vdora, aplikacija pošlje uporabniku obvestilo o sumu vdora. Uporabnik lahko nato sprejme ustrezne ukrepe za varovanje paketnika.

## Tehnične zahteve

- Jezik: Kotlin
- Okvir: Compose
- Podatkovna baza: Uporaba ustrezne podatkovne baze za shranjevanje informacij o paketnikih in dostopih.
- Algoritem za prepoznavanje obraza
