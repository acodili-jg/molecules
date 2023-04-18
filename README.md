# Molecules

The Java application that COULD fry your computer!

## Hardware Requirements

 - Anything that can run Java 17

## Controls

 - `LMB` - deselects all molecules

 - `Shift` `LMB` - an inclusive selection including more molecules to the selection (Drag press)

 - `Shift` `Ctrl` `LMB` - an exclusive selection excluding selected molecules from selection (Drag
    press)

 - `Ctrl` `LMB` - adds a new molecule that is ready for acceleration

 - `Ctrl` `Alt` `LMB` - adds multiple nonmoving molecules without overlapping others (Supports drag
    press)

 - `RMB` - moves the world view (Drag press)

 - `M` - toggles accelerate molecules mode

 - `Alt` `M` - make selected molecules lose their current velocity

 - `Delete` - deletes the selected molecules

## Visuals

 - Yellow Outline - those molecules are selected

 - Magenta Outline - accelerate molecules mode was enabled

 - Red Line - the velocity of molecules

 - Text on the Top Right - debug information

 - Flashing When Moving Around - desync during the client view and server model.

    - When the mouse is pressed then dragged, the client translates the rendering of molecules.

    - When the mouse is released, the server is told to move all molecules as if visually
      translated and the client no longer needs to translate rendering

    - The flashing is minized by pausing rendering during syncs at mouse releases, though uncommon
      will still occur especially when the server is lagging
