CREATE DATABASE IF NOT EXISTS raf_novosti CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE raf_novosti;

-- 1. Tabela: Korisnici
CREATE TABLE korisnici (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           email VARCHAR(100) NOT NULL UNIQUE,
                           ime VARCHAR(50) NOT NULL,
                           prezime VARCHAR(50) NOT NULL,
                           tip ENUM('ADMINISTRATOR', 'STVARALAC_SADRZAJA') NOT NULL,
                           status ENUM('AKTIVAN', 'NEAKTIVAN') NOT NULL DEFAULT 'AKTIVAN',
                           lozinka VARCHAR(255) NOT NULL -- Ovde ide SHA-256 hash
);

-- 2. Tabela: Kategorije
CREATE TABLE kategorije (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            ime VARCHAR(100) NOT NULL UNIQUE,
                            opis TEXT NOT NULL
);

-- 3. Tabela: Vesti
CREATE TABLE vesti (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       naslov VARCHAR(255) NOT NULL,
                       tekst TEXT NOT NULL,
                       vreme_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       broj_poseta INT NOT NULL DEFAULT 0,
                       broj_lajkova INT NOT NULL DEFAULT 0,
                       broj_dislajkova INT NOT NULL DEFAULT 0,
                       autor_id INT NOT NULL,
                       kategorija_id INT NOT NULL,
                       FOREIGN KEY (autor_id) REFERENCES korisnici(id),
    -- RESTRICT brani brisanje kategorije ako ima vesti u njoj (zahtev iz specifikacije)
                       FOREIGN KEY (kategorija_id) REFERENCES kategorije(id) ON DELETE RESTRICT
);

-- 4. Tabela: Tagovi
CREATE TABLE tagovi (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        kljucna_rec VARCHAR(50) NOT NULL UNIQUE
);

-- 5. Vezna tabela: Vest_Tag (Many-to-Many)
CREATE TABLE vest_tag (
                          vest_id INT NOT NULL,
                          tag_id INT NOT NULL,
                          PRIMARY KEY (vest_id, tag_id),
                          FOREIGN KEY (vest_id) REFERENCES vesti(id) ON DELETE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tagovi(id) ON DELETE CASCADE
);

-- 6. Tabela: Komentari
CREATE TABLE komentari (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           autor VARCHAR(100) NOT NULL, -- Ime koje citalac unese na frontu
                           tekst TEXT NOT NULL,
                           vreme_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           broj_lajkova INT NOT NULL DEFAULT 0,
                           broj_dislajkova INT NOT NULL DEFAULT 0,
                           vest_id INT NOT NULL,
    -- CASCADE brise komentare kada se obrise vest (zahtev iz specifikacije)
                           FOREIGN KEY (vest_id) REFERENCES vesti(id) ON DELETE CASCADE
);

-- Inicijalni podaci za Admina (Lozinka je primer za "admin123" u SHA-256)
-- U specifikaciji pise da se prvi admin unosi rucno u bazu
INSERT INTO korisnici (email, ime, prezime, tip, status, lozinka)
VALUES ('admin@raf.rs', 'Admin', 'Raf', 'ADMINISTRATOR', 'AKTIVAN', '240aa35473d440a110151d38eb41155ccbba9f43e377695c6468600871158fdd');