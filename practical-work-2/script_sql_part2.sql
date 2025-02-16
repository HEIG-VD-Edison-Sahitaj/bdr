DROP TABLE IF EXISTS Client;
DROP TABLE IF EXISTS Article;
DROP TABLE IF EXISTS Article_MotsCles;
DROP TABLE IF EXISTS Client_Article;
DROP TABLE IF EXISTS Console;
DROP TABLE IF EXISTS Article_Console;
DROP TABLE IF EXISTS Fabricant;
DROP TABLE IF EXISTS DLC;
DROP TABLE IF EXISTS JeuVideo;
DROP TABLE IF EXISTS JeuVideo_Genre;
DROP TABLE IF EXISTS Genre;
DROP TABLE IF EXISTS Editeur;

CREATE TABLE Client (
    pseudo VARCHAR(80),
    dateNaissance DATE NOT NULL,
    adresseFacturation VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    CONSTRAINT PK_Client PRIMARY KEY (pseudo),
    CONSTRAINT UC_Client_email UNIQUE (email)
);

CREATE TABLE Article (
    id SERIAL,
    nom VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    dateSortie DATE,
    prix DECIMAL(10,2),
    note INTEGER,
    CONSTRAINT PK_Article PRIMARY KEY (id),
    CONSTRAINT CK_Article_dateSortie CHECK (dateSortie IS NULL OR prix IS NOT NULL),
    CONSTRAINT CK_Article_note CHECK (note IS NULL OR dateSortie <= CURRENT_DATE)
);

CREATE TABLE Article_MotsCles (
    articleId SERIAL,
    motCle VARCHAR(255),
    CONSTRAINT PK_Article_MotsCles PRIMARY KEY (articleId, motCle),
    CONSTRAINT FK_Article_MotsCles_articleId FOREIGN KEY (articleId) REFERENCES Article(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Client_Article (
    pseudoClient VARCHAR(80),
    idArticle SERIAL,
    dateAchat DATE NOT NULL,
    CONSTRAINT PK_Client_Article PRIMARY KEY (pseudoClient, idArticle),
    CONSTRAINT FK_Client_Article_pseudoClient FOREIGN KEY (pseudoClient) REFERENCES Client(pseudo)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_Client_Article_idArticle FOREIGN KEY (idArticle) REFERENCES Article(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Fabricant (
    nom VARCHAR(255),
    CONSTRAINT PK_Fabricant PRIMARY KEY (nom)
);

CREATE TABLE Console (
    nom VARCHAR(255) NOT NULL,
    anneeParution INTEGER NOT NULL,
    nomFabricant VARCHAR(255) NOT NULL,
    CONSTRAINT PK_Console PRIMARY KEY (nom),
    CONSTRAINT FK_Console_nomFabricant FOREIGN KEY (nomFabricant) REFERENCES Fabricant(nom)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE Article_Console (
    idArticle SERIAL,
    nomConsole VARCHAR(255) NOT NULL,
    CONSTRAINT PK_Article_Console PRIMARY KEY (idArticle, nomConsole),
    CONSTRAINT FK_Article_Console_idArticle FOREIGN KEY (idArticle) REFERENCES Article(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_Article_Console_nomConsole FOREIGN KEY (nomConsole) REFERENCES Console(nom)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Editeur (
    id SERIAL,
    nom VARCHAR(255),
    siegeSocial VARCHAR(255) NOT NULL,
    CONSTRAINT PK_Editeur PRIMARY KEY (id),
    CONSTRAINT UC_Editeur_nom UNIQUE (nom)
);

CREATE TABLE JeuVideo (
    idArticle SERIAL,
    idEditeur SERIAL,
    ageMinimum INTEGER NOT NULL,
    CONSTRAINT PK_JeuVideo PRIMARY KEY (idArticle),
    CONSTRAINT FK_JeuVideo_idArticle FOREIGN KEY (idArticle) REFERENCES Article(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_JeuVideo_idEditeur FOREIGN KEY (idEditeur) REFERENCES Editeur(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE DLC (
    idArticle SERIAL,
    idJeuVideo SERIAL,
    necessiteJeuDeBase BOOLEAN NOT NULL,
    CONSTRAINT PK_DLC PRIMARY KEY (idArticle),
    CONSTRAINT FK_DLC_idArticle FOREIGN KEY (idArticle) REFERENCES Article(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_DLC_idArticleJeuVideo FOREIGN KEY (idJeuVideo) REFERENCES JeuVideo(idArticle)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Genre (
    nom VARCHAR(255),
    CONSTRAINT PK_GENRE PRIMARY KEY (nom)
);

CREATE TABLE JeuVideo_Genre (
    idJeuVideo SERIAL,
    nomGenre VARCHAR(255),
    CONSTRAINT PK_JeuVideo_Genre PRIMARY KEY (idJeuVideo, nomGenre),
    CONSTRAINT FK_JeuVideo_Genre_idArticle FOREIGN KEY (idJeuVideo) REFERENCES JeuVideo(idArticle)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_JeuVideo_Genre_nomGenre FOREIGN KEY (nomGenre) REFERENCES GENRE(nom)
        ON DELETE CASCADE ON UPDATE CASCADE
);
