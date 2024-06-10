# CC: BX
This is a mod based on the Peripheral Tutorial made from [Sirendii](https://github.com/SirEndii/CCTutorial).


This is a fork I made to update [the mod originally by DxsSucuk](https://github.com/DxsSucuk/CCBX), which adds a peripheral which allows control over the missile silo from the [BallistX](https://www.curseforge.com/minecraft/mc-mods/ballistix) mod.

Here is a link to [the original mod on Curseforge](https://www.curseforge.com/minecraft/mc-mods/cc-ballistx).

Or you can grab a .jar of my fork [here](https://github.com/Thorium0/CCBX/releases/latest).


To interact with BallistiX Silos with a Computer you will need to place the Silo Controller below the center block of the Silo.
After that you can access the peripheral, via its name "siloController" (e.g. peripheral.find("siloController"), or using the wrap function to get it from a direction (e.g. peripheral.wrap("top").



The silo controller has these functions:

- launch() & launchWithPosition(int x, int y, int z)

- getFrequency() & setFrequency(int freq)

- getPosition() & setPosition(int x, int y, int z)

- getExplosiveType() & getExplosiveAmount()

- getMissileType() & getMissileAmount()

 
