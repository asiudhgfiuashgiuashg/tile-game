#!/bin/bash
shopt -s nullglob
ASE_DIRECTORY=~/tilegame/tile-game/art/tiles
PNG_DIRECTORY=~/tilegame/tile-game/core/assets/tileart/
for file in $ASE_DIRECTORY/*.ase
do
	echo "file: $file"
	NO_EXTENSION=$(basename "$file" .ase)
	echo "NO_EXTENSION: $NO_EXTENSION"
	SAVE_AS="$NO_EXTENSION.png"
	echo "saving as: $SAVE_AS"
	~/apps/aseprite/bin/aseprite --batch $file --save-as $SAVE_AS

	for pngFile in $ASE_DIRECTORY/*.png
	do
		mv $pngFile $PNG_DIRECTORY
	done
done

shopt -u nullglob #revert nullglob back to it's normal default state


