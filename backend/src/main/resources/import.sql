USE raf_novosti;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE vest_tag; TRUNCATE TABLE komentari; TRUNCATE TABLE tagovi;
TRUNCATE TABLE vesti; TRUNCATE TABLE kategorije; TRUNCATE TABLE korisnici;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. KORISNICI (Pravi ljudi)
INSERT INTO korisnici (id, email, ime, prezime, tip, status, lozinka) VALUES
                                                                          (1, 'admin@raf.rs', 'Marko', 'Kraljevic', 'ADMINISTRATOR', 'AKTIVAN', '240be518ebb21465334888be1278e2661c4707e9ed0ea57c800b65383921870a'),
                                                                          (2, 'nikola.tesla@raf.rs', 'Nikola', 'Tesla', 'STVARALAC_SADRZAJA', 'AKTIVAN', '240be518ebb21465334888be1278e2661c4707e9ed0ea57c800b65383921870a'),
                                                                          (3, 'novak@raf.rs', 'Novak', 'Djokovic', 'STVARALAC_SADRZAJA', 'AKTIVAN', '240be518ebb21465334888be1278e2661c4707e9ed0ea57c800b65383921870a'),
                                                                          (4, 'mihajlo.pupin@raf.rs', 'Mihajlo', 'Pupin', 'STVARALAC_SADRZAJA', 'AKTIVAN', '240be518ebb21465334888be1278e2661c4707e9ed0ea57c800b65383921870a'),
                                                                          (5, 'mileva@raf.rs', 'Mileva', 'Maric', 'STVARALAC_SADRZAJA', 'AKTIVAN', '240be518ebb21465334888be1278e2661c4707e9ed0ea57c800b65383921870a');

-- 2. KATEGORIJE
INSERT INTO kategorije (id, ime, opis) VALUES
                                           (1, 'Sport', 'Sve o uspesima naših sportista širom sveta.'),
                                           (2, 'Tehnologija', 'Najnovija dostignuća u svetu softvera i hardvera.'),
                                           (3, 'Politika', 'Aktuelna dešavanja u zemlji i regionu.'),
                                           (4, 'Kultura', 'Pozorišta, bioskopi i umetničke izložbe.'),
                                           (5, 'Nauka', 'Svemir, medicina i fundamentalna istraživanja.');

-- 3. TAGOVI
INSERT INTO tagovi (id, kljucna_rec) VALUES
                                         (1, 'Srbija'), (2, 'Tenis'), (3, 'AI'), (4, 'Fudbal'), (5, 'NASA'), (6, 'Java'), (7, 'Beograd');

-- 4. VESTI (Konkretni podaci)
INSERT INTO vesti (id, naslov, tekst, autor_id, kategorija_id, broj_poseta) VALUES
                                                                                (1, 'Djokovic osvojio novi grend slem', 'Novak Djokovic je u finalu savladao mladog rivala i podigao pehar koji ga ucvrscuje na prvom mestu svih vremena.', 3, 1, 1500),
                                                                                (2, 'Srbija ide na Evropsko prvenstvo', 'Fudbalska reprezentacija Srbije je nakon teske borbe uspela da obezbedi plasman na predstojece prvenstvo.', 2, 1, 800),
                                                                                (3, 'OpenAI lansirao novi GPT-5 model', 'Novi model donosi neverovatne mogucnosti u razumevanju konteksta i resavanju matematickih problema.', 1, 2, 2500),
                                                                                (4, 'Rekonstrukcija centra Beograda', 'Radovi na Trgu republike i okolnim ulicama ulaze u zavrsnu fazu. Ocekuju se guzve u saobracaju.', 4, 3, 400),
                                                                                (5, 'Java 21 uvodi Virtual Threads', 'Ova promena ce drasticno olaksati pisanje konkurentnih aplikacija u Javi i smanjiti koriscenje resursa.', 5, 2, 600),
                                                                                (6, 'James Webb otkrio novu galaksiju', 'Teleskop je snimio najstariju do sada poznatu galaksiju koja datira iz samih pocetaka naseg univerzuma.', 1, 5, 1200),
                                                                                (7, 'Premijera u Narodnom pozoristu', 'Nova predstava pod nazivom "Senke proslosti" odusevila je publiku i dobila odlicne kritike kritike.', 4, 4, 200),
                                                                                (8, 'Novi zakon o digitalnoj imovini', 'Skupstina je danas usvojila zakon koji preciznije definise trgovanje kriptovalutama u nasoj zemlji.', 2, 3, 350),
                                                                                (9, 'Zvezda pobedila u Evroligi', 'Kosarkasi Crvene zvezde su u prepunoj Areni savladali sampiona Evrope nakon neverovatnog preokreta.', 3, 1, 1100),
                                                                                (10, 'Buducnost kvantnih racunara', 'Naucnici iz IBM-a tvrde da smo blizu tacke gde ce kvantni racunari postati komercijalno dostupni.', 5, 2, 700);

-- 5. VEZA VEST-TAG
INSERT INTO vest_tag (vest_id, tag_id) VALUES
                                           (1, 1), (1, 2), -- Djokovic: Srbija, Tenis
                                           (2, 1), (2, 4), -- Fudbal: Srbija, Fudbal
                                           (3, 3), (3, 6), -- AI: AI, Java
                                           (5, 6), (5, 3), -- Java: Java, AI
                                           (4, 1), (4, 7), -- Beograd: Srbija, Beograd
                                           (6, 5);         -- Svemir: NASA

-- 6. KOMENTARI (Pravi tekstovi)
INSERT INTO komentari (autor, tekst, vest_id) VALUES
                                                  ('Laza', 'Najveci svih vremena, svaka cast Nole!', 1),
                                                  ('Mare', 'Jedva smo se plasirali, ali bitno je da smo tamo.', 2),
                                                  ('Programer123', 'Jedva cekam da probam GPT-5, nadam se da je brzi.', 3),
                                                  ('Beogradjanin', 'Opet radovi, ne moze se proci kroz grad.', 4),
                                                  ('JavaDev', 'Virtual threads su zakon, konacno kraj muka sa thread pool-ovima.', 5),
                                                  ('Astronom', 'James Webb je najbolja investicija u istoriji nauke.', 6),
                                                  ('Kole', 'Sjajna utakmica, srce mi je stalo u zadnjoj sekundi!', 9);