USE raf_novosti;

-- 1. BRISANJE PODATAKA (Reset)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE vest_tag;
TRUNCATE TABLE komentari;
TRUNCATE TABLE tagovi;
TRUNCATE TABLE vesti;
TRUNCATE TABLE kategorije;
TRUNCATE TABLE korisnici;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. KORISNICI (Lozinka za sve je 'admin123')
INSERT INTO korisnici (id, email, ime, prezime, tip, status, lozinka) VALUES
                                                                          (1, 'admin@raf.rs', 'Marko', 'Admin', 'ADMINISTRATOR', 'AKTIVAN', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'),
                                                                          (2, 'autor@raf.rs', 'Pera', 'Pisac', 'STVARALAC_SADRZAJA', 'AKTIVAN', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'),
                                                                          (3, 'ugasen@raf.rs', 'Mika', 'Mikic', 'STVARALAC_SADRZAJA', 'NEAKTIVAN', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'),
                                                                          (4, 'jovana@raf.rs', 'Jovana', 'Jovic', 'STVARALAC_SADRZAJA', 'AKTIVAN', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- 3. KATEGORIJE
INSERT INTO kategorije (id, ime, opis) VALUES
                                           (1, 'Sport', 'Vesti iz sveta fudbala, tenisa i ostalih sportova.'),
                                           (2, 'Tehnologija', 'Sve o IT industriji, programiranju i AI-u.'),
                                           (3, 'Politika', 'Aktuelna dešavanja u zemlji i svetu.');

-- 4. TAGOVI
INSERT INTO tagovi (id, kljucna_rec) VALUES
                                         (1, 'Srbija'), (2, 'Java'), (3, 'AI'), (4, 'Fudbal'), (5, 'Beograd');

-- 5. VESTI (7 vesti)
INSERT INTO vesti (id, naslov, tekst, vreme_kreiranja, broj_poseta, autor_id, kategorija_id) VALUES
                                                                                                 (1, 'Djokovic osvojio turnir', 'Novak je jos jednom pokazao zasto je najbolji na svetu...', NOW(), 150, 2, 1),
                                                                                                 (2, 'Java 21 je revolucija', 'Virtual threads menjaju nacin na koji pisemo konkurentni kod.', NOW(), 300, 1, 2),
                                                                                                 (3, 'Novi zakon u skupstini', 'Poslanici su danas glasali o novom setu ekonomskih zakona.', NOW(), 50, 1, 3),
                                                                                                 (4, 'Srbija ide na prvenstvo', 'Fudbalska reprezentacija je pobedom osigurala plasman.', NOW(), 120, 2, 1),
                                                                                                 (5, 'Vestacka inteligencija na RAF-u', 'Studenti su razvili novi model za prepoznavanje govora.', NOW(), 450, 4, 2),
                                                                                                 (6, 'Guzve u saobracaju', 'Zbog radova na mostu, ocekuje se kolaps u popodnevnim satima.', NOW(), 80, 4, 3),
                                                                                                 (7, 'Novi startup u Beogradu', 'Mladi tim inzenjera dobio je investiciju od milion dolara.', NOW(), 210, 2, 2);

-- 6. VEZA VEST-TAG
INSERT INTO vest_tag (vest_id, tag_id) VALUES
                                           (1, 1), (2, 2), (2, 3), (3, 1), (4, 1), (4, 4), (5, 2), (5, 3), (6, 5), (7, 1);

-- 7. KOMENTARI (15 komentara)
INSERT INTO komentari (autor, tekst, vest_id) VALUES
                                                  ('Laza', 'Bravo Nole!', 1),
                                                  ('Mare', 'Kralj tenisa.', 1),
                                                  ('JavaFan', 'Konacno Virtual Threads!', 2),
                                                  ('DevOps', 'Ovo ce ubrzati nase servere.', 2),
                                                  ('Mile', 'Samo da ne bude guzve opet.', 6),
                                                  ('Ana', 'Sjajne vesti za startup scenu.', 7),
                                                  ('Kriticar', 'Nije mi jasan ovaj zakon.', 3),
                                                  ('Sale', 'Napred orlovi!', 4),
                                                  ('Gost', 'AI je buducnost, svaka cast studentima.', 5),
                                                  ( 'Student', 'Najbolji faks u gradu.', 5),
                                                  ('Miki', 'Kad ce zavrsiti te radove?', 6),
                                                  ('Dule', 'Fudbal nam je opet u modi.', 4),
                                                  ('Srdjan', 'Java ostaje broj 1.', 2),
                                                  ('Boki', 'Srbija do Tokija!', 4),
                                                  ('Ivana', 'Ovaj tekst je jako koristan.', 7);

COMMIT;