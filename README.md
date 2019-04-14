# cyan_zip
zip module of cyan_potion game engine.

[![Build status](https://ci.appveyor.com/api/projects/status/wk3ojjock5dmcff4?svg=true)](https://ci.appveyor.com/project/XenoAmess/cyan-zip)

It is still in development, and every part of it is not fully tested, so it is not recommend to be used in your own project for now.

# done:
By now, we implemented an algorithm that is based on ranged encoding, the only difference is it used not the current proportion of some char to encode it, but use the predicted proportion of that char. the "predicted proportion" is calculated using the last 3 chars.

We also provided an InputStream class and an OutputStream class to make things easier.

You can see the implementation details in package com.xenoamess.cyan_zip.forecastingRangeEncoding

You can see the usecases in test folder.

# todo:

We will build a file format to zip several files into one data file, and every file can be random accessed.

When you want to package the resources, first you prepare two passwords, password A is reserved by yourself, and password B is put in all game copies and given to your players.

When you decide to encrypt your game resources, first you list them in a folder

Then you run a program provided to generate encrypted file names using password A.

Then the package program will encrypted the files using password B, and put them with encrypted filenames into a single file.

When running the game, the player will use the file names A to fetch the file content B, and use B to get the raw files.

And they will never know about password A, which means your resource file names are always safe.

That means even if they unpack your sources file, they will have no idea which file is what format, and they can only test them one by one.

