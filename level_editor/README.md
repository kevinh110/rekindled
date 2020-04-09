# ReKindle Level Editor

## Instructions

The editor is pretty straight forward to use, but here are some things to note:

### *Menu*

**'new'**: requires you to specify width and height. By default creates a border of walls, which are not included in the "walls" attribute of the resulting JSON.

**'open'**: must be of the JSON format that you get when you 'copy'. If the inputted JSON string has different indenting or new lines, it may not parse correctly.

**'copy'**: copies the resulting JSON string to your clipboard. Paste it onto a blank notepad (or similar) to see it.

### *Tools*

**'brush'**: allows you to occupy a single square on the grid with the selected letter.

**'fill'**: allows you to occupy the entire grid with the selected letter. Mainly useful when trying to empty the entire grid: select 'fill'+'.' and click anywhere on the grid to clear all.

**'Number Init Lights'**: specify the number of initial lights the player starts with. Must press the 'Apply' button for the specified value to be saved and reflected on the resulting JSON.

**'Number of Enemies'**: specify the number of enemies this level has. This is important because each enemy is labeled E1, E2, E3, ..., etc. and each have their wander path boxes @1, @2, @3, ..., etc. Click 'Apply' to see the specified number of enemy letters appear in the Enemies Palette just below.

### *When Setting Wander Paths*

Only select the squares that are the edges of the wander paths. For example, if enemy 1 (E1) has wander path of a straight line, then only two squares (at the two endpoints of the straight path) should be highlighted with '@1'. Do not fill up the entire length of the line with '@1' squares.

'E' and '@' squares cannot overlap at the same square: one will simply cover up the other square.

Note that if 'E' is missing, then the corresponding '@' squares will not be recorded in the resulting JSON. If 'E' is missing but the corresponding '@' squares exist on the grid, trying to copy the resulting JSON may throw errors.

In contrast, if 'E' exists on the grid but the corresponding '@' is missing, then the enemy is considered stationary and the resulting JSON simply stores an empty string for the "wander" attribute of the enemy. 

Put simply: a wander path without its owner is not possible but an enemy without a wander path is possible.

### *Bordered Walls*

As of now, all levels are assumed to have walls as border. Thus the bordering squares are not taken into account when creating the resulting JSON. This means whatever letter you put on the border squares, it will not show up in the JSON. For example, if you place the player 'P' on a border square, the "spawn" attribute in the JSON will end up with an empty string.

### *Refresh to Reset to Default Template*

If you wish to return to the default level, simply refresh.


---
## Credits

This JavaScript Level Editor is based off of code by misha-codes:
https://github.com/misha-codes/eloquent-javascript--level-editor

Icons used in this project are the open source material icons by Google, they can  
be found at https://material.io/icons/
