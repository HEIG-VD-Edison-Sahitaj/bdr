-- 1
CREATE TABLE Achat (
    idAchat SERIAL,
    pseudoClient VARCHAR(80) NOT NULL,
    idArticle SERIAL NOT NULL,
    nomConsole VARCHAR(255),
    dateAchat DATE NOT NULL,
    copiesRestantes INTEGER NOT NULL,
    CONSTRAINT PK_Achat PRIMARY KEY (idAchat),
    CONSTRAINT FK_Achat_Client FOREIGN KEY (pseudoClient) REFERENCES Client(pseudo)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_Achat_Article FOREIGN KEY (idArticle) REFERENCES Article(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_Achat_Console FOREIGN KEY (nomConsole) REFERENCES Console(nom)
    ON DELETE CASCADE ON UPDATE CASCADE
);

-- 2
SELECT copiesRestantes
FROM Achat
WHERE idArticle = 1 AND nomConsole = 'PS5' AND historique = TRUE AND copiesRestantes > 0;

-- 3
ALTER TABLE Achat
ADD COLUMN historique BOOLEAN DEFAULT FALSE;