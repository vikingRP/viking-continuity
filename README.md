# Viking Continuity — Forge 1.20.1

Port natif Forge de [Continuity par PepperCode1](https://github.com/PepperCode1/Continuity), fork VikingRP, branche `viking/forge-1.20.1`. Base : `1.20.1/dev`, commit `e283f6e`. Licence **LGPL-3.0-only**, attribution et licence originales conservées.

Ce mod demande uniquement **Minecraft 1.20.1 et Forge 47.1 ou supérieur**. Il ne charge aucune Fabric API, Forgified Fabric API, Connector, Indium, Mod Menu ou bibliothèque de rendu supplémentaire. Java 17 est requis. Le modId reste `continuity` : retirer l’ancienne version de Continuity avant installation.

## Fonctionnalités

- Règles CTM OptiFine/Continuity : CTM à 47 tuiles, CTM compact, horizontal/vertical, top, fixe, aléatoire, répétition et superpositions.
- Textures émissives des modèles de blocs et d’objets, avec lumière maximale et sans ombrage ambiant sur les faces émissives.
- Couches de rendu définies par `optifine/block.properties`.
- Packs intégrés **Default Connected Textures** et **Glass Pane Culling Fix**, disponibles dans le menu des packs de ressources.
- Configuration dans `config/continuity.json`, accessible depuis le bouton de configuration Forge.

Les règles, processeurs et ressources de Continuity sont conservés. Le moteur de rendu interne convertit les faces Minecraft en données modifiables, applique ces processeurs, puis renvoie des `BakedQuad` Forge. Les modèles délèguent les propriétés et conservent le `ModelData` du modèle enveloppé, notamment pour les métiers à tisser de Conquest. Les listeners et les événements Forge remplacent ceux de Fabric. Les mixins utilisent le refmap généré pour les distributions SRG.

Ce port concerne Continuity. Un autre mod Fabric tel que Puzzle conserve ses propres dépendances, même lorsqu’il est utilisé avec ce Continuity natif.

## Construire et installer

```powershell
$env:JAVA_HOME='C:/chemin/vers/jdk-17'
./gradlew.bat build
```

Depuis le workspace : `./gradlew.bat :viking-continuity:build`.

Le JAR de production est `build/libs/viking-continuity-forge-1.20.1-3.0.1-viking.1.jar`. Le copier dans `mods/` du client, puis activer le pack de textures voulu. Le fichier `-sources.jar` contient les sources. Le fichier `-dev.jar` est réservé aux runs de développement ; le build le dépose automatiquement dans `../viking-laomod/run/mods/`.

Le profil complet `viking-conquest -PvisualDependencies` utilise ce `devJar` natif. Construire Continuity avant ce lancement. Les anciens téléchargements Continuity de Conquest restent des archives et sont exclus du profil Gradle.

## Vérifier

```powershell
py tools/prepare-smoke.py
./gradlew.bat --init-script tools/smoke.init.gradle runClient
```

Le harnais est séparé des distributions. Il lance Forge et Continuity sans API externe, active les packs de test, vérifie les **47 variantes CTM** du verre sur 256 voisinages, les émissifs des blocs et objets, CTM compact avec découpage des faces, horizontal/vertical, les règles combinées, fixe/aléatoire/répétition, les superpositions, leurs couches et teintes, et les couches de blocs personnalisées. Il répète les vérifications après un rechargement manuel puis ferme le client. Succès : `CONTINUITY_NATIVE_SMOKE_OK` et `CONTINUITY_NATIVE_METHODS_OK`, aux étapes `startup` et `reload`.

Validé en développement avec Forge 47.2.0, puis avec le **JAR reobfusqué sur Forge 47.4.10**, seul et avec Embeddium VikingRP 0.3.31 : toutes les vérifications passent au démarrage et après rechargement. Le profil complet Conquest passe également : 47 ciels, 384 variantes de métiers à tisser avec fils rouges, 108 faces dans le modèle témoin. Ce dernier profil utilise encore les dépendances Fabric propres à Puzzle.

Test du JAR de production depuis un cache Forge déjà installé :

```powershell
./gradlew.bat --init-script tools/smoke.init.gradle -PproductionSmoke reobfProductionSmokeJar
py tools/production-smoke.py --store C:/chemin/vers/cache/store
```

Le harnais de production est un JAR séparé, jamais déployé dans laomod. `--embeddium chemin/embeddium.jar` ajoute Embeddium au test. Ces tests vérifient le chargement et les modèles ; une validation visuelle en monde avec les shaders du profil reste utile.

Documentation des formats de packs : [wiki Continuity](https://github.com/PepperCode1/Continuity/wiki).
