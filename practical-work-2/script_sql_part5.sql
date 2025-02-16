-- 1
ALTER TABLE DLC
ALTER COLUMN necessiteJeuDeBase SET DEFAULT TRUE;

-- 2
ALTER TABLE JeuVideo
ADD CONSTRAINT CK_JeuVideo_ageMinimum CHECK (ageMinimum BETWEEN 12 AND 18);

-- 3
ALTER TABLE Article_Console
ADD copiesRestantes INT NOT NULL DEFAULT 0;

-- 4
ALTER TABLE Client_Article
DROP CONSTRAINT PK_Client_Article;

ALTER TABLE Client_Article
ADD CONSTRAINT PK_Client_Article PRIMARY KEY (pseudoClient, idArticle, dateAchat);