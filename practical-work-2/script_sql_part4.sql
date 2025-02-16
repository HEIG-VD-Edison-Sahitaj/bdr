-- 1
UPDATE Client
SET pseudo = 'BitCoinLover2024'
WHERE pseudo = 'YoLo666';

-- 2
INSERT INTO Client (pseudo, dateNaissance, adresseFacturation, email)
VALUES ('K3V1N', '2009-08-12', 'Rue du haut 12, 1004 Lausanne', 'HeadShot2012@gmail.com');

-- 3
DELETE FROM Article
WHERE id = 5;

-- 4
INSERT INTO Article_MotsCles (articleId, motCle)
VALUES
    (3, 'loot'),
    (3, 'Horadrims');
