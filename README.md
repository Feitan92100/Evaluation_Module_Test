# Pokémon Management API 

Application Spring Boot consacrée aux tests des couches Repository, Service et Controller.
Projet autonome : l'exercice Poudlard situé dans le dossier voisin n'est pas modifié.

## Démarrage rapide

Prérequis : JDK 17 ou supérieur, avec `JAVA_HOME` configuré. Le projet a été vérifié avec JDK 17.
Le wrapper fourni télécharge Maven 3.9.11 ; une connexion est nécessaire au premier lancement.
Ouvrir **ce dossier** ou son `pom.xml` dans IntelliJ IDEA, puis importer le projet Maven.

Dans PowerShell, depuis ce dossier :

```powershell
.\mvnw.cmd clean install
java -jar target/pokemon-management-api-1.0.0.jar
```

Avec Maven déjà installé, `mvn clean install` est équivalent. Sous Linux/macOS : `sh mvnw clean install`.
Pour arrêter l'application : `Ctrl+C` dans son terminal.

Si Java 17 n'est pas sélectionné sur le poste actuel :

```powershell
$env:JAVA_HOME = 'chemin\vers\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

La base H2 de l'application utilise le fichier `data/pokemon.mv.db` et accepte la connexion IntelliJ
grâce à `AUTO_SERVER=TRUE`. Aucun MySQL ou Docker nécessaire. Lancer depuis la racine du projet
pour que le chemin relatif `./data/pokemon` pointe vers la base déjà configurée dans IntelliJ.

Hibernate recrée les tables au démarrage (`create-drop`), puis Spring Boot charge automatiquement
`src/main/resources/data.sql`. Les tables sont supprimées à l'arrêt normal. Cette remise à zéro
vient de la configuration Hibernate, pas du seul choix de H2.

Pour lancer directement depuis les sources :

```powershell
.\mvnw.cmd spring-boot:run
```
