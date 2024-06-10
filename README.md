# CC: BX
This is a mod based on the Peripheral Tutorial made from [Sirendii](https://github.com/SirEndii/CCTutorial).

It adds a peripheral which allows control over the Missile silo from the [BallistX](https://www.curseforge.com/minecraft/mc-mods/ballistix) mod.


To interact with BallistiX Silos with a Computer you will need to place the Silo Controller below the center block of the Silo.
After that you can access the peripheral, via its name "siloController" (e.g. peripheral.find("siloController"), or using the wrap function to get it from a direction (e.g. peripheral.wrap("top").



The silo controller has these functions:

- launch() & launchWithPosition(int x, int y, int z)

- getFrequency() & setFrequency(int freq)

- getPosition() & setPosition(int x, int y, int z)

- getExplosiveType() & getExplosiveAmount()

- getMissileType() & getMissileAmount()

 